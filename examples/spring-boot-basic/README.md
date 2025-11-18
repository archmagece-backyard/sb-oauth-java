# Spring Boot Basic Example

**난이도**: ⭐ 초급
**소요 시간**: 15분

Spring Boot를 사용한 기본 Naver OAuth 로그인 예제입니다.

## 무엇을 배울 수 있나요?

- ✅ sb-oauth-java 기본 사용법
- ✅ Spring Boot 통합
- ✅ OAuth 2.0 전체 플로우
- ✅ State 파라미터를 사용한 CSRF 방지
- ✅ 세션 기반 토큰 관리
- ✅ 사용자 프로필 조회

## 사전 요구사항

- Java 21+
- Maven 3.8+
- Naver Developers 계정

## Naver 앱 등록

1. https://developers.naver.com/apps/ 접속
2. **애플리케이션 등록** 클릭
3. 다음 정보 입력:
   - **애플리케이션 이름**: `OAuth Test App`
   - **사용 API**: **네이버 로그인**
   - **제공 정보**: `회원이름`, `이메일 주소`, `프로필 사진`
   - **서비스 URL**: `http://localhost:8080`
   - **Callback URL**: `http://localhost:8080/oauth/callback/naver`
4. **등록하기** → **Client ID**, **Client Secret** 복사

## 빠른 시작

### 1. 환경 변수 설정

```bash
# .env.example을 복사
cp .env.example .env

# .env 파일 편집
nano .env
```

**.env 내용:**
```env
NAVER_CLIENT_ID=your_client_id_here
NAVER_CLIENT_SECRET=your_client_secret_here
SERVER_PORT=8080
```

### 2. 부모 프로젝트 빌드

```bash
# sb-oauth-java 루트로 이동
cd ../..

# 부모 프로젝트 빌드
mvn clean install -DskipTests

# 예제 디렉토리로 돌아오기
cd examples/spring-boot-basic
```

### 3. 애플리케이션 실행

```bash
# 환경 변수 로드 및 실행
export $(cat .env | xargs) && mvn spring-boot:run
```

또는:

```bash
# Maven wrapper 사용
./mvnw spring-boot:run
```

### 4. 브라우저에서 접속

```
http://localhost:8080
```

## 사용 방법

### Step 1: 홈페이지

![Home Page](../../docs/images/example-home.png)

- 홈페이지에서 **Login with Naver** 버튼 클릭

### Step 2: Naver 로그인

- Naver 로그인 페이지로 리다이렉트됨
- Naver 계정으로 로그인
- 앱 권한 승인

### Step 3: 프로필 확인

![Profile Page](../../docs/images/example-profile.png)

- 자동으로 프로필 페이지로 이동
- 사용자 정보 및 토큰 정보 확인
- JSON 응답 확인

### Step 4: 로그아웃

- **Logout** 버튼 클릭
- 세션 종료 및 홈으로 이동

## 프로젝트 구조

```
spring-boot-basic/
├── pom.xml                                     # Maven 설정
├── .env.example                                # 환경 변수 템플릿
├── README.md                                   # 이 파일
└── src/
    └── main/
        ├── java/com/example/oauth/
        │   ├── Application.java                # 메인 클래스
        │   ├── config/
        │   │   └── OAuthConfig.java           # OAuth 설정
        │   ├── controller/
        │   │   ├── HomeController.java        # 홈/프로필 컨트롤러
        │   │   └── OAuthController.java       # OAuth 로그인/콜백
        │   └── service/
        │       └── OAuthService.java          # OAuth 비즈니스 로직
        └── resources/
            ├── application.yml                 # Spring Boot 설정
            └── templates/
                ├── index.html                  # 홈 페이지
                ├── profile.html                # 프로필 페이지
                └── error.html                  # 에러 페이지
```

## 코드 설명

### OAuth 설정 (OAuthConfig.java)

```java
@Configuration
public class OAuthConfig {

    // Naver OAuth 설정
    @Bean
    public OAuth2NaverConfig naverConfig() {
        return OAuth2NaverConfig.builder()
            .clientId(clientId)
            .clientSecret(clientSecret)
            .redirectUri(redirectUri)
            .scope(scope)
            .build();
    }

    // State 생성기 (CSRF 방지)
    @Bean
    public StateGenerator stateGenerator() {
        return new RandomStringStateGenerator();
    }

    // 토큰 저장소 (메모리)
    @Bean
    public TokenStorage tokenStorage() {
        return new LocalTokenStorage();
    }

    // 인증 URL 생성 함수
    @Bean
    public OAuth2NaverAuthFunction naverAuthFunction(OAuth2NaverConfig config) {
        return new OAuth2NaverAuthFunction(config);
    }

    // 토큰 함수 (발급, 갱신, 취소)
    @Bean
    public OAuth2NaverAccesstokenFunction naverTokenFunction(...) {
        return new OAuth2NaverAccesstokenFunction(config, extractor, storage);
    }
}
```

### OAuth 로그인 (OAuthController.java)

```java
@GetMapping("/oauth/naver/login")
public String naverLogin(HttpSession session) {
    // 인증 URL 생성 (State 포함)
    String authUrl = oauthService.generateAuthUrl(session);

    // Naver 로그인 페이지로 리다이렉트
    return "redirect:" + authUrl;
}

@GetMapping("/oauth/callback/naver")
public String naverCallback(
    @RequestParam("code") String code,
    @RequestParam("state") String state,
    HttpSession session
) {
    // State 검증 (CSRF 방지)
    String savedState = (String) session.getAttribute("oauth_state");
    if (!state.equals(savedState)) {
        return "redirect:/error?message=Invalid state";
    }

    // 코드를 토큰으로 교환
    OAuth2NaverTokenRes tokenRes = oauthService.exchangeCodeForToken(code, state);

    // 세션에 토큰 저장
    session.setAttribute("access_token", tokenRes.getAccess_token());
    session.setAttribute("refresh_token", tokenRes.getRefresh_token());

    return "redirect:/profile";
}
```

### 프로필 조회 (HomeController.java)

```java
@GetMapping("/profile")
public String profile(HttpSession session, Model model) {
    String accessToken = (String) session.getAttribute("access_token");

    // 사용자 프로필 조회
    String profileJson = oauthService.fetchUserProfile(accessToken);

    model.addAttribute("profile", profileJson);
    return "profile";
}
```

## OAuth 플로우

```
┌──────┐         ┌─────────┐         ┌──────────┐         ┌─────────┐
│ User │         │   App   │         │sb-oauth │         │  Naver  │
└──┬───┘         └────┬────┘         └────┬─────┘         └────┬────┘
   │                  │                   │                    │
   │ 1. /naver/login  │                   │                    │
   ├─────────────────>│                   │                    │
   │                  │ 2. generate()     │                    │
   │                  ├──────────────────>│                    │
   │                  │ 3. auth URL       │                    │
   │                  │<──────────────────┤                    │
   │ 4. Redirect      │                   │                    │
   │<─────────────────┤                   │                    │
   │                  │                   │                    │
   │ 5. Login & Approve                   │                    │
   ├──────────────────┼───────────────────┼───────────────────>│
   │                  │                   │                    │
   │ 6. Redirect with code & state        │                    │
   │<─────────────────┼───────────────────┼────────────────────┤
   │                  │                   │                    │
   │ 7. /callback     │                   │                    │
   ├─────────────────>│                   │                    │
   │                  │ 8. issue(code)    │                    │
   │                  ├──────────────────>│ 9. POST /token     │
   │                  │                   ├───────────────────>│
   │                  │                   │ 10. Token Response │
   │                  │                   │<───────────────────┤
   │                  │ 11. TokenRes      │                    │
   │                  │<──────────────────┤                    │
   │ 12. Redirect /profile                │                    │
   │<─────────────────┤                   │                    │
```

## 문제 해결

### 1. `redirect_uri_mismatch` 에러

**원인**: Redirect URI 불일치

**해결**:
```yaml
# application.yml에서 확인
oauth:
  naver:
    redirect-uri: http://localhost:8080/oauth/callback/naver

# Naver Developers Console에서 정확히 동일하게 등록되어 있는지 확인
```

### 2. `Invalid state` 에러

**원인**: 세션 만료 또는 CSRF 공격

**해결**:
- 브라우저 새로고침 후 다시 로그인
- 쿠키가 비활성화되어 있는지 확인
- 시크릿 모드에서 테스트

### 3. `ClassNotFoundException` 에러

**원인**: 부모 프로젝트가 빌드되지 않음

**해결**:
```bash
cd ../..
mvn clean install -DskipTests
cd examples/spring-boot-basic
mvn clean package
```

### 4. 환경 변수가 로드되지 않음

**해결**:
```bash
# 환경 변수 확인
echo $NAVER_CLIENT_ID

# 수동으로 설정
export NAVER_CLIENT_ID=your_client_id
export NAVER_CLIENT_SECRET=your_client_secret

# 또는 IntelliJ/Eclipse의 Run Configuration에서 Environment Variables 설정
```

## 다음 단계

이 예제를 이해했다면 다음 예제로 넘어가세요:

1. **[multi-provider](../multi-provider/)** - 여러 OAuth 제공자 동시 지원
2. **[redis-storage](../redis-storage/)** - Redis 토큰 저장소 사용

## 추가 학습 자료

- [사용자 가이드](../../docs/USER_GUIDE.md)
- [Naver Connector README](../../oauth-connector/connector-naver/README.md)
- [FAQ](../../docs/FAQ.md)

## 라이선스

Apache License 2.0

---

**질문이나 문제가 있으신가요?**
[GitHub Issues](https://github.com/archmagece-backyard/sb-oauth-java/issues)에 남겨주세요!
