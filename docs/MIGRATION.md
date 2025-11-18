# 마이그레이션 가이드

다른 OAuth 라이브러리에서 sb-oauth-java로 마이그레이션하는 방법을 안내합니다.

## 목차

1. [라이브러리 비교](#라이브러리-비교)
2. [Spring Security OAuth에서 마이그레이션](#spring-security-oauth에서-마이그레이션)
3. [ScribeJava에서 마이그레이션](#scribejava에서-마이그레이션)
4. [주요 개념 매핑](#주요-개념-매핑)
5. [마이그레이션 체크리스트](#마이그레이션-체크리스트)

---

## 라이브러리 비교

### sb-oauth-java vs 다른 라이브러리

| 특징 | sb-oauth-java | Spring Security OAuth | ScribeJava | Pac4j |
|------|---------------|----------------------|------------|-------|
| **현재 유지보수** | ✅ 활발 | ❌ EOL (2022) | ✅ 활발 | ✅ 활발 |
| **Java 버전** | 21+ | 8+ | 8+ | 11+ |
| **한국 OAuth 특화** | ✅ Yes (Naver, Kakao) | ❌ No | ⚠️ Plugin | ⚠️ Plugin |
| **Spring 의존성** | ❌ 선택적 | ✅ 필수 | ❌ 없음 | ❌ 없음 |
| **모듈화** | ✅ 우수 | ⚠️ 단일 | ⚠️ 단일 | ✅ 우수 |
| **토큰 저장소** | Redis, Ehcache | 직접 구현 | 직접 구현 | 직접 구현 |
| **Provider 특화** | ✅ 우수 | ❌ 일반적 | ⚠️ 보통 | ⚠️ 보통 |
| **타입 안정성** | ✅ 강타입 | ⚠️ 보통 | ⚠️ 보통 | ⚠️ 보통 |
| **설정 복잡도** | ⭐⭐ 간단 | ⭐⭐⭐⭐⭐ 복잡 | ⭐⭐⭐ 보통 | ⭐⭐⭐⭐ 복잡 |

**선택 가이드:**
- **한국 서비스 (Naver, Kakao 중심)**: sb-oauth-java ✅
- **글로벌 서비스만**: ScribeJava 또는 Pac4j
- **Spring Security 통합 필요**: Spring Authorization Server (OAuth 서버 구축)
- **레거시 Spring**: Spring Security OAuth (유지보수 모드)

---

## Spring Security OAuth에서 마이그레이션

### 배경

Spring Security OAuth는 2022년에 EOL(End of Life)되어 더 이상 유지보수되지 않습니다. Spring 팀은 Spring Authorization Server를 권장하지만, 이는 OAuth **서버** 구축용입니다. OAuth **클라이언트**로 사용하려면 대안이 필요합니다.

### 주요 차이점

| Spring Security OAuth | sb-oauth-java |
|----------------------|---------------|
| `@EnableOAuth2Client` | 직접 Bean 설정 또는 Auto-configuration |
| `OAuth2RestTemplate` | `OAuth2ResourceFunction` + HTTP Client |
| `OAuth2ClientContext` | `TokenStorage` 인터페이스 |
| `OAuth2ProtectedResourceDetails` | `OAuth2*Config` (Provider별) |
| `UserInfoTokenServices` | `OAuth2ResourceFunction` |

### 마이그레이션 단계

#### Before: Spring Security OAuth

**의존성:**
```xml
<dependency>
    <groupId>org.springframework.security.oauth</groupId>
    <artifactId>spring-security-oauth2</artifactId>
    <version>2.5.2.RELEASE</version>
</dependency>
```

**설정:**
```java
@Configuration
@EnableOAuth2Client
public class OAuth2Config {

    @Bean
    public OAuth2ProtectedResourceDetails naver() {
        AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();
        details.setClientId("CLIENT_ID");
        details.setClientSecret("CLIENT_SECRET");
        details.setAccessTokenUri("https://nid.naver.com/oauth2.0/token");
        details.setUserAuthorizationUri("https://nid.naver.com/oauth2.0/authorize");
        details.setScope(Arrays.asList("profile", "email"));
        details.setPreEstablishedRedirectUri("http://localhost:8080/callback");
        return details;
    }

    @Bean
    public OAuth2RestTemplate oauth2RestTemplate(
        OAuth2ClientContext context,
        OAuth2ProtectedResourceDetails details
    ) {
        return new OAuth2RestTemplate(details, context);
    }
}
```

**사용:**
```java
@Autowired
private OAuth2RestTemplate restTemplate;

public String getUserProfile() {
    return restTemplate.getForObject(
        "https://openapi.naver.com/v1/nid/me",
        String.class
    );
}
```

#### After: sb-oauth-java

**의존성:**
```xml
<dependency>
    <groupId>org.scriptonbasestar.oauth</groupId>
    <artifactId>oauth-connector-naver</artifactId>
    <version>sb-oauth-20181219-3-DEV</version>
</dependency>
<dependency>
    <groupId>org.scriptonbasestar.oauth</groupId>
    <artifactId>oauth-storage-redis</artifactId>
    <version>sb-oauth-20181219-3-DEV</version>
</dependency>
```

**설정:**
```java
@Configuration
public class OAuth2Config {

    @Bean
    public OAuth2NaverConfig naverConfig(
        @Value("${oauth.naver.client-id}") String clientId,
        @Value("${oauth.naver.client-secret}") String clientSecret,
        @Value("${oauth.naver.redirect-uri}") String redirectUri
    ) {
        return OAuth2NaverConfig.builder()
            .clientId(clientId)
            .clientSecret(clientSecret)
            .redirectUri(redirectUri)
            .scope("profile,email")
            .build();
    }

    @Bean
    public TokenStorage tokenStorage(JedisPool jedisPool) {
        return new RedisTokenStorage(jedisPool);
    }

    @Bean
    public OAuth2NaverAccesstokenFunction naverTokenFunction(
        OAuth2NaverConfig config,
        TokenStorage storage
    ) {
        TokenExtractor<OAuth2NaverTokenRes> extractor =
            new JsonTokenExtractor<>(new TypeReference<OAuth2NaverTokenRes>() {});
        return new OAuth2NaverAccesstokenFunction(config, extractor, storage);
    }

    @Bean
    public OAuth2NaverAuthFunction naverAuthFunction(OAuth2NaverConfig config) {
        return new OAuth2NaverAuthFunction(config);
    }
}
```

**사용:**
```java
@Autowired
private OAuth2NaverAccesstokenFunction tokenFunction;

@Autowired
private OAuth2NaverAuthFunction authFunction;

// 1. 인증 URL 생성
public String getAuthUrl(HttpSession session) {
    State state = new RandomStringStateGenerator().generate("NAVER");
    session.setAttribute("oauth_state", state.getValue());
    return authFunction.generate(state);
}

// 2. 콜백 처리
public OAuth2NaverTokenRes handleCallback(String code, String state) {
    Verifier verifier = new Verifier(code);
    State stateObj = new State(state);
    return tokenFunction.issue(verifier, stateObj);
}

// 3. 사용자 프로필 조회
public String getUserProfile(String accessToken) {
    OAuth2ResourceFunction<String> resourceFunction =
        new DefaultOAuth2ResourceFunction("https://openapi.naver.com/v1/nid/me");
    return resourceFunction.fetch(accessToken);
}
```

### 매핑 테이블

| Spring Security OAuth | sb-oauth-java | 설명 |
|----------------------|---------------|------|
| `OAuth2ProtectedResourceDetails` | `OAuth2NaverConfig` | OAuth 설정 |
| `OAuth2RestTemplate.getAccessToken()` | `tokenFunction.issue()` | 토큰 발급 |
| `OAuth2RestTemplate.getForObject()` | `resourceFunction.fetch()` | API 호출 |
| `OAuth2ClientContext` | `TokenStorage` | 토큰 저장 |
| `ResourceServerTokenServices` | `TokenExtractor` | 토큰 파싱 |
| `@EnableOAuth2Client` | `@Configuration` + Beans | 설정 활성화 |

---

## ScribeJava에서 마이그레이션

### 배경

ScribeJava는 범용 OAuth 라이브러리이지만, 한국 OAuth 제공자(Naver, Kakao)의 특이사항을 처리하려면 추가 작업이 필요합니다.

### 주요 차이점

| ScribeJava | sb-oauth-java |
|-----------|---------------|
| `ServiceBuilder` | `OAuth2*Config.builder()` |
| `OAuth20Service` | `OAuth2*AuthFunction` + `OAuth2*AccesstokenFunction` |
| `OAuth2AccessToken` | `OAuth2*TokenRes` (타입 안전) |
| `OAuthRequest` | `OAuth2ResourceFunction` |
| Generic API | Provider-specific API |

### 마이그레이션 단계

#### Before: ScribeJava

**의존성:**
```xml
<dependency>
    <groupId>com.github.scribejava</groupId>
    <artifactId>scribejava-core</artifactId>
    <version>8.3.3</version>
</dependency>
```

**코드:**
```java
// 1. 서비스 생성
OAuth20Service service = new ServiceBuilder("CLIENT_ID")
    .apiSecret("CLIENT_SECRET")
    .defaultScope("profile email")
    .callback("http://localhost:8080/callback")
    .build(NaverApi.instance());

// 2. 인증 URL 생성
String state = "random_string";
String authUrl = service.getAuthorizationUrl(state);

// 3. 토큰 교환
OAuth2AccessToken accessToken = service.getAccessToken(code);

// 4. API 호출
OAuthRequest request = new OAuthRequest(
    Verb.GET,
    "https://openapi.naver.com/v1/nid/me"
);
service.signRequest(accessToken, request);
Response response = service.execute(request);
String profile = response.getBody();
```

#### After: sb-oauth-java

**의존성:**
```xml
<dependency>
    <groupId>org.scriptonbasestar.oauth</groupId>
    <artifactId>oauth-connector-naver</artifactId>
    <version>sb-oauth-20181219-3-DEV</version>
</dependency>
```

**코드:**
```java
// 1. 설정
OAuth2NaverConfig config = OAuth2NaverConfig.builder()
    .clientId("CLIENT_ID")
    .clientSecret("CLIENT_SECRET")
    .redirectUri("http://localhost:8080/callback")
    .scope("profile,email")
    .build();

// 2. 인증 URL 생성
State state = new RandomStringStateGenerator().generate("NAVER");
OAuth2NaverAuthFunction authFunction = new OAuth2NaverAuthFunction(config);
String authUrl = authFunction.generate(state);

// 3. 토큰 교환
TokenExtractor<OAuth2NaverTokenRes> extractor =
    new JsonTokenExtractor<>(new TypeReference<OAuth2NaverTokenRes>() {});
TokenStorage storage = new LocalTokenStorage();

OAuth2NaverAccesstokenFunction tokenFunction =
    new OAuth2NaverAccesstokenFunction(config, extractor, storage);

OAuth2NaverTokenRes tokenRes = tokenFunction.issue(new Verifier(code), state);

// 4. API 호출
OAuth2ResourceFunction<String> resourceFunction =
    new DefaultOAuth2ResourceFunction("https://openapi.naver.com/v1/nid/me");
String profile = resourceFunction.fetch(tokenRes.getAccess_token());
```

### 장점 비교

**ScribeJava의 단점:**
```java
// ❌ Generic하게 처리하므로 Provider별 특이사항 놓치기 쉬움
OAuth2AccessToken token = service.getAccessToken(code);
// Naver의 경우 refresh_token이 영구 유효한데, 이런 정보가 없음

// ❌ 타입 안정성 부족
String accessToken = token.getAccessToken();  // 단순 String
Integer expiresIn = token.getExpiresIn();     // Naver는 null 가능
```

**sb-oauth-java의 장점:**
```java
// ✅ Provider별 특화된 응답 타입
OAuth2NaverTokenRes tokenRes = tokenFunction.issue(verifier, state);

// ✅ 타입 안전
String accessToken = tokenRes.getAccess_token();     // Never null
String refreshToken = tokenRes.getRefresh_token();   // Never null
Integer expiresIn = tokenRes.getExpires_in();        // 3600 (1 hour)

// ✅ Provider별 주의사항이 문서화됨
// Naver: refresh_token은 영구 유효, 갱신 시 변경되지 않음
```

---

## 주요 개념 매핑

### 용어 대조표

| 일반 OAuth 용어 | Spring Security OAuth | ScribeJava | sb-oauth-java |
|---------------|----------------------|------------|---------------|
| Client ID | `clientId` | `apiKey` | `clientId` |
| Client Secret | `clientSecret` | `apiSecret` | `clientSecret` |
| Authorization Endpoint | `userAuthorizationUri` | `authorizationBaseUrl` | `authorizeEndpoint` |
| Token Endpoint | `accessTokenUri` | `accessTokenEndpoint` | `accessTokenEndpoint` |
| Redirect URI | `redirectUri` | `callback` | `redirectUri` |
| Scope | `scope` | `scope` | `scope` |
| State | Context 관리 | `state` parameter | `State` 객체 |
| Authorization Code | Parameter | `code` | `Verifier` 객체 |
| Access Token | `OAuth2AccessToken` | `OAuth2AccessToken` | `OAuth2*TokenRes` |
| Refresh Token | `refreshToken` | `refreshToken` | `refresh_token` 필드 |

### 코드 패턴 매핑

#### 1. 인증 URL 생성

**Spring Security OAuth:**
```java
String authUrl = details.getUserAuthorizationUri() +
    "?response_type=code" +
    "&client_id=" + details.getClientId() +
    "&redirect_uri=" + details.getRedirectUri() +
    "&scope=" + String.join(" ", details.getScope()) +
    "&state=" + state;
```

**ScribeJava:**
```java
String authUrl = service.getAuthorizationUrl(state);
```

**sb-oauth-java:**
```java
OAuth2NaverAuthFunction authFunction = new OAuth2NaverAuthFunction(config);
String authUrl = authFunction.generate(state);
```

#### 2. 토큰 발급

**Spring Security OAuth:**
```java
OAuth2AccessToken token = restTemplate.getAccessToken();
```

**ScribeJava:**
```java
OAuth2AccessToken token = service.getAccessToken(code);
```

**sb-oauth-java:**
```java
OAuth2NaverTokenRes token = tokenFunction.issue(new Verifier(code), state);
```

#### 3. 토큰 갱신

**Spring Security OAuth:**
```java
// 자동으로 처리됨 (OAuth2RestTemplate)
```

**ScribeJava:**
```java
OAuth2AccessToken newToken = service.refreshAccessToken(token.getRefreshToken());
```

**sb-oauth-java:**
```java
OAuth2NaverTokenRes newToken = tokenFunction.refresh(new Token(refreshToken));
```

#### 4. API 호출

**Spring Security OAuth:**
```java
String result = restTemplate.getForObject(url, String.class);
```

**ScribeJava:**
```java
OAuthRequest request = new OAuthRequest(Verb.GET, url);
service.signRequest(token, request);
Response response = service.execute(request);
String result = response.getBody();
```

**sb-oauth-java:**
```java
OAuth2ResourceFunction<String> resourceFunction =
    new DefaultOAuth2ResourceFunction(url);
String result = resourceFunction.fetch(accessToken);
```

---

## 마이그레이션 체크리스트

### 1. 사전 준비

- [ ] 현재 사용 중인 OAuth 제공자 확인 (Naver, Kakao, Google, Facebook 등)
- [ ] 현재 라이브러리 버전 및 의존성 확인
- [ ] 토큰 저장 방식 확인 (세션, DB, 캐시 등)
- [ ] 현재 OAuth 플로우 문서화
- [ ] 테스트 환경 준비

### 2. 의존성 변경

**제거:**
- [ ] Spring Security OAuth 의존성 제거
- [ ] ScribeJava 의존성 제거

**추가:**
- [ ] `oauth-client` 추가
- [ ] Provider별 connector 추가 (예: `oauth-connector-naver`)
- [ ] 토큰 저장소 모듈 추가 (예: `oauth-storage-redis`)

### 3. 설정 마이그레이션

- [ ] OAuth 설정 클래스 작성 (`OAuth2*Config`)
- [ ] Bean 설정 추가 (AuthFunction, TokenFunction)
- [ ] `application.yml` 또는 `application.properties`에 설정 추가
- [ ] 환경 변수 설정 (CLIENT_ID, CLIENT_SECRET)

### 4. 코드 변경

**인증 플로우:**
- [ ] 인증 URL 생성 로직 변경
- [ ] State 생성 및 저장 로직 추가
- [ ] 콜백 핸들러 수정
- [ ] 토큰 교환 로직 변경

**토큰 관리:**
- [ ] 토큰 저장 로직 변경 (TokenStorage 사용)
- [ ] 토큰 갱신 로직 변경
- [ ] 토큰 취소 로직 추가 (지원하는 경우)

**API 호출:**
- [ ] ResourceFunction으로 API 호출 변경
- [ ] HTTP 헤더 처리 확인
- [ ] 응답 파싱 로직 확인

### 5. 테스트

- [ ] 단위 테스트 작성/수정
- [ ] 통합 테스트 작성
- [ ] 인증 플로우 E2E 테스트
- [ ] 토큰 갱신 테스트
- [ ] 에러 처리 테스트

### 6. 문서화

- [ ] 새로운 OAuth 플로우 문서화
- [ ] 설정 가이드 작성
- [ ] 배포 가이드 업데이트
- [ ] 팀원 교육 자료 준비

### 7. 배포

- [ ] 개발 환경 테스트
- [ ] 스테이징 환경 테스트
- [ ] 프로덕션 배포 계획 수립
- [ ] 롤백 계획 준비
- [ ] 모니터링 설정

---

## 일반적인 마이그레이션 문제

### 문제 1: 토큰 저장소 호환성

**증상**: 기존 토큰을 읽을 수 없음

**원인**: 토큰 저장 포맷이 다름

**해결**:
```java
// 마이그레이션 스크립트
@Component
public class TokenMigration {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TokenStorage newTokenStorage;

    public void migrateTokens() {
        // 기존 DB에서 토큰 조회
        List<OldToken> oldTokens = jdbcTemplate.query(
            "SELECT user_id, access_token, refresh_token FROM oauth_tokens",
            (rs, rowNum) -> new OldToken(
                rs.getString("user_id"),
                rs.getString("access_token"),
                rs.getString("refresh_token")
            )
        );

        // 새로운 저장소로 이관
        for (OldToken oldToken : oldTokens) {
            newTokenStorage.store(
                oldToken.getUserId() + ":access_token",
                new Token(oldToken.getAccessToken())
            );
            newTokenStorage.store(
                oldToken.getUserId() + ":refresh_token",
                new Token(oldToken.getRefreshToken())
            );
        }
    }
}
```

### 문제 2: State 검증 로직 부재

**증상**: CSRF 공격에 취약

**원인**: 기존 코드에서 State 검증을 하지 않음

**해결**:
```java
// Before: State 검증 없음
@GetMapping("/callback")
public String callback(@RequestParam("code") String code) {
    // 위험: State 검증 없음
    OAuth2AccessToken token = service.getAccessToken(code);
    return "success";
}

// After: State 검증 추가
@GetMapping("/callback")
public String callback(
    @RequestParam("code") String code,
    @RequestParam("state") String stateValue,
    HttpSession session
) {
    // State 검증
    String savedState = (String) session.getAttribute("oauth_state");
    if (!stateValue.equals(savedState)) {
        throw new IllegalStateException("Invalid state - CSRF attack detected");
    }

    // 토큰 교환
    OAuth2NaverTokenRes token = tokenFunction.issue(
        new Verifier(code),
        new State(stateValue)
    );
    return "success";
}
```

### 문제 3: Provider별 특이사항 누락

**증상**: 토큰 갱신이 제대로 동작하지 않음

**원인**: Provider별 토큰 동작 방식 차이

**해결**:
```java
// Naver: Refresh Token이 영구 유효하고 갱신 시 변경되지 않음
OAuth2NaverTokenRes newToken = naverTokenFunction.refresh(refreshToken);
// newToken.getRefresh_token() == 기존 refreshToken (동일함)

// Kakao: Refresh Token이 60일 후 만료되고 갱신 시 새로운 토큰 발급
OAuth2KakaoTokenRes newToken = kakaoTokenFunction.refresh(refreshToken);
// newToken.getRefresh_token() != 기존 refreshToken (변경됨, 저장 필요!)

// 올바른 처리
if (provider.equals("kakao")) {
    // Kakao는 새로운 refresh token 저장 필요
    saveRefreshToken(userId, newToken.getRefresh_token());
}
```

---

## 점진적 마이그레이션 전략

대규모 애플리케이션의 경우 한 번에 모두 마이그레이션하기 어려울 수 있습니다.

### 전략 1: Provider별 마이그레이션

```java
@Configuration
public class HybridOAuthConfig {

    // 기존: Spring Security OAuth (Google, Facebook)
    @Bean
    public OAuth2RestTemplate googleRestTemplate() {
        // 기존 설정 유지
    }

    // 신규: sb-oauth-java (Naver, Kakao)
    @Bean
    public OAuth2NaverAccesstokenFunction naverTokenFunction() {
        // 새로운 설정
    }
}
```

**마이그레이션 순서:**
1. Naver → sb-oauth-java
2. Kakao → sb-oauth-java
3. Google → sb-oauth-java
4. Facebook → sb-oauth-java

### 전략 2: 기능별 마이그레이션

```java
// Phase 1: 신규 가입만 sb-oauth-java 사용
if (isNewUser) {
    return newOAuthService.login(provider);
} else {
    return legacyOAuthService.login(provider);
}

// Phase 2: 일부 사용자 마이그레이션
if (shouldMigrate(userId)) {
    migrateUserTokens(userId);
    return newOAuthService.login(provider);
} else {
    return legacyOAuthService.login(provider);
}

// Phase 3: 전체 전환
return newOAuthService.login(provider);
```

---

## 추가 리소스

- [사용자 가이드](USER_GUIDE.md) - 기본 사용법
- [아키텍처 가이드](ARCHITECTURE.md) - 내부 구조
- [FAQ](FAQ.md) - 자주 묻는 질문
- [GitHub Issues](https://github.com/archmagece-backyard/sb-oauth-java/issues) - 문제 제보

---

**마이그레이션에 도움이 필요하신가요?**
- GitHub Issues에 질문 남기기
- 예제 코드: `examples/` 디렉토리 참고
- 커뮤니티 지원: GitHub Discussions

Happy migrating! 🚀
