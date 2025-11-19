# sb-oauth-java

[![Release](https://img.shields.io/badge/release-v1.0.0-blue.svg)](https://github.com/ScriptonBasestar-io/sb-oauth-java/releases/tag/v1.0.0)
[![Java CI](https://github.com/ScriptonBasestar-io/sb-oauth-java/actions/workflows/ci.yml/badge.svg)](https://github.com/ScriptonBasestar-io/sb-oauth-java/actions/workflows/ci.yml)
[![CodeQL](https://github.com/ScriptonBasestar-io/sb-oauth-java/actions/workflows/codeql.yml/badge.svg)](https://github.com/ScriptonBasestar-io/sb-oauth-java/actions/workflows/codeql.yml)
[![Coverage](https://img.shields.io/badge/coverage-90%25-brightgreen.svg)](https://github.com/ScriptonBasestar-io/sb-oauth-java/actions/workflows/coverage.yml)
[![Java Version](https://img.shields.io/badge/Java-21-blue)](https://adoptium.net/)
[![Maven Central](https://img.shields.io/badge/Maven%20Central-1.0.0-brightgreen)](https://search.maven.org/)
[![JavaDoc](https://img.shields.io/badge/JavaDoc-Online-green.svg)](https://scriptonbasestar-io.github.io/sb-oauth-java/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

> 🌐 **Languages**: [English](README.md) | 한국어

프로덕션 환경에 최적화된 Java OAuth 2.0 클라이언트 라이브러리

네이버, 카카오 등 한국 OAuth 제공자와 Google, Facebook 등 글로벌 제공자를 기본 지원하는 안전하고 완성도 높은 OAuth 2.0 클라이언트 라이브러리입니다.

## 📋 목차

- [주요 기능](#-주요-기능)
- [빠른 시작](#-빠른-시작)
- [문서](#-문서)
- [지원 OAuth 제공자](#-지원-oauth-제공자)
- [시스템 요구사항](#️-시스템-요구사항)
- [설치 방법](#-설치-방법)
- [사용 예제](#-사용-예제)
- [보안](#-보안)
- [기여하기](#-기여하기)
- [라이선스](#-라이선스)

## ✨ 주요 기능

### 핵심 기능
- ☕ **최신 Java 21**: Virtual Threads 등 최신 언어 기능 지원
- 🔒 **프로덕션 수준 보안**: 종합적인 보안 유틸리티 및 OWASP 준수
- ⚡ **고성능**: 최적화된 처리량 (~5,000 state 생성/초)
- 🎯 **타입 안전 API**: 직관적이고 타입 안전한 OAuth 2.0 플로우
- 🌐 **다중 제공자 지원**: 네이버, 카카오, Google, Facebook 및 확장 가능한 구조
- 🚀 **Spring Boot 자동 구성**: Spring Boot Starter로 설정 없이 바로 사용

### 보안 기능 (v1.0.0)
- 🛡️ **SecureStateGenerator**: 암호학적으로 안전한 CSRF 방어 (256비트 엔트로피)
- 🔐 **RedirectUriValidator**: 화이트리스트 기반 오픈 리다이렉트 공격 방지
- 📝 **SensitiveDataMaskingUtil**: 로그에서 민감 정보 자동 마스킹 (OWASP 준수)
- ⚠️ **풍부한 예외 계층**: 상세한 오류 컨텍스트를 가진 18개 예외 클래스
- 🔍 **보안 스캔**: CodeQL 및 OWASP Dependency Check 자동화

### 품질 및 테스트
- ✅ **400+ 단위 테스트**: 종합적인 테스트 커버리지 (90%+)
- 📊 **JaCoCo 커버리지**: Codecov 통합으로 지속적인 커버리지 추적
- 🧪 **테스트 프레임워크**: JUnit 5, AssertJ, Mockito로 견고한 테스트
- 🔄 **CI/CD 파이프라인**: 자동화된 테스트, 보안 스캔 및 배포

### 문서화
- 📚 **API 문서**: 사용 예제가 포함된 완전한 JavaDoc
- 📖 **프로덕션 가이드**: 종합적인 프로덕션 배포 가이드 (936줄)
- 🔒 **보안 정책**: 상세한 보안 설정 및 모범 사례 (871줄)
- 🚢 **배포 가이드**: Docker, Kubernetes, CI/CD 설정 (1,277줄)

> 📝 **참고**: OAuth 1.0a는 지원하지 않습니다. 대부분의 플랫폼이 OAuth 2.0으로 전환했습니다.

## 🚀 빠른 시작

### Maven 의존성

**Spring Boot Starter** (권장):
```xml
<dependency>
    <groupId>org.scriptonbasestar.oauth</groupId>
    <artifactId>integration-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

**Core 라이브러리**:
```xml
<dependency>
    <groupId>org.scriptonbasestar.oauth</groupId>
    <artifactId>oauth-client</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle 의존성

```gradle
// Spring Boot Starter
implementation 'org.scriptonbasestar.oauth:integration-spring-boot-starter:1.0.0'

// Core 라이브러리
implementation 'org.scriptonbasestar.oauth:oauth-client:1.0.0'
```

### 설정 (application.yml)

```yaml
oauth:
  providers:
    naver:
      client-id: ${NAVER_CLIENT_ID}
      client-secret: ${NAVER_CLIENT_SECRET}
      redirect-uri: http://localhost:8080/oauth/callback/naver
    kakao:
      client-id: ${KAKAO_CLIENT_ID}
      client-secret: ${KAKAO_CLIENT_SECRET}
      redirect-uri: http://localhost:8080/oauth/callback/kakao
```

### 간단한 사용법 (3단계)

```java
// 1. 자동 구성된 빈 주입
@Autowired
private OAuth2NaverGenerateAuthorizeEndpointFunction authFunction;

@Autowired
private OAuth2NaverAccesstokenFunction tokenFunction;

// 2. 인증 URL 생성
State state = stateGenerator.generate();
String authUrl = authFunction.generate(state);
response.sendRedirect(authUrl);

// 3. 코드를 토큰으로 교환
OAuth2NaverTokenRes token = tokenFunction.issue(new Verifier(code), state);
String accessToken = token.getAccessToken();
```

Spring Boot Starter를 사용하면 별도 설정 없이 바로 사용 가능합니다!

## 📚 문서

### 가이드

- 📖 **[프로덕션 가이드](PRODUCTION_GUIDE.md)** - 프로덕션 배포, 성능 튜닝, 모니터링
- 🔒 **[보안 정책](SECURITY.md)** - 보안 설정, 취약점 방지, 규정 준수
- 🚢 **[배포 가이드](DEPLOYMENT.md)** - Docker, Kubernetes, CI/CD 파이프라인
- 📋 **[변경 이력](CHANGELOG.md)** - 릴리스 노트 및 버전 히스토리

### API 문서

- 📚 **[JavaDoc (온라인)](https://scriptonbasestar-io.github.io/sb-oauth-java/)** - 완전한 API 문서
- 📝 **[사용자 가이드](docs/USER_GUIDE.md)** - 초보자를 위한 단계별 튜토리얼
- 🏗️ **[아키텍처](docs/ARCHITECTURE.md)** - 내부 아키텍처 및 설계 철학
- ❓ **[FAQ](docs/FAQ.md)** - 자주 묻는 질문 및 문제 해결

### 예제

- 🎯 **[spring-boot-basic](examples/spring-boot-basic/)** - 기본 네이버 OAuth 예제
- 🔐 **[spring-boot-security-enhanced](examples/spring-boot-security-enhanced/)** - 프로덕션 수준 보안 설정

## 📦 지원 OAuth 제공자

| 제공자 | 문서 | 애플리케이션 등록 |
|--------|------|-------------------|
| **네이버** | [개발 가이드](https://developers.naver.com) | [내 애플리케이션](https://developers.naver.com/apps/#/myapps) |
| **카카오** | [REST API](https://developers.kakao.com/docs/restapi/user-management) | [내 애플리케이션](https://developers.kakao.com/console/app) |
| **Google** | [OAuth 2.0](https://developers.google.com/identity/protocols/oauth2) | [Cloud Console](https://console.developers.google.com) |
| **Facebook** | [로그인 문서](https://developers.facebook.com/docs/facebook-login) | [앱 대시보드](https://developers.facebook.com/apps) |

## ⚙️ 시스템 요구사항

- **Java**: 21 이상
- **Maven**: 3.9.x 이상
- **Spring Boot**: 3.4.x (Spring Boot Starter 사용 시)

## 💻 설치 방법

### Maven 사용

`pom.xml`에 추가:

```xml
<dependencies>
    <!-- Spring Boot Starter (자동 구성) -->
    <dependency>
        <groupId>org.scriptonbasestar.oauth</groupId>
        <artifactId>integration-spring-boot-starter</artifactId>
        <version>1.0.0</version>
    </dependency>

    <!-- Storage Backend (하나 선택) -->
    <!-- Redis (프로덕션 권장) -->
    <dependency>
        <groupId>org.scriptonbasestar.oauth</groupId>
        <artifactId>storage-redis</artifactId>
        <version>1.0.0</version>
    </dependency>

    <!-- 또는 Ehcache (단일 서버 배포용) -->
    <!--
    <dependency>
        <groupId>org.scriptonbasestar.oauth</groupId>
        <artifactId>storage-ehcache</artifactId>
        <version>1.0.0</version>
    </dependency>
    -->
</dependencies>
```

### Gradle 사용

`build.gradle`에 추가:

```gradle
dependencies {
    // Spring Boot Starter (자동 구성)
    implementation 'org.scriptonbasestar.oauth:integration-spring-boot-starter:1.0.0'

    // Storage Backend (하나 선택)
    implementation 'org.scriptonbasestar.oauth:storage-redis:1.0.0'
    // 또는
    // implementation 'org.scriptonbasestar.oauth:storage-ehcache:1.0.0'
}
```

## 📖 사용 예제

### 기본 예제

전체 작동 예제는 [examples/spring-boot-basic](examples/spring-boot-basic/)를 참조하세요.

```java
@RestController
public class OAuthController {

    @Autowired
    private OAuth2NaverGenerateAuthorizeEndpointFunction naverAuthFunction;

    @Autowired
    private OAuth2NaverAccesstokenFunction naverTokenFunction;

    @Autowired
    private StateGenerator stateGenerator;

    // 1단계: OAuth 제공자로 리다이렉트
    @GetMapping("/oauth/naver/login")
    public void login(HttpServletResponse response) throws IOException {
        State state = stateGenerator.generate();
        String authUrl = naverAuthFunction.generate(state);
        response.sendRedirect(authUrl);
    }

    // 2단계: 콜백 처리
    @GetMapping("/oauth/callback/naver")
    public String callback(@RequestParam String code, @RequestParam String state) {
        OAuth2NaverTokenRes token = naverTokenFunction.issue(
            new Verifier(code),
            new State(state)
        );
        return "Access Token: " + token.getAccessToken();
    }
}
```

### 보안 강화 예제

프로덕션 수준 보안 설정은 [examples/spring-boot-security-enhanced](examples/spring-boot-security-enhanced/)를 참조하세요.

```java
@Configuration
public class SecurityConfig {

    @Bean
    public StateGenerator stateGenerator() {
        // 프로덕션 최적화: 256비트 엔트로피, 타임스탬프 기반 만료
        return SecureStateGenerator.forProduction();
    }

    @Bean
    public RedirectUriValidator redirectUriValidator() {
        // 화이트리스트 검증, HTTPS 강제
        return new RedirectUriValidator(
            Set.of("https://yourdomain.com/oauth/callback"),
            false, // allowLocalhost
            true   // requireHttps
        );
    }
}
```

### 네이버 OAuth 전체 플로우

```java
@Service
public class NaverOAuthService {

    @Autowired
    private OAuth2NaverGenerateAuthorizeEndpointFunction authFunction;

    @Autowired
    private OAuth2NaverAccesstokenFunction tokenFunction;

    @Autowired
    private StateGenerator stateGenerator;

    // 1. 로그인 URL 생성
    public String getLoginUrl() {
        State state = stateGenerator.generate();
        // state를 세션에 저장 (CSRF 방지)
        sessionStorage.save(state);
        return authFunction.generate(state);
    }

    // 2. 콜백 처리 및 토큰 발급
    public UserInfo handleCallback(String code, String state) {
        // state 검증
        State savedState = sessionStorage.retrieve(state);
        if (savedState == null) {
            throw new InvalidStateException("Invalid state");
        }

        // 토큰 교환
        OAuth2NaverTokenRes token = tokenFunction.issue(
            new Verifier(code),
            savedState
        );

        // 사용자 정보 조회
        return getUserInfo(token.getAccessToken());
    }

    // 3. 사용자 정보 조회
    private UserInfo getUserInfo(String accessToken) {
        // 네이버 프로필 API 호출
        String profileUrl = "https://openapi.naver.com/v1/nid/me";
        // RestTemplate 또는 WebClient로 호출
        // ...
    }
}
```

### 카카오 OAuth 예제

```java
@Service
public class KakaoOAuthService {

    @Autowired
    private OAuth2KakaoGenerateAuthorizeEndpointFunction authFunction;

    @Autowired
    private OAuth2KakaoAccesstokenFunction tokenFunction;

    public String getLoginUrl() {
        State state = stateGenerator.generate();
        sessionStorage.save(state);
        return authFunction.generate(state);
    }

    public KakaoUserInfo handleCallback(String code, String state) {
        State savedState = sessionStorage.retrieve(state);

        OAuth2KakaoTokenRes token = tokenFunction.issue(
            new Verifier(code),
            savedState
        );

        return getKakaoUserInfo(token.getAccessToken());
    }
}
```

> 💡 **카카오 참고사항**: 카카오는 client_secret이 선택 사항입니다. Admin Key를 사용하는 경우에만 설정하세요.

## 🔒 보안

### 보안 기능

- **CSRF 방어**: 암호학적으로 안전한 state 파라미터 (256비트 엔트로피)
- **오픈 리다이렉트 방지**: 화이트리스트 기반 리다이렉트 URI 검증
- **안전한 로깅**: 로그에서 민감 데이터 자동 마스킹
- **HTTPS 강제**: 프로덕션 환경 SSL/TLS 설정
- **OWASP 준수**: OWASP Top 10 보안 가이드라인 준수
- **OAuth 2.0 Security BCP**: [RFC 6749](https://tools.ietf.org/html/rfc6749) 및 [Security Best Current Practice](https://tools.ietf.org/html/draft-ietf-oauth-security-topics) 준수

### 취약점 보고

취약점 보고 절차는 [SECURITY.md](SECURITY.md)를 참조하세요.

**이메일**: security@scriptonbasestar.org

## 🏗️ 아키텍처

### 모듈 구조

```
sb-oauth-java/
├── oauth-client/              # 핵심 OAuth 클라이언트
├── oauth-connector/           # 제공자 구현
│   ├── connector-naver/       # 네이버 OAuth 커넥터
│   ├── connector-kakao/       # 카카오 OAuth 커넥터
│   ├── connector-google/      # Google OAuth 커넥터
│   └── connector-facebook/    # Facebook OAuth 커넥터
├── oauth-storage/             # 스토리지 백엔드
│   ├── storage-redis/         # Redis 스토리지
│   └── storage-ehcache/       # Ehcache 스토리지
├── oauth-integration/         # 프레임워크 통합
│   └── integration-spring-boot-starter/  # Spring Boot 스타터
└── examples/                  # 예제 애플리케이션
```

상세한 아키텍처 문서는 [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)를 참조하세요.

## 🤝 기여하기

기여를 환영합니다! 가이드라인은 [CONTRIBUTING.md](CONTRIBUTING.md)를 참조하세요.

### 개발 환경 설정

1. **저장소 복제**:
   ```bash
   git clone https://github.com/ScriptonBasestar-io/sb-oauth-java.git
   cd sb-oauth-java
   ```

2. **프로젝트 빌드**:
   ```bash
   mvn clean install
   ```

3. **테스트 실행**:
   ```bash
   mvn test
   ```

4. **커버리지 리포트 생성**:
   ```bash
   mvn jacoco:report
   ```

## 📝 라이선스

이 프로젝트는 Apache License 2.0 라이선스를 따릅니다. 자세한 내용은 [LICENSE](LICENSE) 파일을 참조하세요.

## 🌟 Star History

이 프로젝트가 유용하다면 ⭐를 눌러주세요!

[![Star History Chart](https://api.star-history.com/svg?repos=ScriptonBasestar-io/sb-oauth-java&type=Date)](https://star-history.com/#ScriptonBasestar-io/sb-oauth-java&Date)

## 📧 연락처

- **GitHub Issues**: [Issues](https://github.com/ScriptonBasestar-io/sb-oauth-java/issues)
- **이메일**: support@scriptonbasestar.org
- **웹사이트**: https://scriptonbasestar.org

---

**Made with ❤️ by [ScriptonBaseStar](https://github.com/ScriptonBasestar-io)**
