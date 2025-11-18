# Multi-Provider Example

**난이도**: ⭐⭐ 중급
**소요 시간**: 30분
**상태**: 📝 계획 중 (Coming Soon)

여러 OAuth 제공자(Naver, Kakao, Google)를 동시에 지원하는 예제입니다.

## 예정된 기능

### 지원 OAuth 제공자

- ✅ Naver OAuth 2.0
- ✅ Kakao OAuth 2.0
- ✅ Google OAuth 2.0 / OIDC

### 주요 기능

1. **통합 로그인 페이지**
   - 사용자가 원하는 Provider 선택
   - 각 Provider별 로그인 버튼

2. **Provider별 설정 관리**
   ```java
   @Bean
   public Map<String, OAuth2GenerateAuthorizeEndpointFunction> authFunctions() {
       Map<String, OAuth2GenerateAuthorizeEndpointFunction> map = new HashMap<>();
       map.put("naver", naverAuthFunction);
       map.put("kakao", kakaoAuthFunction);
       map.put("google", googleAuthFunction);
       return map;
   }
   ```

3. **통합 사용자 관리**
   - Provider별 사용자 정보 통합
   - 동일 이메일 계정 연동
   - Provider별 프로필 형식 표준화

4. **Redis 토큰 저장소**
   - 분산 환경 지원
   - 토큰 자동 만료
   - Connection Pooling

### 프로젝트 구조 (계획)

```
multi-provider/
├── pom.xml
├── README.md
├── .env.example
├── docker-compose.yml                          # Redis
└── src/
    └── main/
        ├── java/com/example/oauth/
        │   ├── Application.java
        │   ├── config/
        │   │   ├── OAuthConfig.java            # 다중 Provider 설정
        │   │   └── RedisConfig.java            # Redis 설정
        │   ├── controller/
        │   │   ├── OAuthController.java        # 통합 OAuth 컨트롤러
        │   │   └── HomeController.java
        │   ├── service/
        │   │   ├── OAuthService.java           # Provider 추상화
        │   │   └── UserService.java            # 사용자 관리
        │   └── model/
        │       ├── UnifiedUser.java            # 통합 사용자 모델
        │       └── OAuthProvider.java          # Provider enum
        └── resources/
            ├── application.yml
            └── templates/
                ├── index.html                   # 다중 로그인 버튼
                └── profile.html                 # 통합 프로필 페이지
```

### 환경 변수 (.env.example)

```env
# Naver
NAVER_CLIENT_ID=your_naver_client_id
NAVER_CLIENT_SECRET=your_naver_client_secret

# Kakao
KAKAO_CLIENT_ID=your_kakao_rest_api_key
KAKAO_CLIENT_SECRET=your_kakao_client_secret_optional

# Google
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# Server
SERVER_PORT=8080
```

### 통합 사용자 모델

```java
@Data
public class UnifiedUser {
    private String userId;              // 내부 사용자 ID
    private String email;               // 공통: 이메일
    private String name;                // 공통: 이름
    private String profileImage;        // 공통: 프로필 이미지
    private OAuthProvider provider;     // Provider (NAVER, KAKAO, GOOGLE)
    private String providerUserId;      // Provider별 사용자 ID

    // Provider별 고유 필드는 Map으로 저장
    private Map<String, Object> additionalFields;
}
```

### Provider별 차이점 처리

| 항목 | Naver | Kakao | Google |
|------|-------|-------|--------|
| **Scope 구분자** | `,` (comma) | 공백 | 공백 |
| **client_secret** | 필수 | 선택 | 필수 |
| **Refresh Token** | 영구 | 60일 (갱신) | 영구 (선택) |
| **OIDC** | ❌ | ❌ | ✅ |
| **프로필 필드** | response.* | kakao_account.* | JWT claims |

### Docker Compose

```yaml
version: '3.8'
services:
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data
    command: redis-server --appendonly yes

volumes:
  redis-data:
```

## 학습 목표

이 예제를 완료하면 다음을 배울 수 있습니다:

1. **Provider 추상화**
   - 여러 OAuth Provider를 통합하는 방법
   - Provider별 특이사항을 처리하는 패턴

2. **토큰 저장소**
   - Redis를 사용한 분산 토큰 관리
   - Connection Pooling 설정

3. **사용자 관리**
   - Provider별 사용자 정보 통합
   - 계정 연동 (Account Linking)

4. **에러 처리**
   - Provider별 에러 응답 처리
   - 공통 에러 처리 로직

## 다음 단계

이 예제가 완성되면 [redis-storage](../redis-storage/) 예제로 넘어가세요.

## 기여

이 예제 구현에 관심이 있으신가요?

1. Fork this repository
2. Implement the example following this README
3. Submit Pull Request

---

**📝 Note**: 이 예제는 현재 계획 단계입니다. 구현에 참여하고 싶으시다면 [GitHub Issues](https://github.com/archmagece-backyard/sb-oauth-java/issues)에 의견을 남겨주세요!

## 대안: 직접 구현하기

### 1단계: spring-boot-basic 예제 확장

```bash
cd ../spring-boot-basic
cp -r . ../my-multi-provider
cd ../my-multi-provider
```

### 2단계: Kakao와 Google connector 추가

**pom.xml에 추가:**
```xml
<dependency>
    <groupId>org.scriptonbasestar.oauth</groupId>
    <artifactId>oauth-connector-kakao</artifactId>
    <version>${sb-oauth.version}</version>
</dependency>
<dependency>
    <groupId>org.scriptonbasestar.oauth</groupId>
    <artifactId>oauth-connector-google</artifactId>
    <version>${sb-oauth.version}</version>
</dependency>
```

### 3단계: 설정 추가

[사용자 가이드](../../docs/USER_GUIDE.md#사용-사례-1-다중-oauth-제공자-지원)의 Multi-Provider 섹션을 참고하세요.

---

**Status**: Coming Soon
**Last Updated**: 2025-01-18
