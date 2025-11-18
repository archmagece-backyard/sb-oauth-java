# 자주 묻는 질문 (FAQ)

sb-oauth-java 사용 시 자주 묻는 질문과 답변입니다.

## 목차

1. [일반](#일반)
2. [설정 및 설치](#설정-및-설치)
3. [OAuth 플로우](#oauth-플로우)
4. [에러 처리](#에러-처리)
5. [토큰 관리](#토큰-관리)
6. [보안](#보안)
7. [성능](#성능)
8. [프로덕션 배포](#프로덕션-배포)

---

## 일반

### Q: sb-oauth-java는 무엇인가요?

**A:** Java 21 기반 OAuth 2.0 클라이언트 라이브러리로, 특히 **한국 OAuth 제공자**(Naver, Kakao)를 지원합니다. Spring Security OAuth의 대안으로 사용할 수 있으며, 각 OAuth 제공자의 특이사항을 명시적으로 처리합니다.

### Q: 어떤 OAuth 제공자를 지원하나요?

**A:** 현재 지원:
- ✅ Naver
- ✅ Kakao
- ✅ Google
- ✅ Facebook

계획 중:
- LINE, PAYCO, Toss, GitHub, Apple 등

새로운 제공자는 [CONTRIBUTING.md](../CONTRIBUTING.md)를 참고하여 기여할 수 있습니다.

### Q: Spring Security OAuth와 무엇이 다른가요?

**A:** 주요 차이점:

| 특징 | Spring Security OAuth | sb-oauth-java |
|------|----------------------|---------------|
| **유지보수** | ❌ EOL (2022) | ✅ 활발 |
| **한국 OAuth** | ❌ 직접 구현 필요 | ✅ 기본 지원 |
| **Spring 의존성** | ✅ 필수 | ❌ 선택적 |
| **설정 복잡도** | 높음 | 낮음 |
| **Provider 특화** | 일반적 | 특화됨 |

자세한 내용은 [MIGRATION.md](MIGRATION.md)를 참고하세요.

### Q: OAuth 1.0a를 지원하나요?

**A:** 아니요, OAuth 2.0만 지원합니다. OAuth 1.0a는 더 이상 권장되지 않으며, 대부분의 서비스가 OAuth 2.0으로 전환했습니다.

### Q: 라이선스는 무엇인가요?

**A:** Apache License 2.0입니다. 상업적 사용이 가능하며, 소스 코드 공개 의무가 없습니다.

---

## 설정 및 설치

### Q: 최소 Java 버전은?

**A:** **Java 21 이상**이 필요합니다.

이유:
- Record 클래스 활용
- Pattern Matching
- Virtual Threads 대비

Java 8-20을 사용 중이라면 이전 버전(sb-oauth-20181219-2)을 사용하거나, Java 21로 업그레이드하세요.

### Q: Maven Central에 배포되어 있나요?

**A:** 현재는 **미배포** 상태입니다. GitHub Packages 또는 로컬 설치를 사용하세요:

```bash
git clone https://github.com/archmagece-backyard/sb-oauth-java.git
cd sb-oauth-java
mvn clean install
```

Maven Central 배포는 계획 중입니다.

### Q: Spring Boot를 사용하지 않아도 되나요?

**A:** 네, **Spring Boot 없이도 사용 가능**합니다. oauth-client와 connector 모듈만 추가하면 됩니다:

```xml
<dependency>
    <groupId>org.scriptonbasestar.oauth</groupId>
    <artifactId>oauth-connector-naver</artifactId>
    <version>sb-oauth-20181219-3-DEV</version>
</dependency>
```

Spring Boot 통합은 선택적입니다 (`oauth-integration-spring-boot`).

### Q: Gradle을 사용할 수 있나요?

**A:** 네, Gradle도 지원합니다:

```gradle
dependencies {
    implementation 'org.scriptonbasestar.oauth:oauth-client:sb-oauth-20181219-3-DEV'
    implementation 'org.scriptonbasestar.oauth:oauth-connector-naver:sb-oauth-20181219-3-DEV'
}
```

### Q: 어떤 모듈을 선택해야 하나요?

**A:**

**필수:**
- `oauth-client` - 핵심 라이브러리

**OAuth Provider (하나 이상 선택):**
- `oauth-connector-naver` - Naver
- `oauth-connector-kakao` - Kakao
- `oauth-connector-google` - Google
- `oauth-connector-facebook` - Facebook

**토큰 저장소 (하나 선택):**
- 개발: `LocalTokenStorage` (코드로 직접 생성)
- 단일 서버: `oauth-storage-ehcache`
- 다중 서버: `oauth-storage-redis`

---

## OAuth 플로우

### Q: State는 왜 필요한가요?

**A:** **CSRF(Cross-Site Request Forgery) 공격 방지**를 위해 필수입니다.

```java
// 1. State 생성 및 저장
State state = new RandomStringStateGenerator().generate("NAVER");
session.setAttribute("oauth_state", state.getValue());

// 2. 콜백에서 검증
String savedState = (String) session.getAttribute("oauth_state");
if (!receivedState.equals(savedState)) {
    throw new SecurityException("CSRF attack detected");
}
```

State 없이는 악의적인 사용자가 다른 사용자의 인증을 가로챌 수 있습니다.

### Q: redirect_uri_mismatch 에러가 발생합니다

**A:** Redirect URI가 정확히 일치하지 않습니다.

**체크리스트:**
- [ ] Protocol 일치 (`http` vs `https`)
- [ ] Domain 일치 (`localhost` vs `127.0.0.1`)
- [ ] Port 일치 (`:8080` vs `:80`)
- [ ] Path 일치 (`/callback` vs `/callback/`)
- [ ] OAuth Provider Console에 등록된 URI와 정확히 동일

**예시:**
```java
// 코드
redirectUri = "http://localhost:8080/oauth/callback/naver"

// Naver Developers Console
✅ http://localhost:8080/oauth/callback/naver  // 정확히 일치
❌ http://localhost:8080/oauth/callback/naver/ // 끝에 / 있음
❌ https://localhost:8080/oauth/callback/naver // https vs http
```

### Q: 프로덕션에서 localhost를 사용할 수 없나요?

**A:** 네, 프로덕션에서는 **실제 도메인과 HTTPS**를 사용해야 합니다.

```java
// 개발
redirectUri = "http://localhost:8080/callback"

// 프로덕션
redirectUri = "https://example.com/oauth/callback"
```

대부분의 OAuth 제공자는 프로덕션 환경에서 HTTPS를 강제합니다.

### Q: 여러 Redirect URI를 사용할 수 있나요?

**A:** OAuth Provider Console에 **여러 개 등록 가능**하지만, **코드에서는 하나만 사용**해야 합니다.

```java
// Naver Console에 등록
http://localhost:8080/callback       // 개발
https://dev.example.com/callback     // 개발 서버
https://example.com/callback         // 프로덕션

// 코드에서는 환경에 맞게 하나만
OAuth2NaverConfig config = OAuth2NaverConfig.builder()
    .redirectUri(System.getenv("OAUTH_REDIRECT_URI"))  // 환경 변수로 관리
    .build();
```

### Q: Scope는 어떻게 선택하나요?

**A:** **필요한 권한만 최소한으로** 요청하세요.

**Naver:**
```java
scope = "profile"          // 기본 프로필만
scope = "profile,email"    // 프로필 + 이메일
```

**Kakao:**
```java
scope = "profile_nickname,profile_image"  // 닉네임, 이미지
scope = "account_email"                   // 이메일 (추가 승인 필요)
```

**Google:**
```java
scope = "openid profile email"  // OIDC + 프로필 + 이메일
```

불필요한 권한 요청은 사용자 신뢰도를 낮춥니다.

---

## 에러 처리

### Q: OAuthNetworkException이 발생합니다

**A:** OAuth Provider와 통신 중 네트워크 에러가 발생했습니다.

**원인:**
- OAuth Provider 서버 다운
- 네트워크 연결 문제
- 타임아웃

**해결:**
```java
try {
    OAuth2NaverTokenRes token = tokenFunction.issue(verifier, state);
} catch (OAuthNetworkException e) {
    // 재시도 로직
    logger.error("Network error: {}", e.getMessage());
    // 사용자에게 "일시적 오류, 다시 시도해주세요" 메시지
}
```

**재시도 로직:**
```java
public OAuth2NaverTokenRes issueWithRetry(Verifier verifier, State state) {
    int maxRetries = 3;
    for (int i = 0; i < maxRetries; i++) {
        try {
            return tokenFunction.issue(verifier, state);
        } catch (OAuthNetworkException e) {
            if (i == maxRetries - 1) {
                throw e;  // 마지막 시도 실패
            }
            Thread.sleep(1000 * (i + 1));  // 1초, 2초, 3초 대기
        }
    }
}
```

### Q: OAuthAuthException이 발생합니다

**A:** 인증 실패입니다.

**원인:**
- 잘못된 Client ID/Secret
- 만료된 Authorization Code
- 이미 사용한 Authorization Code
- 잘못된 Redirect URI

**해결:**
```java
try {
    OAuth2NaverTokenRes token = tokenFunction.issue(verifier, state);
} catch (OAuthAuthException e) {
    logger.error("Auth error: {}", e.getMessage());
    // 사용자를 다시 로그인 페이지로 리다이렉트
}
```

**Authorization Code 만료:**
- Authorization Code는 **10분** 내에 사용해야 합니다
- **일회용**입니다 (재사용 불가)

### Q: OAuthParsingException이 발생합니다

**A:** OAuth Provider 응답 파싱 실패입니다.

**원인:**
- API 응답 형식 변경
- 잘못된 TokenExtractor 사용

**해결:**
```java
// 1. 응답 내용 확인
try {
    OAuth2NaverTokenRes token = tokenFunction.issue(verifier, state);
} catch (OAuthParsingException e) {
    logger.error("Parsing error: {}", e.getMessage());
    logger.error("Response body: {}", e.getResponseBody());  // 원본 응답 확인
}

// 2. 커스텀 TokenExtractor 사용
TokenExtractor<OAuth2NaverTokenRes> extractor = new CustomTokenExtractor();
```

### Q: Invalid State 에러가 발생합니다

**A:** State 값이 일치하지 않습니다.

**원인:**
- State를 세션에 저장하지 않음
- 세션이 만료됨
- 여러 탭에서 동시 로그인 시도

**해결:**
```java
// 1. State 생성 시 저장
State state = stateGenerator.generate("NAVER");
session.setAttribute("oauth_state", state.getValue());

// 2. 콜백에서 검증
String savedState = (String) session.getAttribute("oauth_state");
if (savedState == null) {
    // 세션 만료
    return "redirect:/login";
}
if (!savedState.equals(receivedState)) {
    // State 불일치
    throw new SecurityException("Invalid state");
}

// 3. 검증 후 삭제 (재사용 방지)
session.removeAttribute("oauth_state");
```

---

## 토큰 관리

### Q: Access Token과 Refresh Token의 차이는?

**A:**

| 특징 | Access Token | Refresh Token |
|------|--------------|---------------|
| **용도** | API 호출 | Access Token 갱신 |
| **수명** | 짧음 (1-6시간) | 길음 (30-60일 또는 영구) |
| **재발급** | Refresh Token으로 | 재로그인 필요 |
| **노출 위험** | 높음 (매번 전송) | 낮음 (저장만) |

**사용 패턴:**
```java
// 1. 초기 발급
OAuth2NaverTokenRes token = tokenFunction.issue(verifier, state);
String accessToken = token.getAccess_token();
String refreshToken = token.getRefresh_token();

// 2. Access Token으로 API 호출
resourceFunction.fetch(accessToken);

// 3. Access Token 만료 시 갱신
OAuth2NaverTokenRes newToken = tokenFunction.refresh(new Token(refreshToken));
String newAccessToken = newToken.getAccess_token();
```

### Q: 토큰은 어디에 저장해야 하나요?

**A:** 환경에 따라 다릅니다:

**개발:**
- `LocalTokenStorage` (메모리)

**단일 서버:**
- `EhcacheTokenStorage` (메모리 + 디스크)
- Database (JPA)

**다중 서버 (권장):**
- `RedisTokenStorage` (분산 캐시)
- Database with caching

**클라이언트:**
- ❌ LocalStorage (XSS 위험)
- ✅ HttpOnly Cookie (권장)
- ✅ Session Storage

### Q: Refresh Token은 언제 갱신되나요?

**A:** **Provider마다 다릅니다:**

| Provider | Refresh Token 수명 | 갱신 시 변경 |
|----------|-------------------|--------------|
| **Naver** | 영구 | ❌ 변경 안 됨 |
| **Kakao** | 60일 | ✅ 변경됨 |
| **Google** | 취소 전까지 | ⚠️ 때때로 변경 |
| **Facebook** | ❌ 미지원 | N/A |

**처리 방법:**
```java
// Naver: 기존 refresh_token 재사용
OAuth2NaverTokenRes newToken = naverFunction.refresh(refreshToken);
// newToken.getRefresh_token() == refreshToken (동일)

// Kakao: 새 refresh_token 저장
OAuth2KakaoTokenRes newToken = kakaoFunction.refresh(refreshToken);
if (newToken.getRefresh_token() != null) {
    // 새 refresh_token 저장
    saveRefreshToken(userId, newToken.getRefresh_token());
}
```

### Q: Access Token이 만료되었는지 어떻게 확인하나요?

**A:** 두 가지 방법:

**1. expires_in 기준 (추천):**
```java
@Entity
public class UserOAuth {
    private String accessToken;
    private Long issuedAt;      // 발급 시간 (epoch seconds)
    private Integer expiresIn;  // 유효 시간 (seconds)

    public boolean isExpired() {
        long now = System.currentTimeMillis() / 1000;
        return (now - issuedAt) >= (expiresIn - 300);  // 5분 여유
    }
}
```

**2. API 호출 후 에러 처리:**
```java
try {
    String profile = resourceFunction.fetch(accessToken);
} catch (OAuthAuthException e) {
    // 401 Unauthorized → 토큰 만료
    OAuth2NaverTokenRes newToken = tokenFunction.refresh(new Token(refreshToken));
    accessToken = newToken.getAccess_token();
    // 재시도
    String profile = resourceFunction.fetch(accessToken);
}
```

### Q: Refresh Token도 만료되면 어떻게 하나요?

**A:** **재로그인이 필요**합니다.

```java
try {
    OAuth2NaverTokenRes newToken = tokenFunction.refresh(new Token(refreshToken));
} catch (OAuthAuthException e) {
    // Refresh Token 만료 또는 취소됨
    logger.warn("Refresh token expired for user: {}", userId);

    // 사용자를 로그인 페이지로 리다이렉트
    return "redirect:/login";
}
```

**만료 시간:**
- Naver: 영구 (만료 없음)
- Kakao: 60일
- Google: 취소하지 않으면 영구
- Facebook: 60일 (Long-lived token)

---

## 보안

### Q: Client Secret을 안전하게 관리하는 방법은?

**A:**

**✅ 권장:**
```java
// 1. 환경 변수
String clientSecret = System.getenv("NAVER_CLIENT_SECRET");

// 2. 외부 설정 파일 (gitignore)
// application-prod.yml (Git에 커밋하지 않음)
oauth:
  naver:
    client-secret: ${NAVER_CLIENT_SECRET}

// 3. Secret Management 서비스
// AWS Secrets Manager, HashiCorp Vault 등
```

**❌ 절대 하지 말 것:**
```java
// 코드에 하드코딩
String clientSecret = "abc123...";  // ❌ Git에 노출됨

// 프론트엔드에 노출
<script>
  const clientSecret = "abc123...";  // ❌ 누구나 볼 수 있음
</script>
```

### Q: 토큰을 암호화해야 하나요?

**A:** **저장 위치에 따라 다릅니다:**

**암호화 필요:**
- Database 저장
- Redis 저장 (네트워크 전송)
- 로그 파일

**암호화 불필요:**
- 메모리 (LocalTokenStorage, Ehcache)
- HTTPS 통신 (이미 암호화됨)

**암호화 예시:**
```java
public class EncryptedTokenStorage implements TokenStorage {

    private final TokenStorage delegate;
    private final AES256Cipher cipher;

    @Override
    public void store(String id, Token token) {
        String encrypted = cipher.encrypt(token.getValue());
        delegate.store(id, new Token(encrypted));
    }

    @Override
    public Token load(String id) {
        Token encrypted = delegate.load(id);
        String decrypted = cipher.decrypt(encrypted.getValue());
        return new Token(decrypted);
    }
}
```

### Q: HTTPS는 필수인가요?

**A:** **프로덕션에서는 필수**입니다.

**이유:**
- OAuth Provider가 HTTPS Redirect URI를 강제
- Token 탈취 방지
- MITM(Man-in-the-Middle) 공격 방지

**개발 환경:**
- `http://localhost` 허용 (대부분의 Provider)

**프로덕션:**
- `https://` 필수
- Let's Encrypt로 무료 SSL 인증서 발급 가능

### Q: Access Token을 URL에 포함해도 되나요?

**A:** **절대 안 됩니다.**

**❌ 위험:**
```
// URL에 토큰 포함
https://example.com/profile?access_token=ABC123

// 문제점:
// 1. 브라우저 히스토리에 저장됨
// 2. 서버 로그에 기록됨
// 3. Referer 헤더로 유출 가능
```

**✅ 안전:**
```
// Authorization 헤더 사용
GET /profile HTTP/1.1
Host: example.com
Authorization: Bearer ABC123
```

---

## 성능

### Q: 성능 최적화 팁은?

**A:**

**1. HTTP 연결 재사용:**
```java
// ✅ HttpClient 재사용
private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
    .version(HttpClient.Version.HTTP_2)
    .build();
```

**2. 토큰 캐싱:**
```java
@Cacheable(value = "accessTokens", key = "#userId")
public String getAccessToken(String userId) {
    // DB 조회 또는 갱신
}
```

**3. Connection Pooling (Redis):**
```java
JedisPoolConfig config = new JedisPoolConfig();
config.setMaxTotal(50);
config.setMaxIdle(10);
```

**4. 병렬 API 호출:**
```java
List<CompletableFuture<String>> futures = userIds.stream()
    .map(id -> CompletableFuture.supplyAsync(() -> fetchProfile(id)))
    .collect(Collectors.toList());
```

### Q: 몇 명의 사용자까지 처리할 수 있나요?

**A:** **저장소에 따라 다릅니다:**

| 저장소 | 최대 사용자 수 | 비고 |
|--------|----------------|------|
| `LocalTokenStorage` | ~10,000 | 메모리 제한 |
| `EhcacheTokenStorage` | ~100,000 | 디스크 오버플로우 사용 시 |
| `RedisTokenStorage` | 수백만 | Redis 클러스터 사용 시 |

**병목:**
- 저장소 I/O
- HTTP 연결 수
- 메모리 사용량

---

## 프로덕션 배포

### Q: 프로덕션 체크리스트는?

**A:**

**보안:**
- [ ] HTTPS 사용
- [ ] Client Secret 환경 변수로 관리
- [ ] State 검증 활성화
- [ ] 토큰 암호화 (DB 저장 시)
- [ ] CORS 설정
- [ ] Rate Limiting

**성능:**
- [ ] Redis 사용 (다중 서버)
- [ ] Connection Pooling 설정
- [ ] HTTP/2 활성화
- [ ] 토큰 캐싱
- [ ] 로그 레벨 조정 (ERROR/WARN)

**모니터링:**
- [ ] 로그 수집 (ELK, Splunk 등)
- [ ] 메트릭 수집 (Prometheus, Grafana)
- [ ] 알림 설정 (실패율 > 5%)
- [ ] Health Check 엔드포인트

**에러 처리:**
- [ ] 재시도 로직
- [ ] Circuit Breaker
- [ ] 사용자 친화적 에러 메시지
- [ ] 에러 로깅

### Q: Docker로 배포하는 방법은?

**A:**

**Dockerfile:**
```dockerfile
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/myapp.jar app.jar

ENV NAVER_CLIENT_ID=""
ENV NAVER_CLIENT_SECRET=""
ENV REDIS_HOST="redis"
ENV REDIS_PORT="6379"

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**docker-compose.yml:**
```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - NAVER_CLIENT_ID=${NAVER_CLIENT_ID}
      - NAVER_CLIENT_SECRET=${NAVER_CLIENT_SECRET}
      - REDIS_HOST=redis
    depends_on:
      - redis

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
```

**실행:**
```bash
docker-compose up -d
```

### Q: Kubernetes로 배포하는 방법은?

**A:**

**deployment.yaml:**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: oauth-app
spec:
  replicas: 3
  selector:
    matchLabels:
      app: oauth-app
  template:
    metadata:
      labels:
        app: oauth-app
    spec:
      containers:
      - name: app
        image: myapp:latest
        env:
        - name: NAVER_CLIENT_ID
          valueFrom:
            secretKeyRef:
              name: oauth-secrets
              key: naver-client-id
        - name: NAVER_CLIENT_SECRET
          valueFrom:
            secretKeyRef:
              name: oauth-secrets
              key: naver-client-secret
        - name: REDIS_HOST
          value: redis-service
```

**secret.yaml:**
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: oauth-secrets
type: Opaque
data:
  naver-client-id: <base64-encoded>
  naver-client-secret: <base64-encoded>
```

---

## 추가 질문

### Q: 여기에 없는 질문이 있어요

**A:** 다음 방법으로 도움을 받으세요:

1. **문서 확인:**
   - [사용자 가이드](USER_GUIDE.md)
   - [마이그레이션 가이드](MIGRATION.md)
   - [아키텍처 가이드](ARCHITECTURE.md)

2. **GitHub Issues:**
   - [기존 이슈 검색](https://github.com/archmagece-backyard/sb-oauth-java/issues)
   - 새 이슈 생성

3. **커뮤니티:**
   - GitHub Discussions

4. **코드 예제:**
   - `examples/` 디렉토리 참고

---

**도움이 되었나요?**
더 나은 FAQ를 위해 피드백을 남겨주세요!
