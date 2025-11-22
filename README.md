# sb-oauth-java

[![Java CI](https://github.com/ScriptonBasestar-io/sb-oauth-java/actions/workflows/ci.yml/badge.svg)](https://github.com/ScriptonBasestar-io/sb-oauth-java/actions/workflows/ci.yml)
[![Java Version](https://img.shields.io/badge/Java-21%20LTS-blue)](https://adoptium.net/)
[![Test Coverage](https://img.shields.io/badge/Coverage-40%25-green)](https://github.com/ScriptonBasestar-io/sb-oauth-java)
[![Maven Central](https://img.shields.io/badge/Maven%20Central-2.0.0-brightgreen)](https://search.maven.org/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Code Quality](https://img.shields.io/badge/Quality-A-brightgreen)](https://github.com/ScriptonBasestar-io/sb-oauth-java)

자바 OAuth 2.0 Client 라이브러리

**주요 특징:**
- ☕ **Modern Java**: Java 21 LTS 공식 지원
- 🔒 **보안 강화**: 최신 의존성 및 보안 패치 적용
- ⚡ **HttpClient 5.x**: 향상된 성능 및 HTTP/2 지원
- 🎯 **간단한 API**: 직관적인 OAuth 2.0 플로우 구현
- 🌐 **다중 제공자**: Naver, Kakao, Google, Facebook 지원
- ✅ **높은 품질**: 40% 테스트 커버리지, 정적 분석 통과

> 📝 OAuth 1.0a는 지원하지 않습니다. 대부분의 플랫폼이 OAuth 2.0으로 전환했습니다.

## 🎉 최근 개선사항 (2025-01)

### Java 21 완전 지원
- ✅ Java 21 LTS로 마이그레이션 완료
- ✅ SpotBugs 4.9.x 업그레이드 (Java 21 호환)
- ✅ Apache HttpClient 5.x 최신 API 적용
- ✅ Jackson PropertyNamingStrategies 최신 API 사용

### 코드 품질 개선
- ✅ Lombok 의존성 제거 (수동 구현으로 전환)
- ✅ Checkstyle 위반 0개 (1,809개 → 0개)
- ✅ Deprecated API 제거 (HttpClient, Jackson)
- ✅ Unchecked operation 경고 제거
- ✅ SpotBugs UUF_UNUSED_FIELD 경고 제거

### 테스트 강화
- ✅ 테스트 커버리지 18% → 40% (+22%)
- ✅ 총 119개 단위 테스트 (68개 → 119개)
- ✅ 핵심 OAuth 클래스 전체 테스트 커버리지
- ✅ 엣지 케이스 및 에러 처리 테스트 추가

### 버그 수정
- ✅ OAuth20Constants.REFRESH_TOKEN typo 수정 (`refesh_token` → `refresh_token`)

## 모듈 설명

```text
run
oauth-client <- oauth-storage <- oauth-connector 

test
oauth-client <- test-helper <- oauth-connector-* 
```
oauth spec
https://tools.ietf.org/html/rfc6749


## 📦 지원 OAuth 제공자

| Provider | 문서 | 애플리케이션 등록 |
|----------|------|-------------------|
| **Naver** | [개발 가이드](https://developers.naver.com) | [내 애플리케이션](https://developers.naver.com/apps/#/myapps) |
| **Kakao** | [REST API](https://developers.kakao.com/docs/restapi/user-management) | [내 애플리케이션](https://developers.kakao.com/console/app) |
| **Google** | [OAuth 2.0](https://developers.google.com/identity/protocols/oauth2) | [Cloud Console](https://console.developers.google.com) |
| **Facebook** | [로그인 문서](https://developers.facebook.com/docs/facebook-login) | [앱 대시보드](https://developers.facebook.com/apps) |

## ⚙️ 시스템 요구사항

- **Java**: 17 이상 (권장: Java 21 LTS)
- **Maven**: 3.9.x 이상
- **Build Tool**: Maven 또는 Gradle


## 🚀 빠른 시작

### Maven 의존성 추가

```xml
<dependency>
    <groupId>org.scriptonbasestar.oauth</groupId>
    <artifactId>oauth-connector-naver</artifactId>
    <version>2.0.0</version>
</dependency>
```

### Gradle 의존성 추가

```gradle
implementation 'org.scriptonbasestar.oauth:oauth-connector-naver:2.0.0'
```

### 기본 사용 예제

```java
import org.scriptonbasestar.oauth.client.*;
import org.scriptonbasestar.oauth.client.model.*;
import org.scripton.oauth.connector.naver.*;

public class NaverOAuthExample {
    public static void main(String[] args) {
        // 1. OAuth 설정
        OAuth2NaverConfig config = OAuth2NaverConfig.builder()
            .clientId("YOUR_CLIENT_ID")
            .clientSecret("YOUR_CLIENT_SECRET")
            .redirectUri("http://localhost:8080/callback")
            .scope("profile,email")
            .build();

        // 2. 인증 URL 생성
        OAuth2NaverGenerateAuthorizeEndpointFunction authFunction =
            new OAuth2NaverGenerateAuthorizeEndpointFunction(config);

        State state = new RandomStringStateGenerator().generate("NAVER");
        String authUrl = authFunction.generate(state);

        System.out.println("인증 URL: " + authUrl);

        // 3. 사용자 인증 후 받은 code로 액세스 토큰 발급
        Verifier code = new Verifier("RECEIVED_CODE_FROM_CALLBACK");

        OAuth2NaverAccesstokenFunction tokenFunction =
            new OAuth2NaverAccesstokenFunction(config, tokenExtractor, tokenStorage);

        OAuth2NaverTokenRes token = tokenFunction.issue(code, state);

        System.out.println("Access Token: " + token.getAccessToken());

        // 4. 사용자 정보 조회
        OAuth2ResourceFunction<String> resourceFunction =
            new DefaultOAuth2ResourceFunction(config.getResourceProfileUri());

        String userProfile = resourceFunction.run(token.getAccessToken());
        System.out.println("User Profile: " + userProfile);
    }
}
```

### Spring Boot 통합 예제

```java
@Configuration
public class OAuth2Config {

    @Bean
    public OAuth2NaverConfig naverConfig() {
        return OAuth2NaverConfig.builder()
            .clientId("${oauth.naver.client-id}")
            .clientSecret("${oauth.naver.client-secret}")
            .redirectUri("${oauth.naver.redirect-uri}")
            .scope("profile,email")
            .build();
    }

    @Bean
    public OAuth2NaverAccesstokenFunction naverTokenFunction(
            OAuth2NaverConfig config,
            TokenExtractor<OAuth2NaverTokenRes> tokenExtractor,
            TokenStorage tokenStorage) {
        return new OAuth2NaverAccesstokenFunction(config, tokenExtractor, tokenStorage);
    }
}
```

### 설정 파일 예제

테스트를 위한 OAuth 설정 파일: `~/.devenv/oauth/NAVER.cfg`

```properties
client_id=YOUR_CLIENT_ID
client_secret=YOUR_CLIENT_SECRET
redirect_uri=http://localhost:8080/oauth/naver/callback
scope=profile,email
resource_profile_uri=https://openapi.naver.com/v1/nid/me
```

> 💡 **Kakao 참고사항**: Kakao는 client_secret이 선택적입니다. Admin Key를 사용하는 경우 추가하세요.

## exit()

oauth 프로토콜은 공통코드를 쓸 수 없지 않을까

스펙이 너무 다양해서 로그인 프로세스나 파라미터도 제가각이다.
사실상 표준화 실패로 봐야하지 않나.
로그인 및 access_token 획득까지만 처리. 이후엔 해당 사이트별로 처리.

어차피 진짜 미친듯이 많아야 열개, 보통 2~4개 할건데 각 사이트 개별 SDK 사용하는것도 괜찮을 것 같다.
