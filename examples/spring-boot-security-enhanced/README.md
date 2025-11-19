# Spring Boot Security Enhanced Example

OAuth 2.0 보안 Best Practices를 적용한 예제입니다.

## 🔒 보안 강화 기능

이 예제는 다음과 같은 보안 기능을 구현합니다:

### 1. 암호학적으로 안전한 CSRF State 생성
- `SecureStateGenerator` 사용
- 256-bit 랜덤 데이터 생성
- 타임스탬프 기반 만료 검증

### 2. Redirect URI 검증
- `RedirectUriValidator`를 통한 Open Redirect 방지
- 화이트리스트 기반 URI 검증
- Production/Development 모드 지원

### 3. 민감 정보 마스킹
- `SensitiveDataMaskingUtil`을 사용한 로그 보안
- Access Token, Client Secret 자동 마스킹
- 프로덕션 환경 안전 로깅

### 4. 체계적인 예외 처리
- OAuth2Exception 계층 사용
- 에러 코드 및 컨텍스트 정보 포함
- 사용자 친화적 에러 메시지

## 📋 전제 조건

- Java 21+
- Maven 3.8+
- Naver Developer 계정 및 등록된 앱

## 🚀 빠른 시작

### 1. Naver 앱 등록

1. https://developers.naver.com/apps/ 접속
2. **애플리케이션 등록** 클릭
3. **네이버 로그인** API 선택
4. Callback URL 설정:
   - Development: `http://localhost:8080/oauth/callback/naver`
   - Production: `https://yourdomain.com/oauth/callback/naver`
5. Client ID와 Client Secret 복사

### 2. 환경 변수 설정

`.env` 파일 생성:

```bash
cp .env.example .env
```

`.env` 파일 편집:

```properties
# Naver OAuth
NAVER_CLIENT_ID=your_client_id_here
NAVER_CLIENT_SECRET=your_client_secret_here

# Security Settings
OAUTH_STATE_MAX_AGE_SECONDS=600
OAUTH_REQUIRE_HTTPS=false
OAUTH_ALLOW_LOCALHOST=true
```

### 3. 애플리케이션 실행

```bash
# 부모 프로젝트 빌드 (처음만)
cd ../..
mvn clean install -DskipTests

# 예제 실행
cd examples/spring-boot-security-enhanced
mvn spring-boot:run
```

### 4. 브라우저에서 접속

```
http://localhost:8080
```

"Naver 로그인" 버튼을 클릭하여 OAuth 플로우를 시작하세요.

## 🏗️ 프로젝트 구조

```
spring-boot-security-enhanced/
├── pom.xml
├── README.md
├── .env.example
├── src/
│   ├── main/
│   │   ├── java/com/example/oauth/
│   │   │   ├── Application.java
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java       # 보안 설정
│   │   │   │   └── OAuth2Config.java         # OAuth 설정
│   │   │   ├── controller/
│   │   │   │   ├── HomeController.java
│   │   │   │   └── OAuthController.java      # 개선된 예외 처리
│   │   │   ├── service/
│   │   │   │   └── SecureOAuthService.java   # 보안 강화 서비스
│   │   │   └── exception/
│   │   │       └── GlobalExceptionHandler.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── logback-spring.xml             # 민감 정보 마스킹 로깅
│   │       └── templates/
│   │           ├── index.html
│   │           ├── profile.html
│   │           └── error.html
│   └── test/
│       └── java/                              # 통합 테스트
```

## 🔐 보안 기능 상세

### SecureStateGenerator

CSRF 공격을 방지하기 위한 암호학적으로 안전한 State 생성:

```java
@Bean
public StateGenerator stateGenerator() {
    return SecureStateGenerator.forProduction();  // 256-bit 랜덤
}
```

**특징:**
- `SecureRandom` 사용
- 32 bytes (256 bits) 랜덤 데이터
- Base64 URL-safe 인코딩
- 타임스탬프 포함으로 만료 검증 가능

### RedirectUriValidator

Open Redirect 취약점 방지:

```java
@Bean
public RedirectUriValidator redirectUriValidator() {
    if (isProduction) {
        return RedirectUriValidator.forProduction(
            "https://yourdomain.com/oauth/callback/naver"
        );
    } else {
        return RedirectUriValidator.forDevelopment(
            "http://localhost:8080/oauth/callback/naver"
        );
    }
}
```

**검증 항목:**
- URI 형식 검증 (scheme, host, path)
- 화이트리스트 확인
- HTTPS 강제 (프로덕션)
- localhost 허용 (개발)

### SensitiveDataMaskingUtil

로그에서 민감 정보 자동 마스킹:

```java
log.info("Token issued: {}",
    SensitiveDataMaskingUtil.maskAccessToken(tokenRes.getAccessToken()));
// 출력: Token issued: access_t***1234
```

**마스킹 대상:**
- Client Secret: `clie***cret`
- Access Token: `access_t***1234`
- Refresh Token: `refresh_***5678`
- Authorization Code: `author***code`

### OAuth2Exception Hierarchy

체계적인 에러 처리:

```java
try {
    validateState(expectedState, actualState);
} catch (InvalidStateException e) {
    log.error("CSRF validation failed - expected: {}, actual: {}",
        e.getExpectedState(), e.getActualState());
    throw e;
}
```

**예외 계층:**
- `OAuth2ConfigurationException` - 설정 오류
- `OAuth2AuthorizationException` - 인증 오류 (CSRF, invalid grant 등)
- `OAuth2TokenException` - 토큰 오류
- `OAuth2NetworkException` - 네트워크 오류

## 📊 로깅 전략

### 로그 레벨

- **DEBUG**: 상세 파라미터 (개발 환경)
- **INFO**: 주요 플로우 (토큰 발급, 사용자 로그인)
- **WARN**: 재시도, 만료 임박
- **ERROR**: 예외 상황

### 민감 정보 보호

`logback-spring.xml`에서 자동 마스킹:

```xml
<appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
        <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
    </encoder>
</appender>
```

모든 토큰과 시크릿은 애플리케이션 코드에서 마스킹되어 로그에 기록됩니다.

## 🧪 테스트

```bash
# 단위 테스트
mvn test

# 통합 테스트
mvn verify

# 특정 테스트만
mvn test -Dtest=SecureOAuthServiceTest
```

## 🚨 프로덕션 체크리스트

프로덕션 배포 전 확인사항:

- [ ] `OAUTH_REQUIRE_HTTPS=true` 설정
- [ ] `OAUTH_ALLOW_LOCALHOST=false` 설정
- [ ] Redirect URI를 HTTPS로 등록
- [ ] Client Secret을 환경 변수로 관리
- [ ] 로그 레벨을 INFO 이상으로 설정
- [ ] State 만료 시간 적절히 설정 (권장: 10분)
- [ ] Redis 등 외부 State 저장소 사용 (세션 클러스터링 시)

## 📚 추가 학습

### 관련 문서
- [보안 유틸리티 가이드](../../docs/SECURITY_UTILITIES.md)
- [예외 처리 가이드](../../docs/EXCEPTION_HANDLING.md)
- [로깅 Best Practices](../../docs/LOGGING.md)

### 다른 예제
- [기본 예제](../spring-boot-basic/) - 간단한 시작
- [멀티 프로바이더](../multi-provider/) - 여러 OAuth 제공자
- [Redis 저장소](../redis-storage/) - 분산 환경

## 🐛 문제 해결

### State 만료 에러

**증상**: `StateExpiredException: State has expired`

**해결**:
1. `OAUTH_STATE_MAX_AGE_SECONDS` 값 증가
2. 사용자가 로그인 페이지에 너무 오래 머물지 않도록 안내

### Redirect URI 불일치

**증상**: `InvalidRedirectUriException: Invalid redirect URI`

**해결**:
1. Naver Developer Console의 Redirect URI와 정확히 일치하는지 확인
2. Protocol (http vs https), domain, port, path 모두 일치해야 함
3. 개발 환경에서는 `OAUTH_ALLOW_LOCALHOST=true` 설정

### CSRF 검증 실패

**증상**: `InvalidStateException: State mismatch`

**해결**:
1. 브라우저 쿠키가 활성화되어 있는지 확인
2. 세션이 유지되는지 확인 (로드 밸런서 sticky session 설정)
3. State가 중복 사용되지 않는지 확인 (한 번만 사용)

## 📝 라이센스

이 예제는 sb-oauth-java의 일부이며 동일한 라이센스를 따릅니다.

## 🤝 기여

버그를 발견하셨나요? 개선 아이디어가 있으신가요?

[GitHub Issues](https://github.com/archmagece-backyard/sb-oauth-java/issues)에 알려주세요!
