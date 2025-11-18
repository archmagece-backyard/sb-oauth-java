# sb-oauth-java 아키텍처 가이드

sb-oauth-java의 내부 구조와 설계 철학을 설명합니다.

## 목차

1. [설계 철학](#설계-철학)
2. [전체 아키텍처](#전체-아키텍처)
3. [모듈 구조](#모듈-구조)
4. [핵심 인터페이스](#핵심-인터페이스)
5. [OAuth 플로우](#oauth-플로우)
6. [확장 포인트](#확장-포인트)
7. [성능 고려사항](#성능-고려사항)

---

## 설계 철학

### 1. Provider 특화 (Provider-Specific Design)

범용 OAuth 라이브러리와 달리, **각 OAuth 제공자의 특이사항을 명시적으로 모델링**합니다.

**Why?**
- OAuth 2.0 표준은 있지만, 실제 구현은 제공자마다 크게 다름
- Naver: Refresh Token 영구 유효
- Kakao: client_secret 선택적, Refresh Token 60일 만료
- Google: OIDC 지원, ID Token 제공
- Facebook: Graph API 버전 관리

**How?**
```java
// ❌ Generic (실수하기 쉬움)
OAuth2Service service = new GenericOAuth2Service(config);
TokenResponse token = service.getToken(code);

// ✅ Provider-specific (타입 안전, 명확한 의도)
OAuth2NaverAccesstokenFunction naverFunction = new OAuth2NaverAccesstokenFunction(...);
OAuth2NaverTokenRes token = naverFunction.issue(verifier, state);
// → 컴파일 타임에 Naver 특화 필드 확인 가능
```

### 2. 모듈화 (Modularity)

사용하지 않는 기능은 포함하지 않습니다.

**구조:**
```
oauth-client (핵심, 항상 필요)
  ↓
connector-* (선택: 사용할 Provider만)
  ↓
storage-* (선택: 환경에 맞는 저장소)
```

**장점:**
- 의존성 최소화
- JAR 크기 감소
- 보안 표면 축소

### 3. 인터페이스 기반 (Interface-Driven)

구현체를 쉽게 교체할 수 있도록 인터페이스를 우선합니다.

**핵심 인터페이스:**
- `OAuth2GenerateAuthorizeEndpointFunction` - 인증 URL 생성
- `OAuth2AccessTokenEndpointFunction` - 토큰 관리
- `OAuth2ResourceFunction` - API 호출
- `TokenStorage` - 토큰 저장
- `StateStorage` - State 저장
- `TokenExtractor` - 응답 파싱

### 4. 불변성 (Immutability)

설정 객체는 불변(Immutable)입니다.

```java
// ✅ Builder 패턴으로 생성, 이후 변경 불가
OAuth2NaverConfig config = OAuth2NaverConfig.builder()
    .clientId("CLIENT_ID")
    .clientSecret("CLIENT_SECRET")
    .build();

// config.setClientId("NEW_ID");  // ❌ 컴파일 에러 (setter 없음)
```

**장점:**
- Thread-safe
- 예측 가능한 동작
- 실수 방지

### 5. Fail-Fast

문제를 조기에 발견합니다.

```java
// ❌ 런타임에 실패
OAuth2NaverConfig config = new OAuth2NaverConfig(null, null, ...);
// → API 호출 시 NPE

// ✅ 생성 시 검증
OAuth2NaverConfig config = OAuth2NaverConfig.builder()
    .clientId(null)  // ❌ IllegalArgumentException 즉시 발생
    .build();
```

---

## 전체 아키텍처

### 계층 구조

```
┌─────────────────────────────────────────────────────────┐
│                   Application Layer                      │
│         (Spring Boot, Servlet, CLI 등)                   │
└─────────────────────────────────────────────────────────┘
                        ↓ 사용
┌─────────────────────────────────────────────────────────┐
│              OAuth Connector Layer                       │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐   │
│  │  Naver   │ │  Kakao   │ │  Google  │ │ Facebook │   │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘   │
└─────────────────────────────────────────────────────────┘
                        ↓ 의존
┌─────────────────────────────────────────────────────────┐
│                OAuth Client Core                         │
│  ┌───────────────────────────────────────────────────┐  │
│  │ Interfaces & Abstract Classes                     │  │
│  │  - OAuth2GenerateAuthorizeEndpointFunction       │  │
│  │  - OAuth2AccessTokenEndpointFunction             │  │
│  │  - OAuth2ResourceFunction                        │  │
│  └───────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
                        ↓ 사용
┌─────────────────────────────────────────────────────────┐
│               Storage Abstraction Layer                  │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐                │
│  │  Redis   │ │ Ehcache  │ │  Custom  │                │
│  └──────────┘ └──────────┘ └──────────┘                │
└─────────────────────────────────────────────────────────┘
                        ↓ 저장
┌─────────────────────────────────────────────────────────┐
│                   Data Layer                             │
│         (Redis, Ehcache, Database 등)                    │
└─────────────────────────────────────────────────────────┘
```

### 모듈 의존성 그래프

```
oauth-client (핵심)
    ↑
    ├─── oauth-connector-naver
    ├─── oauth-connector-kakao
    ├─── oauth-connector-google
    └─── oauth-connector-facebook

oauth-client (핵심)
    ↑
    ├─── oauth-storage-redis
    └─── oauth-storage-ehcache

oauth-client (핵심)
oauth-connector-* (Provider)
oauth-storage-* (Storage)
    ↑
    └─── oauth-integration-spring-boot
```

**규칙:**
- Connector는 Client에만 의존
- Storage는 Client에만 의존
- Connector와 Storage는 서로 독립적
- Integration은 모든 모듈을 통합

---

## 모듈 구조

### oauth-client (핵심 모듈)

**역할**: 공통 인터페이스, 추상 클래스, 유틸리티 제공

**주요 패키지:**
```
org.scriptonbasestar.oauth.client/
├── config/
│   └── OAuthBaseConfig                    # 기본 설정 (clientId, clientSecret)
├── model/
│   ├── State                               # State 값 래퍼
│   ├── Verifier                            # Authorization Code 래퍼
│   └── Token                               # Token 값 래퍼
├── nobi/                                   # "No Boilerplate Interface"
│   ├── TokenStorage                        # 토큰 저장소 인터페이스
│   ├── StateStorage                        # State 저장소 인터페이스
│   ├── LocalTokenStorage                   # 로컬 메모리 구현
│   ├── token/
│   │   ├── TokenExtractor                  # 토큰 파싱 인터페이스
│   │   └── JsonTokenExtractor              # JSON 파싱 구현
│   └── state/
│       ├── StateGenerator                  # State 생성 인터페이스
│       └── RandomStringStateGenerator      # 랜덤 문자열 구현
├── exception/
│   ├── OAuthException                      # 최상위 예외
│   ├── OAuthNetworkException               # 네트워크 에러
│   ├── OAuthAuthException                  # 인증 에러
│   └── OAuthParsingException               # 파싱 에러
└── OAuth2*Function                         # 핵심 인터페이스들
```

### oauth-connector-naver

**역할**: Naver OAuth 2.0 구현

**구조:**
```
org.scripton.oauth.connector.naver/
├── OAuth2NaverConfig                       # Naver 설정 (Builder 패턴)
├── OAuth2NaverAuthFunction                 # 인증 URL 생성
├── OAuth2NaverAccesstokenFunction          # 토큰 발급/갱신
├── OAuth2NaverTokenRes                     # Naver 토큰 응답 (access_token, refresh_token)
└── OAuth2NaverCallbackRes                  # Naver 콜백 응답
```

**특징:**
- `client_secret` 필수
- `refresh_token`은 영구 유효, 갱신 시 변경 안 됨
- Scope: `profile`, `email` 등

### oauth-connector-kakao

**역할**: Kakao OAuth 2.0 구현

**구조:**
```
org.scripton.oauth.connector.kakao/
├── OAuth2KakaoConfig                       # Kakao 설정
├── OAuth2KakaoAuthFunction                 # 인증 URL 생성
├── OAuth2KakaoAccessTokenFunction          # 토큰 발급/갱신/취소
├── OAuth2KakaoTokenRes                     # Kakao 토큰 응답
└── OAuth2KakaoCallbackRes                  # Kakao 콜백 응답
```

**특징:**
- `client_secret` 선택적 (Admin Key 권장)
- `refresh_token`은 60일 만료, 갱신 시 새 토큰 발급
- Scope: 공백 구분

### oauth-storage-redis

**역할**: Redis 기반 분산 토큰 저장소

**구조:**
```
org.scripton.oauth.storage.redis/
├── RedisTokenStorage                       # TokenStorage 구현
│   ├── store(id, token)                    # 토큰 저장
│   ├── load(id)                            # 토큰 조회
│   └── drop(id)                            # 토큰 삭제
└── RedisStateStorage                       # StateStorage 구현
```

**저장 형식:**
```
Key: oauth:token:{userId}:access_token
Value: {access_token_value}

Key: oauth:token:{userId}:refresh_token
Value: {refresh_token_value}

Key: oauth:state:{stateValue}
Value: {state_metadata}
TTL: 600초 (10분)
```

---

## 핵심 인터페이스

### OAuth2GenerateAuthorizeEndpointFunction

**역할**: OAuth 인증 URL 생성

```java
public interface OAuth2GenerateAuthorizeEndpointFunction {
    /**
     * OAuth 인증 URL 생성
     * @param state CSRF 방지용 State
     * @return 인증 URL (사용자를 리다이렉트할 URL)
     */
    String generate(State state);
}
```

**구현 예시 (Naver):**
```java
public class OAuth2NaverAuthFunction
    implements OAuth2GenerateAuthorizeEndpointFunction {

    private final OAuth2NaverConfig config;

    @Override
    public String generate(State state) {
        return config.getAuthorizeEndpoint() +
            "?response_type=code" +
            "&client_id=" + config.getClientId() +
            "&redirect_uri=" + urlEncode(config.getRedirectUri()) +
            "&state=" + state.getValue() +
            "&scope=" + config.getScope();
    }
}
```

### OAuth2AccessTokenEndpointFunction

**역할**: 토큰 발급, 갱신, 취소

```java
public interface OAuth2AccessTokenEndpointFunction<TOKEN_RES> {
    /**
     * Authorization Code로 토큰 발급
     * @param verifier Authorization Code
     * @param state CSRF 검증용 State
     * @return 토큰 응답 (Provider별 타입)
     */
    TOKEN_RES issue(Verifier verifier, State state);

    /**
     * Refresh Token으로 Access Token 갱신
     * @param refreshToken Refresh Token
     * @return 새로운 토큰 응답
     */
    TOKEN_RES refresh(Token refreshToken);

    /**
     * 토큰 취소 (Provider가 지원하는 경우만)
     * @param accessToken Access Token
     * @return 취소 응답
     */
    TOKEN_RES revoke(Token accessToken);
}
```

**제네릭 타입 `TOKEN_RES`:**
- `OAuth2NaverTokenRes` - Naver 응답
- `OAuth2KakaoTokenRes` - Kakao 응답
- `OAuth2GoogleTokenRes` - Google 응답
- Provider별 필드가 다르므로 타입 안전성 확보

### OAuth2ResourceFunction

**역할**: Access Token으로 OAuth Provider API 호출

```java
public interface OAuth2ResourceFunction<RESOURCE> {
    /**
     * OAuth API 호출
     * @param accessToken Access Token
     * @return API 응답
     */
    RESOURCE fetch(String accessToken);
}
```

**구현 예시:**
```java
public class DefaultOAuth2ResourceFunction
    implements OAuth2ResourceFunction<String> {

    private final String resourceUrl;

    @Override
    public String fetch(String accessToken) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(resourceUrl))
            .header("Authorization", "Bearer " + accessToken)
            .GET()
            .build();

        HttpResponse<String> response = client.send(request,
            HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new OAuthAuthException("API call failed: " + response.statusCode());
        }

        return response.body();
    }
}
```

### TokenStorage

**역할**: 토큰 영속화 추상화

```java
public interface TokenStorage {
    /**
     * 토큰 저장
     * @param id 토큰 식별자 (예: "user123:access_token")
     * @param token 토큰 객체
     */
    void store(String id, Token token);

    /**
     * 토큰 조회
     * @param id 토큰 식별자
     * @return 토큰 객체 (없으면 null)
     */
    Token load(String id);

    /**
     * 토큰 삭제
     * @param id 토큰 식별자
     */
    void drop(String id);
}
```

**구현체:**
- `LocalTokenStorage` - ConcurrentHashMap 기반
- `RedisTokenStorage` - Redis 기반
- `EhcacheTokenStorage` - Ehcache 기반
- 커스텀 구현 가능 (JPA, MongoDB 등)

---

## OAuth 플로우

### 전체 시퀀스 다이어그램

```
┌──────┐         ┌─────────┐         ┌──────────┐         ┌─────────┐
│ User │         │   App   │         │sb-oauth-│         │  Naver  │
│      │         │         │         │  java   │         │  OAuth  │
└──┬───┘         └────┬────┘         └────┬─────┘         └────┬────┘
   │                  │                   │                    │
   │ 1. 로그인 클릭   │                   │                    │
   ├─────────────────>│                   │                    │
   │                  │                   │                    │
   │                  │ 2. generate()     │                    │
   │                  ├──────────────────>│                    │
   │                  │ OAuth URL 생성    │                    │
   │                  │<──────────────────┤                    │
   │                  │                   │                    │
   │ 3. Redirect to   │                   │                    │
   │    Naver OAuth   │                   │                    │
   │<─────────────────┤                   │                    │
   │                  │                   │                    │
   │ 4. 로그인 & 동의 │                   │                    │
   ├──────────────────┼───────────────────┼───────────────────>│
   │                  │                   │                    │
   │ 5. Redirect back │                   │                    │
   │    with code     │                   │                    │
   │<─────────────────┼───────────────────┼────────────────────┤
   │                  │                   │                    │
   │ 6. code & state  │                   │                    │
   ├─────────────────>│                   │                    │
   │                  │                   │                    │
   │                  │ 7. issue(code)    │                    │
   │                  ├──────────────────>│                    │
   │                  │                   │ 8. Token Request   │
   │                  │                   ├───────────────────>│
   │                  │                   │ 9. Token Response  │
   │                  │                   │<───────────────────┤
   │                  │ 10. TokenRes      │                    │
   │                  │<──────────────────┤                    │
   │                  │                   │                    │
   │                  │ 11. store(token)  │                    │
   │                  ├──────────────────>│                    │
   │                  │     (TokenStorage)│                    │
   │                  │                   │                    │
   │ 12. Login Success│                   │                    │
   │<─────────────────┤                   │                    │
```

### 코드 레벨 플로우

#### 1단계: 인증 URL 생성

```java
// App에서 실행
State state = new RandomStringStateGenerator().generate("NAVER");
session.setAttribute("oauth_state", state.getValue());

OAuth2NaverAuthFunction authFunction = new OAuth2NaverAuthFunction(config);
String authUrl = authFunction.generate(state);

// authUrl 내부 구조:
// https://nid.naver.com/oauth2.0/authorize
//   ?response_type=code
//   &client_id=YOUR_CLIENT_ID
//   &redirect_uri=http://localhost:8080/callback
//   &state=NAVER_abc123
//   &scope=profile,email
```

#### 2단계: 사용자 인증 (Naver에서 처리)

사용자가 Naver에 로그인하고 권한을 승인하면, Naver는 사용자를 `redirect_uri`로 리다이렉트합니다:

```
http://localhost:8080/callback?code=AUTH_CODE&state=NAVER_abc123
```

#### 3단계: 토큰 교환

```java
// App에서 실행
@GetMapping("/callback")
public String callback(
    @RequestParam("code") String code,
    @RequestParam("state") String stateValue,
    HttpSession session
) {
    // State 검증
    String savedState = (String) session.getAttribute("oauth_state");
    if (!stateValue.equals(savedState)) {
        throw new IllegalStateException("Invalid state");
    }

    // 토큰 교환
    Verifier verifier = new Verifier(code);
    State state = new State(stateValue);

    OAuth2NaverAccesstokenFunction tokenFunction = ...; // Bean 주입
    OAuth2NaverTokenRes tokenRes = tokenFunction.issue(verifier, state);

    // tokenFunction.issue() 내부에서 일어나는 일:
    // 1. HTTP POST to https://nid.naver.com/oauth2.0/token
    //    grant_type=authorization_code
    //    code=AUTH_CODE
    //    client_id=...
    //    client_secret=...
    //    redirect_uri=...
    //
    // 2. Naver 응답:
    //    {
    //      "access_token": "AAAAQoQ...",
    //      "refresh_token": "c8ceMEJi...",
    //      "token_type": "bearer",
    //      "expires_in": 3600
    //    }
    //
    // 3. TokenExtractor로 JSON → OAuth2NaverTokenRes 변환
    // 4. TokenStorage에 저장

    session.setAttribute("access_token", tokenRes.getAccess_token());
    return "redirect:/home";
}
```

#### 4단계: API 호출

```java
@GetMapping("/profile")
public String getProfile(HttpSession session) {
    String accessToken = (String) session.getAttribute("access_token");

    OAuth2ResourceFunction<String> resourceFunction =
        new DefaultOAuth2ResourceFunction("https://openapi.naver.com/v1/nid/me");

    String profileJson = resourceFunction.fetch(accessToken);

    // resourceFunction.fetch() 내부:
    // 1. HTTP GET to https://openapi.naver.com/v1/nid/me
    //    Header: Authorization: Bearer AAAAQoQ...
    //
    // 2. Naver 응답:
    //    {
    //      "resultcode": "00",
    //      "message": "success",
    //      "response": {
    //        "id": "1234567890",
    //        "nickname": "홍길동",
    //        "email": "user@naver.com"
    //      }
    //    }

    return profileJson;
}
```

---

## 확장 포인트

### 1. 커스텀 OAuth Provider 추가

새로운 OAuth Provider (예: LINE, PAYCO)를 추가하려면:

**Step 1**: Config 클래스 작성
```java
@Getter
public class OAuth2LineConfig extends OAuthBaseConfig {
    private final String redirectUri;
    private final String authorizeEndpoint;
    private final String scope;
    private final String accessTokenEndpoint;
    private final OAuthHttpVerb accessTokenVerb;

    public OAuth2LineConfig(
        String clientId,
        String clientSecret,
        String redirectUri,
        String scope
    ) {
        super(clientId, clientSecret);
        this.redirectUri = redirectUri;
        this.authorizeEndpoint = "https://access.line.me/oauth2/v2.1/authorize";
        this.scope = scope;
        this.accessTokenEndpoint = "https://api.line.me/oauth2/v2.1/token";
        this.accessTokenVerb = OAuthHttpVerb.POST;
    }
}
```

**Step 2**: TokenRes 클래스 작성
```java
@Getter
@Setter
public class OAuth2LineTokenRes {
    private String access_token;
    private Integer expires_in;
    private String id_token;        // LINE은 OIDC 지원
    private String refresh_token;
    private String scope;
    private String token_type;
}
```

**Step 3**: AuthFunction 구현
```java
public class OAuth2LineAuthFunction
    implements OAuth2GenerateAuthorizeEndpointFunction {

    private final OAuth2LineConfig config;

    @Override
    public String generate(State state) {
        return config.getAuthorizeEndpoint() +
            "?response_type=code" +
            "&client_id=" + config.getClientId() +
            "&redirect_uri=" + urlEncode(config.getRedirectUri()) +
            "&state=" + state.getValue() +
            "&scope=" + config.getScope();
    }
}
```

**Step 4**: TokenFunction 구현
```java
public class OAuth2LineAccessTokenFunction
    implements OAuth2AccessTokenEndpointFunction<OAuth2LineTokenRes> {

    private final OAuth2LineConfig config;
    private final TokenExtractor<OAuth2LineTokenRes> extractor;
    private final TokenStorage storage;

    @Override
    public OAuth2LineTokenRes issue(Verifier verifier, State state) {
        // HTTP POST to LINE token endpoint
        // Parse response → OAuth2LineTokenRes
        // Store to TokenStorage
        // Return result
    }

    @Override
    public OAuth2LineTokenRes refresh(Token refreshToken) {
        // Similar implementation
    }

    @Override
    public OAuth2LineTokenRes revoke(Token accessToken) {
        // LINE may not support revocation
        throw new UnsupportedOperationException("LINE does not support token revocation");
    }
}
```

### 2. 커스텀 TokenStorage 구현

예: MongoDB 기반 저장소

```java
public class MongoTokenStorage implements TokenStorage {

    private final MongoTemplate mongoTemplate;

    @Override
    public void store(String id, Token token) {
        Document doc = new Document()
            .append("_id", id)
            .append("value", token.getValue())
            .append("createdAt", new Date());

        mongoTemplate.save(doc, "oauth_tokens");
    }

    @Override
    public Token load(String id) {
        Document doc = mongoTemplate.findById(id, Document.class, "oauth_tokens");
        if (doc == null) {
            return null;
        }
        return new Token(doc.getString("value"));
    }

    @Override
    public void drop(String id) {
        mongoTemplate.remove(Query.query(Criteria.where("_id").is(id)), "oauth_tokens");
    }
}
```

### 3. 커스텀 TokenExtractor 구현

예: XML 응답 파싱

```java
public class XmlTokenExtractor<T> implements TokenExtractor<T> {

    private final Class<T> type;

    @Override
    public T extract(String responseBody) throws OAuthParsingException {
        try {
            JAXBContext context = JAXBContext.newInstance(type);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StringReader reader = new StringReader(responseBody);
            return (T) unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            throw new OAuthParsingException("Failed to parse XML response", e);
        }
    }
}
```

---

## 성능 고려사항

### 1. HTTP 연결 재사용

```java
// ❌ 매번 새로운 HttpClient 생성 (비효율적)
public String fetch(String accessToken) {
    HttpClient client = HttpClient.newHttpClient();  // 매번 생성
    // ...
}

// ✅ HttpClient 재사용 (권장)
public class DefaultOAuth2ResourceFunction {
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)
        .connectTimeout(Duration.ofSeconds(10))
        .build();

    @Override
    public String fetch(String accessToken) {
        // HTTP_CLIENT 재사용
    }
}
```

### 2. 토큰 캐싱

```java
@Component
public class CachedTokenService {

    @Autowired
    private OAuth2NaverAccesstokenFunction tokenFunction;

    private final Map<String, CachedToken> cache = new ConcurrentHashMap<>();

    public String getAccessToken(String userId) {
        CachedToken cached = cache.get(userId);

        // 캐시 히트 & 유효한 토큰
        if (cached != null && !cached.isExpired()) {
            return cached.getAccessToken();
        }

        // 캐시 미스 또는 만료 → DB에서 조회 & 갱신
        OAuth2NaverTokenRes token = loadFromDB(userId);
        if (token.isExpired()) {
            token = tokenFunction.refresh(new Token(token.getRefresh_token()));
            saveToDb(userId, token);
        }

        // 캐시 업데이트
        cache.put(userId, new CachedToken(token));
        return token.getAccess_token();
    }
}
```

### 3. Connection Pooling (Redis)

```java
// ✅ Connection Pool 사용
JedisPoolConfig poolConfig = new JedisPoolConfig();
poolConfig.setMaxTotal(50);         // 최대 50개 연결
poolConfig.setMaxIdle(10);          // 유휴 연결 10개 유지
poolConfig.setMinIdle(5);           // 최소 5개 연결
poolConfig.setTestOnBorrow(true);   // 연결 테스트

JedisPool jedisPool = new JedisPool(poolConfig, "localhost", 6379);
TokenStorage storage = new RedisTokenStorage(jedisPool);
```

### 4. Batch API 호출

여러 사용자의 프로필을 조회할 때:

```java
// ❌ 순차 호출 (느림)
for (String userId : userIds) {
    String profile = resourceFunction.fetch(getAccessToken(userId));
    // 100명이면 100번 HTTP 요청
}

// ✅ 병렬 호출
List<CompletableFuture<String>> futures = userIds.stream()
    .map(userId -> CompletableFuture.supplyAsync(() ->
        resourceFunction.fetch(getAccessToken(userId))
    ))
    .collect(Collectors.toList());

List<String> profiles = futures.stream()
    .map(CompletableFuture::join)
    .collect(Collectors.toList());
```

---

## 보안 고려사항

### 1. State 검증 (CSRF 방지)

```java
// ✅ 반드시 State 검증
String savedState = session.getAttribute("oauth_state");
if (!receivedState.equals(savedState)) {
    throw new SecurityException("CSRF attack detected");
}
```

### 2. Redirect URI 검증

```java
// ✅ Redirect URI는 OAuth Provider에 등록된 것만 사용
// ❌ 사용자 입력을 redirect_uri로 사용 금지
String redirectUri = request.getParameter("redirect_uri");  // 위험!
```

### 3. Client Secret 보호

```java
// ✅ 환경 변수 사용
String clientSecret = System.getenv("NAVER_CLIENT_SECRET");

// ❌ 코드에 하드코딩 금지
String clientSecret = "abc123...";  // 위험!
```

### 4. 토큰 암호화

```java
// Redis 저장 시 암호화
public class EncryptedRedisTokenStorage implements TokenStorage {

    private final RedisTokenStorage delegate;
    private final Cipher cipher;

    @Override
    public void store(String id, Token token) {
        String encrypted = encrypt(token.getValue());
        delegate.store(id, new Token(encrypted));
    }

    @Override
    public Token load(String id) {
        Token encrypted = delegate.load(id);
        String decrypted = decrypt(encrypted.getValue());
        return new Token(decrypted);
    }
}
```

---

## 추가 리소스

- [사용자 가이드](USER_GUIDE.md) - 기본 사용법
- [마이그레이션 가이드](MIGRATION.md) - 다른 라이브러리에서 전환
- [FAQ](FAQ.md) - 자주 묻는 질문
- [기여 가이드](../CONTRIBUTING.md) - 코드 기여 방법

---

**질문이나 제안이 있으신가요?**
GitHub Issues에 남겨주세요!
