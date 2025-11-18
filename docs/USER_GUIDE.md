# sb-oauth-java 사용자 가이드

sb-oauth-java를 처음 사용하시는 분들을 위한 단계별 가이드입니다.

## 목차

1. [시작하기 전에](#시작하기-전에)
2. [빠른 시작](#빠른-시작)
3. [기본 개념](#기본-개념)
4. [단계별 튜토리얼](#단계별-튜토리얼)
5. [일반적인 사용 사례](#일반적인-사용-사례)
6. [다음 단계](#다음-단계)

---

## 시작하기 전에

### 필요한 것

- **Java 21 이상** - OpenJDK 또는 Oracle JDK
- **Maven 3.8+** 또는 **Gradle 7.0+**
- **OAuth 제공자 계정** - Naver, Kakao, Google, Facebook 중 하나
- **기본적인 OAuth 2.0 이해** - [OAuth 2.0 간단 설명](#oauth-20-간단-설명) 참고

### OAuth 2.0 간단 설명

OAuth 2.0은 사용자가 비밀번호를 공유하지 않고도 제3자 애플리케이션에 권한을 부여할 수 있게 해주는 인증 프로토콜입니다.

**기본 흐름:**
```
1. 사용자 → [로그인 버튼 클릭]
2. 앱 → 사용자를 OAuth 제공자(Naver/Kakao 등)로 리다이렉트
3. 사용자 → OAuth 제공자에서 로그인 및 권한 승인
4. OAuth 제공자 → 사용자를 앱으로 리다이렉트 (인증 코드 포함)
5. 앱 → 인증 코드를 액세스 토큰으로 교환
6. 앱 → 액세스 토큰으로 사용자 정보 조회
```

**주요 용어:**
- **Client ID / Secret**: 앱을 식별하는 인증 정보
- **Authorization Code**: 일회성 인증 코드
- **Access Token**: API 호출에 사용하는 토큰 (짧은 수명)
- **Refresh Token**: Access Token 갱신용 토큰 (긴 수명)
- **Scope**: 요청하는 권한 범위 (profile, email 등)
- **Redirect URI**: OAuth 후 돌아올 URL (콜백 URL)

---

## 빠른 시작

### 1. 의존성 추가

**Maven** (`pom.xml`):
```xml
<dependencies>
    <!-- 핵심 라이브러리 -->
    <dependency>
        <groupId>org.scriptonbasestar.oauth</groupId>
        <artifactId>oauth-client</artifactId>
        <version>sb-oauth-20181219-3-DEV</version>
    </dependency>

    <!-- Naver OAuth (예시) -->
    <dependency>
        <groupId>org.scriptonbasestar.oauth</groupId>
        <artifactId>oauth-connector-naver</artifactId>
        <version>sb-oauth-20181219-3-DEV</version>
    </dependency>

    <!-- 토큰 저장소 (Redis 예시) -->
    <dependency>
        <groupId>org.scriptonbasestar.oauth</groupId>
        <artifactId>oauth-storage-redis</artifactId>
        <version>sb-oauth-20181219-3-DEV</version>
    </dependency>
</dependencies>
```

**Gradle** (`build.gradle`):
```gradle
dependencies {
    implementation 'org.scriptonbasestar.oauth:oauth-client:sb-oauth-20181219-3-DEV'
    implementation 'org.scriptonbasestar.oauth:oauth-connector-naver:sb-oauth-20181219-3-DEV'
    implementation 'org.scriptonbasestar.oauth:oauth-storage-redis:sb-oauth-20181219-3-DEV'
}
```

### 2. OAuth 제공자 등록

사용하려는 OAuth 제공자에 앱을 등록하세요:

| 제공자 | 등록 URL | 발급 받는 것 |
|--------|----------|--------------|
| **Naver** | https://developers.naver.com/apps/ | Client ID, Client Secret |
| **Kakao** | https://developers.kakao.com/ | REST API 키 (Client ID), Client Secret (선택) |
| **Google** | https://console.cloud.google.com/ | Client ID, Client Secret |
| **Facebook** | https://developers.facebook.com/ | App ID, App Secret |

**등록 시 필수 설정:**
- **Redirect URI**: `http://localhost:8080/oauth/callback/{provider}`
- **권한 (Scope)**: `profile`, `email` 등

### 3. 5분 예제 코드

```java
import org.scripton.oauth.connector.naver.*;
import org.scriptonbasestar.oauth.client.nobi.*;
import org.scriptonbasestar.oauth.client.nobi.state.*;
import org.scriptonbasestar.oauth.client.nobi.token.*;
import org.scriptonbasestar.oauth.client.model.*;
import com.fasterxml.jackson.core.type.TypeReference;

public class QuickStart {
    public static void main(String[] args) {
        // 1. 설정
        OAuth2NaverConfig config = OAuth2NaverConfig.builder()
            .clientId("YOUR_CLIENT_ID")
            .clientSecret("YOUR_CLIENT_SECRET")
            .redirectUri("http://localhost:8080/callback")
            .scope("profile,email")
            .build();

        // 2. 인증 URL 생성
        State state = new RandomStringStateGenerator().generate("NAVER");
        OAuth2NaverAuthFunction authFunction = new OAuth2NaverAuthFunction(config);
        String authUrl = authFunction.generate(state);

        System.out.println("다음 URL을 방문하세요:");
        System.out.println(authUrl);

        // 3. 사용자가 인증 후 돌아온 코드 입력
        Scanner scanner = new Scanner(System.in);
        System.out.print("인증 코드를 입력하세요: ");
        String code = scanner.nextLine();

        // 4. 토큰 교환
        TokenExtractor<OAuth2NaverTokenRes> extractor =
            new JsonTokenExtractor<>(new TypeReference<OAuth2NaverTokenRes>() {});
        TokenStorage storage = new LocalTokenStorage();

        OAuth2NaverAccesstokenFunction tokenFunction =
            new OAuth2NaverAccesstokenFunction(config, extractor, storage);

        OAuth2NaverTokenRes token = tokenFunction.issue(new Verifier(code), state);

        System.out.println("Access Token: " + token.getAccess_token());
        System.out.println("로그인 성공!");
    }
}
```

**실행 결과:**
```
다음 URL을 방문하세요:
https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=...

인증 코드를 입력하세요: ABC123DEF456
Access Token: AAAAQoQ...
로그인 성공!
```

---

## 기본 개념

### 모듈 구조

sb-oauth-java는 여러 모듈로 구성되어 있습니다:

```
sb-oauth-java/
├── oauth-client/              # 핵심 라이브러리 (필수)
├── oauth-connector/
│   ├── connector-naver/       # Naver OAuth
│   ├── connector-kakao/       # Kakao OAuth
│   ├── connector-google/      # Google OAuth
│   └── connector-facebook/    # Facebook OAuth
├── oauth-storage/
│   ├── storage-redis/         # Redis 토큰 저장소
│   └── storage-ehcache/       # Ehcache 토큰 저장소
└── oauth-integration/
    └── integration-spring-boot/ # Spring Boot 통합
```

**모듈 선택 가이드:**
- **oauth-client**: 항상 필요 (핵심)
- **connector-***: 사용할 OAuth 제공자 선택
- **storage-***:
  - 개발/테스트: `LocalTokenStorage` (코드로 직접 생성)
  - 단일 서버: `storage-ehcache`
  - 다중 서버: `storage-redis`

### 핵심 인터페이스

#### 1. OAuth2GenerateAuthorizeEndpointFunction
인증 URL을 생성합니다.

```java
OAuth2NaverAuthFunction authFunction = new OAuth2NaverAuthFunction(config);
String url = authFunction.generate(state);
// → https://nid.naver.com/oauth2.0/authorize?...
```

#### 2. OAuth2AccessTokenEndpointFunction
토큰을 발급, 갱신, 취소합니다.

```java
OAuth2NaverAccesstokenFunction tokenFunction =
    new OAuth2NaverAccesstokenFunction(config, extractor, storage);

// 토큰 발급
OAuth2NaverTokenRes token = tokenFunction.issue(verifier, state);

// 토큰 갱신
OAuth2NaverTokenRes newToken = tokenFunction.refresh(new Token(refreshToken));

// 토큰 취소 (제공자에 따라 지원 여부 다름)
tokenFunction.revoke(new Token(accessToken));
```

#### 3. OAuth2ResourceFunction
액세스 토큰으로 API를 호출합니다.

```java
OAuth2ResourceFunction<String> resourceFunction =
    new DefaultOAuth2ResourceFunction("https://openapi.naver.com/v1/nid/me");

String profile = resourceFunction.fetch(accessToken);
```

### 주요 클래스

| 클래스 | 용도 | 예시 |
|--------|------|------|
| **State** | CSRF 방지용 상태값 | `new RandomStringStateGenerator().generate("NAVER")` |
| **Verifier** | 인증 코드 래퍼 | `new Verifier("AUTH_CODE")` |
| **Token** | 토큰 값 래퍼 | `new Token("ACCESS_TOKEN")` |
| **TokenStorage** | 토큰 저장소 인터페이스 | `LocalTokenStorage`, `RedisTokenStorage` |
| **TokenExtractor** | JSON → 토큰 객체 변환 | `JsonTokenExtractor<OAuth2NaverTokenRes>` |

---

## 단계별 튜토리얼

### 튜토리얼 1: Naver 로그인 구현 (기초)

**목표**: Naver 계정으로 로그인하고 사용자 프로필 가져오기

#### Step 1: Naver Developers 앱 등록

1. https://developers.naver.com/apps/ 접속
2. **애플리케이션 등록** 클릭
3. 정보 입력:
   - 애플리케이션 이름: `My App`
   - 사용 API: **네이버 로그인**
   - 제공 정보: `회원이름`, `이메일 주소`, `프로필 사진`
   - 서비스 URL: `http://localhost:8080`
   - Callback URL: `http://localhost:8080/oauth/callback/naver`
4. **등록하기** → **Client ID**, **Client Secret** 복사

#### Step 2: 의존성 추가

```xml
<dependency>
    <groupId>org.scriptonbasestar.oauth</groupId>
    <artifactId>oauth-connector-naver</artifactId>
    <version>sb-oauth-20181219-3-DEV</version>
</dependency>
```

#### Step 3: 설정 클래스 작성

```java
public class NaverOAuthConfig {
    public static final String CLIENT_ID = "YOUR_CLIENT_ID";
    public static final String CLIENT_SECRET = "YOUR_CLIENT_SECRET";
    public static final String REDIRECT_URI = "http://localhost:8080/oauth/callback/naver";
    public static final String SCOPE = "profile,email";

    public static OAuth2NaverConfig createConfig() {
        return OAuth2NaverConfig.builder()
            .clientId(CLIENT_ID)
            .clientSecret(CLIENT_SECRET)
            .redirectUri(REDIRECT_URI)
            .scope(SCOPE)
            .build();
    }
}
```

#### Step 4: 인증 URL 생성 (로그인 버튼)

```java
public class Step1_GenerateAuthUrl {
    public static void main(String[] args) {
        // 설정
        OAuth2NaverConfig config = NaverOAuthConfig.createConfig();

        // State 생성 (CSRF 방지)
        StateGenerator stateGenerator = new RandomStringStateGenerator();
        State state = stateGenerator.generate("NAVER");

        // 인증 URL 생성
        OAuth2NaverAuthFunction authFunction = new OAuth2NaverAuthFunction(config);
        String authUrl = authFunction.generate(state);

        // State 저장 (세션 또는 DB에)
        System.out.println("State (저장 필요): " + state.getValue());
        System.out.println("\n브라우저에서 다음 URL을 방문하세요:");
        System.out.println(authUrl);
    }
}
```

**출력:**
```
State (저장 필요): NAVER_abc123def456
브라우저에서 다음 URL을 방문하세요:
https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=...&redirect_uri=...&state=NAVER_abc123def456
```

#### Step 5: 콜백 처리 및 토큰 교환

```java
public class Step2_ExchangeToken {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // 사용자가 인증 후 돌아온 정보 입력
        System.out.print("인증 코드 (code): ");
        String code = scanner.nextLine();

        System.out.print("State 값: ");
        String stateValue = scanner.nextLine();

        // 설정
        OAuth2NaverConfig config = NaverOAuthConfig.createConfig();

        // TokenExtractor 및 Storage 설정
        TokenExtractor<OAuth2NaverTokenRes> extractor =
            new JsonTokenExtractor<>(new TypeReference<OAuth2NaverTokenRes>() {});
        TokenStorage storage = new LocalTokenStorage();

        // 토큰 함수 생성
        OAuth2NaverAccesstokenFunction tokenFunction =
            new OAuth2NaverAccesstokenFunction(config, extractor, storage);

        // 토큰 교환
        Verifier verifier = new Verifier(code);
        State state = new State(stateValue);
        OAuth2NaverTokenRes tokenRes = tokenFunction.issue(verifier, state);

        // 결과 출력
        System.out.println("\n✅ 토큰 발급 성공!");
        System.out.println("Access Token: " + tokenRes.getAccess_token());
        System.out.println("Refresh Token: " + tokenRes.getRefresh_token());
        System.out.println("Token Type: " + tokenRes.getToken_type());
        System.out.println("Expires In: " + tokenRes.getExpires_in() + "초");
    }
}
```

#### Step 6: 사용자 프로필 조회

```java
public class Step3_FetchProfile {
    public static void main(String[] args) {
        String accessToken = "발급받은_ACCESS_TOKEN";

        // 리소스 함수 생성
        OAuth2ResourceFunction<String> resourceFunction =
            new DefaultOAuth2ResourceFunction("https://openapi.naver.com/v1/nid/me");

        // 프로필 조회
        String profileJson = resourceFunction.fetch(accessToken);

        System.out.println("사용자 프로필:");
        System.out.println(profileJson);

        // JSON 파싱 예시
        // {
        //   "resultcode": "00",
        //   "message": "success",
        //   "response": {
        //     "id": "1234567890",
        //     "nickname": "홍길동",
        //     "email": "user@naver.com",
        //     "profile_image": "https://..."
        //   }
        // }
    }
}
```

---

### 튜토리얼 2: Spring Boot 웹 애플리케이션

**목표**: Spring Boot로 Naver 로그인 웹 앱 만들기

#### Step 1: Spring Boot 프로젝트 생성

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.4.1</version>
</parent>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
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
    <dependency>
        <groupId>redis.clients</groupId>
        <artifactId>jedis</artifactId>
    </dependency>
</dependencies>
```

#### Step 2: application.yml 설정

```yaml
server:
  port: 8080

oauth:
  naver:
    client-id: ${NAVER_CLIENT_ID}
    client-secret: ${NAVER_CLIENT_SECRET}
    redirect-uri: http://localhost:8080/oauth/callback/naver
    scope: profile,email

redis:
  host: localhost
  port: 6379
```

#### Step 3: Configuration 클래스

```java
@Configuration
public class OAuthConfig {

    @Value("${oauth.naver.client-id}")
    private String clientId;

    @Value("${oauth.naver.client-secret}")
    private String clientSecret;

    @Value("${oauth.naver.redirect-uri}")
    private String redirectUri;

    @Value("${oauth.naver.scope}")
    private String scope;

    @Bean
    public OAuth2NaverConfig naverConfig() {
        return OAuth2NaverConfig.builder()
            .clientId(clientId)
            .clientSecret(clientSecret)
            .redirectUri(redirectUri)
            .scope(scope)
            .build();
    }

    @Bean
    public StateGenerator stateGenerator() {
        return new RandomStringStateGenerator();
    }

    @Bean
    public JedisPool jedisPool() {
        return new JedisPool("localhost", 6379);
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

#### Step 4: Controller 작성

```java
@RestController
@RequestMapping("/oauth")
public class OAuthController {

    @Autowired
    private OAuth2NaverAuthFunction authFunction;

    @Autowired
    private OAuth2NaverAccesstokenFunction tokenFunction;

    @Autowired
    private StateGenerator stateGenerator;

    // 1. 로그인 시작
    @GetMapping("/naver/login")
    public String login(HttpSession session) {
        // State 생성 및 세션 저장
        State state = stateGenerator.generate("NAVER");
        session.setAttribute("oauth_state", state.getValue());

        // 인증 URL 생성 및 리다이렉트
        String authUrl = authFunction.generate(state);
        return "redirect:" + authUrl;
    }

    // 2. 콜백 처리
    @GetMapping("/callback/naver")
    public Map<String, Object> callback(
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
        OAuth2NaverTokenRes tokenRes = tokenFunction.issue(verifier, state);

        // 세션에 토큰 저장
        session.setAttribute("access_token", tokenRes.getAccess_token());
        session.setAttribute("refresh_token", tokenRes.getRefresh_token());

        // 결과 반환
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("access_token", tokenRes.getAccess_token());
        return result;
    }

    // 3. 프로필 조회
    @GetMapping("/profile")
    public String getProfile(HttpSession session) {
        String accessToken = (String) session.getAttribute("access_token");
        if (accessToken == null) {
            return "Not logged in";
        }

        OAuth2ResourceFunction<String> resourceFunction =
            new DefaultOAuth2ResourceFunction("https://openapi.naver.com/v1/nid/me");

        return resourceFunction.fetch(accessToken);
    }
}
```

#### Step 5: 테스트

```bash
# Redis 시작
docker run -d -p 6379:6379 redis

# Spring Boot 실행
mvn spring-boot:run

# 브라우저에서 접속
http://localhost:8080/oauth/naver/login
```

---

## 일반적인 사용 사례

### 사용 사례 1: 다중 OAuth 제공자 지원

```java
@Configuration
public class MultiProviderConfig {

    @Bean
    public Map<String, OAuth2GenerateAuthorizeEndpointFunction> authFunctions(
        OAuth2NaverAuthFunction naverAuth,
        OAuth2KakaoAuthFunction kakaoAuth,
        OAuth2GoogleGenerateAuthorizeUrlFunction googleAuth
    ) {
        Map<String, OAuth2GenerateAuthorizeEndpointFunction> map = new HashMap<>();
        map.put("naver", naverAuth);
        map.put("kakao", kakaoAuth);
        map.put("google", googleAuth);
        return map;
    }

    @Bean
    public Map<String, OAuth2AccessTokenEndpointFunction<?>> tokenFunctions(
        OAuth2NaverAccesstokenFunction naverToken,
        OAuth2KakaoAccessTokenFunction kakaoToken,
        OAuth2GoogleAccessTokenEndpointFunction googleToken
    ) {
        Map<String, OAuth2AccessTokenEndpointFunction<?>> map = new HashMap<>();
        map.put("naver", naverToken);
        map.put("kakao", kakaoToken);
        map.put("google", googleToken);
        return map;
    }
}
```

**Controller:**
```java
@GetMapping("/oauth/{provider}/login")
public String login(@PathVariable String provider, HttpSession session) {
    OAuth2GenerateAuthorizeEndpointFunction authFunction =
        authFunctions.get(provider);

    State state = stateGenerator.generate(provider.toUpperCase());
    session.setAttribute("oauth_state_" + provider, state.getValue());

    return "redirect:" + authFunction.generate(state);
}
```

### 사용 사례 2: 토큰 자동 갱신

```java
@Component
public class TokenRefreshService {

    @Autowired
    private OAuth2NaverAccesstokenFunction tokenFunction;

    public String getValidAccessToken(String userId) {
        // 저장된 토큰 조회
        OAuth2NaverTokenRes storedToken = loadTokenFromDB(userId);

        // 만료 체크 (expires_in 기준)
        if (isTokenExpired(storedToken)) {
            // 토큰 갱신
            Token refreshToken = new Token(storedToken.getRefresh_token());
            OAuth2NaverTokenRes newToken = tokenFunction.refresh(refreshToken);

            // DB 업데이트
            saveTokenToDB(userId, newToken);

            return newToken.getAccess_token();
        }

        return storedToken.getAccess_token();
    }

    private boolean isTokenExpired(OAuth2NaverTokenRes token) {
        long issuedAt = token.getIssuedAt(); // 발급 시간 (별도 저장 필요)
        long expiresIn = token.getExpires_in(); // 3600초
        long now = System.currentTimeMillis() / 1000;

        // 만료 5분 전에 미리 갱신
        return (now - issuedAt) >= (expiresIn - 300);
    }
}
```

### 사용 사례 3: 에러 처리

```java
@RestControllerAdvice
public class OAuthExceptionHandler {

    @ExceptionHandler(OAuthNetworkException.class)
    public ResponseEntity<ErrorResponse> handleNetworkError(OAuthNetworkException e) {
        return ResponseEntity.status(503)
            .body(new ErrorResponse("OAuth provider is temporarily unavailable"));
    }

    @ExceptionHandler(OAuthAuthException.class)
    public ResponseEntity<ErrorResponse> handleAuthError(OAuthAuthException e) {
        return ResponseEntity.status(401)
            .body(new ErrorResponse("Authentication failed: " + e.getMessage()));
    }

    @ExceptionHandler(OAuthParsingException.class)
    public ResponseEntity<ErrorResponse> handleParsingError(OAuthParsingException e) {
        return ResponseEntity.status(500)
            .body(new ErrorResponse("Failed to parse OAuth response"));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidState(IllegalStateException e) {
        return ResponseEntity.status(400)
            .body(new ErrorResponse("Invalid state parameter (CSRF check failed)"));
    }
}
```

---

## 다음 단계

### 더 배우기

1. **모듈별 상세 문서**:
   - [oauth-client README](../oauth-client/README.md) - 핵심 API 레퍼런스
   - [Naver Connector](../oauth-connector/connector-naver/README.md)
   - [Kakao Connector](../oauth-connector/connector-kakao/README.md)
   - [Google Connector](../oauth-connector/connector-google/README.md)
   - [Facebook Connector](../oauth-connector/connector-facebook/README.md)
   - [Redis Storage](../oauth-storage/storage-redis/README.md)
   - [Ehcache Storage](../oauth-storage/storage-ehcache/README.md)

2. **고급 주제**:
   - [아키텍처 가이드](ARCHITECTURE.md) - 내부 구조 이해
   - [마이그레이션 가이드](MIGRATION.md) - 다른 라이브러리에서 전환
   - [FAQ](FAQ.md) - 자주 묻는 질문

3. **개발 참여**:
   - [기여 가이드](../CONTRIBUTING.md) - 오픈소스 기여 방법
   - [이슈 제출](https://github.com/archmagece-backyard/sb-oauth-java/issues)

### 추천 학습 경로

**초급 (1-2일):**
1. ✅ 이 문서의 빠른 시작 완료
2. ✅ 튜토리얼 1 완료 (기본 Naver 로그인)
3. ✅ 프로필 조회까지 구현

**중급 (3-5일):**
1. ✅ 튜토리얼 2 완료 (Spring Boot 통합)
2. ✅ Redis 토큰 저장소 사용
3. ✅ 토큰 갱신 로직 구현
4. ✅ 에러 처리 추가

**고급 (1-2주):**
1. ✅ 다중 OAuth 제공자 지원
2. ✅ 커스텀 TokenStorage 구현
3. ✅ ARCHITECTURE.md 읽고 내부 구조 이해
4. ✅ 프로덕션 배포 (HTTPS, 보안 강화)

---

## 문제 해결

### 자주 발생하는 문제

#### 1. `redirect_uri_mismatch` 에러

**원인**: 코드의 redirect_uri와 OAuth 제공자에 등록된 URI가 다름

**해결**:
```java
// 코드
redirectUri = "http://localhost:8080/oauth/callback/naver"

// Naver Developers에서도 정확히 동일하게 등록
// ✅ http://localhost:8080/oauth/callback/naver
// ❌ http://localhost:8080/oauth/callback/naver/
// ❌ http://localhost:8080/callback
```

#### 2. Invalid State 에러

**원인**: State 값이 일치하지 않음 (CSRF 공격 방지 실패)

**해결**:
```java
// 1. State 생성 시 저장
State state = stateGenerator.generate("NAVER");
session.setAttribute("oauth_state", state.getValue());

// 2. 콜백에서 검증
String savedState = (String) session.getAttribute("oauth_state");
if (!stateValue.equals(savedState)) {
    throw new IllegalStateException("Invalid state");
}
```

#### 3. Token Expired 에러

**원인**: Access Token이 만료됨 (보통 1-6시간)

**해결**: Refresh Token으로 갱신
```java
try {
    // API 호출
    resourceFunction.fetch(accessToken);
} catch (OAuthAuthException e) {
    // 토큰 갱신
    OAuth2NaverTokenRes newToken = tokenFunction.refresh(new Token(refreshToken));
    // 재시도
    resourceFunction.fetch(newToken.getAccess_token());
}
```

더 많은 문제 해결 방법은 [FAQ](FAQ.md)를 참고하세요.

---

## 추가 리소스

### 공식 문서
- [OAuth 2.0 RFC 6749](https://tools.ietf.org/html/rfc6749)
- [Naver Login API](https://developers.naver.com/docs/login/api/)
- [Kakao Login](https://developers.kakao.com/docs/latest/ko/kakaologin/common)
- [Google OAuth 2.0](https://developers.google.com/identity/protocols/oauth2)
- [Facebook Login](https://developers.facebook.com/docs/facebook-login/)

### 커뮤니티
- GitHub Issues: [sb-oauth-java/issues](https://github.com/archmagece-backyard/sb-oauth-java/issues)
- GitHub Discussions: 질문 및 토론

---

**도움이 필요하신가요?**
- 버그 발견: [GitHub Issues](https://github.com/archmagece-backyard/sb-oauth-java/issues)에 제보
- 질문: [FAQ](FAQ.md) 확인 또는 Discussions에서 질문
- 기여: [CONTRIBUTING.md](../CONTRIBUTING.md) 참고

Happy coding! 🚀
