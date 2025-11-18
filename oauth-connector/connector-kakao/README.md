# oauth-connector-kakao

Kakao OAuth 2.0 connector for Korean market applications.

## Features

- ✅ Kakao Login (카카오 로그인)
- ✅ KakaoTalk profile integration
- ✅ Optional client_secret support
- ✅ Refresh token support
- ✅ User profile and email scope

## Quick Start

### 1. Register Your Application

1. Visit [Kakao Developers](https://developers.kakao.com/console/app)
2. Create new application
3. Configure:
   - **Redirect URI**: `http://localhost:8080/oauth/kakao/callback`
   - **Activate** Kakao Login
   - **Consent Items**: Profile, Email
4. Get your **REST API Key** (= client_id)
5. (Optional) Get **Admin Key** for server-to-server authentication

### 2. Add Dependency

```xml
<dependency>
    <groupId>org.scriptonbasestar.oauth</groupId>
    <artifactId>oauth-connector-kakao</artifactId>
    <version>sb-oauth-20181219-3-DEV</version>
</dependency>
```

### 3. Basic Usage

```java
import org.scripton.oauth.connector.kakao.*;

// Option 1: REST API Key only (most common)
OAuth2KakaoConfig config = OAuth2KakaoConfig.builder()
    .clientId("YOUR_REST_API_KEY")
    .redirectUri("http://localhost:8080/oauth/kakao/callback")
    .scope("profile_nickname,profile_image,account_email")
    .build();

// Option 2: With Admin Key (for enhanced security)
OAuth2KakaoConfig config = OAuth2KakaoConfig.builder()
    .clientId("YOUR_REST_API_KEY")
    .clientSecret("YOUR_ADMIN_KEY")  // Optional
    .redirectUri("http://localhost:8080/oauth/kakao/callback")
    .scope("profile_nickname,profile_image,account_email")
    .build();

// Generate authorization URL
OAuth2KakaoGenerateAuthorizeEndpointFunction authFunction =
    new OAuth2KakaoGenerateAuthorizeEndpointFunction(config);
State state = new RandomStringStateGenerator().generate("KAKAO");
String authUrl = authFunction.generate(state);

// Issue access token from callback
Verifier code = new Verifier(request.getParameter("code"));
TokenExtractor<OAuth2KakaoTokenRes> tokenExtractor =
    new JsonTokenExtractor<>(OAuth2KakaoTokenRes.class);

OAuth2KakaoAccesstokenFunction tokenFunction =
    new OAuth2KakaoAccesstokenFunction(config, tokenExtractor, tokenStorage);
OAuth2KakaoTokenRes token = tokenFunction.issue(code, state);

// Get user profile
OAuth2ResourceFunction<String> resourceFunction =
    new DefaultOAuth2ResourceFunction("https://kapi.kakao.com/v2/user/me");
String userProfile = resourceFunction.run(token.getAccessToken());
```

## Configuration

### OAuth2KakaoConfig

```java
OAuth2KakaoConfig config = OAuth2KakaoConfig.builder()
    .clientId("REST_API_KEY")            // Required
    .clientSecret("ADMIN_KEY")           // Optional (권장)
    .redirectUri("http://localhost:8080/callback")  // Required
    .scope("profile_nickname,account_email")  // Optional
    .build();
```

### Endpoints

- **Authorization**: `https://kauth.kakao.com/oauth/authorize`
- **Token**: `https://kauth.kakao.com/oauth/token`
- **User Info**: `https://kapi.kakao.com/v2/user/me`

## Scope Options

| Scope | Description | Required |
|-------|-------------|----------|
| `profile_nickname` | Nickname | No |
| `profile_image` | Profile image URL | No |
| `account_email` | Email address | No |
| `openid` | OpenID Connect | No |

Example: `"profile_nickname,profile_image,account_email"`

⚠️ **Note**: Consent items must be **activated** in Kakao Developers console.

## ⚠️ Important Differences from Naver

### Client Secret is Optional

```java
// ✅ Without client_secret (REST API Key only)
OAuth2KakaoConfig.builder()
    .clientId("REST_API_KEY")
    // No client_secret
    .build();

// ✅ With client_secret (Admin Key - recommended for security)
OAuth2KakaoConfig.builder()
    .clientId("REST_API_KEY")
    .clientSecret("ADMIN_KEY")
    .build();
```

**When to use Admin Key:**
- Server-to-server communication
- Enhanced security requirements
- Production environments

### Token Response

```java
OAuth2KakaoTokenRes {
    String access_token;
    String token_type;        // "bearer"
    String refresh_token;
    Integer expires_in;       // 21600 (6 hours)
    Integer refresh_token_expires_in;  // 5184000 (60 days)
    String scope;
}
```

### Refresh Token Expiration

Unlike Naver (permanent), Kakao refresh tokens expire after **60 days**.

```java
// Refresh token rotates on refresh
OAuth2KakaoTokenRes newToken = tokenFunction.refresh(oldRefreshToken);
// newToken.getRefreshToken() != oldRefreshToken ⚠️
```

💡 **Recommendation**: Store the new refresh token after each refresh.

## User Profile Response

```json
{
  "id": 123456789,
  "connected_at": "2024-01-01T00:00:00Z",
  "properties": {
    "nickname": "홍길동",
    "profile_image": "https://...",
    "thumbnail_image": "https://..."
  },
  "kakao_account": {
    "profile_nickname_needs_agreement": false,
    "profile": {
      "nickname": "홍길동",
      "thumbnail_image_url": "https://...",
      "profile_image_url": "https://..."
    },
    "has_email": true,
    "email_needs_agreement": false,
    "is_email_valid": true,
    "is_email_verified": true,
    "email": "user@kakao.com"
  }
}
```

## Spring Boot Integration

```java
@Configuration
public class KakaoOAuthConfig {

    @Bean
    public OAuth2KakaoConfig kakaoConfig(
        @Value("${kakao.rest-api-key}") String restApiKey,
        @Value("${kakao.admin-key:}") String adminKey,
        @Value("${kakao.redirect-uri}") String redirectUri
    ) {
        OAuth2KakaoConfig.OAuth2KakaoConfigBuilder builder = OAuth2KakaoConfig.builder()
            .clientId(restApiKey)
            .redirectUri(redirectUri)
            .scope("profile_nickname,profile_image,account_email");

        if (adminKey != null && !adminKey.isEmpty()) {
            builder.clientSecret(adminKey);
        }

        return builder.build();
    }
}
```

**application.yml**:
```yaml
kakao:
  rest-api-key: ${KAKAO_REST_API_KEY}
  admin-key: ${KAKAO_ADMIN_KEY}  # Optional
  redirect-uri: https://yourdomain.com/oauth/kakao/callback
```

## Testing

Create `~/.devenv/oauth/KAKAO.cfg`:

```properties
client_id=YOUR_REST_API_KEY
client_secret=YOUR_ADMIN_KEY
redirect_uri=http://localhost:8080/oauth/kakao/callback
scope=profile_nickname,profile_image,account_email
```

## Troubleshooting

### Error: "KOE006" (consent required)

**Cause**: User has not consented to required scope

**Solution**: Activate consent items in Kakao Developers console

### Error: "invalid_scope"

**Cause**: Requested scope is not activated

**Solution**: Activate scopes in "Kakao Login → Consent Items"

### Error: "invalid_grant" on refresh

**Cause**: Refresh token expired (60 days) or already used

**Solution**: Re-authenticate user

## Comparison: Kakao vs Naver

| Feature | Kakao | Naver |
|---------|-------|-------|
| **client_secret** | Optional (Admin Key) | Required |
| **Access token TTL** | 6 hours | 1 hour |
| **Refresh token TTL** | 60 days | Permanent |
| **Refresh token rotation** | Yes | No |
| **OpenID Connect** | Yes | No |

## API Reference

See [Kakao Login Documentation](https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api) for official documentation.

## License

Apache License 2.0
