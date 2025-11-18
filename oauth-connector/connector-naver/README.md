# oauth-connector-naver

Naver OAuth 2.0 connector for Korean market applications.

## Features

- ✅ Naver Login (네이버 로그인)
- ✅ Profile and email scope support
- ✅ Refresh token support (영구 유효)
- ✅ User profile API integration

## Quick Start

### 1. Register Your Application

1. Visit [Naver Developers](https://developers.naver.com/apps/#/myapps)
2. Create new application
3. Configure:
   - **Callback URL**: `http://localhost:8080/oauth/naver/callback`
   - **API permissions**: 회원이름, 이메일주소, 프로필사진
4. Get your **Client ID** and **Client Secret**

### 2. Add Dependency

```xml
<dependency>
    <groupId>org.scriptonbasestar.oauth</groupId>
    <artifactId>oauth-connector-naver</artifactId>
    <version>sb-oauth-20181219-3-DEV</version>
</dependency>
```

### 3. Basic Usage

```java
import org.scripton.oauth.connector.naver.*;
import org.scriptonbasestar.oauth.client.*;
import org.scriptonbasestar.oauth.client.model.*;
import org.scriptonbasestar.oauth.client.nobi.*;
import org.scriptonbasestar.oauth.client.nobi.token.*;

// Configure Naver OAuth
OAuth2NaverConfig config = OAuth2NaverConfig.builder()
    .clientId("YOUR_CLIENT_ID")
    .clientSecret("YOUR_CLIENT_SECRET")
    .redirectUri("http://localhost:8080/oauth/naver/callback")
    .scope("profile,email")
    .build();

// Generate authorization URL
OAuth2NaverGenerateAuthorizeEndpointFunction authFunction =
    new OAuth2NaverGenerateAuthorizeEndpointFunction(config);
State state = new RandomStringStateGenerator().generate("NAVER");
String authUrl = authFunction.generate(state);

// Redirect user to authUrl
// User authorizes and is redirected back to your callback URL

// Issue access token from callback
Verifier code = new Verifier(request.getParameter("code"));
TokenExtractor<OAuth2NaverTokenRes> tokenExtractor =
    new JsonTokenExtractor<>(OAuth2NaverTokenRes.class);
TokenStorage tokenStorage = new LocalTokenStorage();

OAuth2NaverAccesstokenFunction tokenFunction =
    new OAuth2NaverAccesstokenFunction(config, tokenExtractor, tokenStorage);
OAuth2NaverTokenRes token = tokenFunction.issue(code, state);

System.out.println("Access Token: " + token.getAccessToken());
System.out.println("Refresh Token: " + token.getRefreshToken());
System.out.println("Expires in: " + token.getExpiresIn() + " seconds");

// Get user profile
OAuth2ResourceFunction<String> resourceFunction =
    new DefaultOAuth2ResourceFunction("https://openapi.naver.com/v1/nid/me");
String userProfile = resourceFunction.run(token.getAccessToken());
System.out.println("User Profile: " + userProfile);
```

## Configuration

### OAuth2NaverConfig

```java
OAuth2NaverConfig config = OAuth2NaverConfig.builder()
    .clientId("YOUR_CLIENT_ID")           // Required
    .clientSecret("YOUR_CLIENT_SECRET")   // Required (⚠️ cannot be omitted)
    .redirectUri("http://localhost:8080/callback")  // Required
    .scope("profile,email")               // Optional (default: profile)
    .build();
```

### Endpoints

- **Authorization**: `https://nid.naver.com/oauth2.0/authorize`
- **Token**: `https://nid.naver.com/oauth2.0/token`
- **User Info**: `https://openapi.naver.com/v1/nid/me`

## Scope Options

| Scope | Description | Required Permission |
|-------|-------------|---------------------|
| `profile` | Basic profile (nickname, age, gender) | 필수 동의 항목 |
| `email` | Email address | 선택 동의 항목 |
| `birthday` | Birthday (MM-DD) | 선택 동의 항목 |
| `name` | Real name | 선택 동의 항목 |

Example: `"profile,email,name"`

## Token Response

```java
OAuth2NaverTokenRes {
    String access_token;      // Access token
    String refresh_token;     // Refresh token (영구 유효)
    String token_type;        // "bearer"
    Integer expires_in;       // 3600 (1 hour)
}
```

## ⚠️ Important Notes

### Client Secret is Required

Unlike Kakao, Naver **requires** `client_secret` for all operations:

```java
// ❌ This will fail
OAuth2NaverConfig.builder()
    .clientId("CLIENT_ID")
    // Missing client_secret
    .build();

// ✅ Correct
OAuth2NaverConfig.builder()
    .clientId("CLIENT_ID")
    .clientSecret("CLIENT_SECRET")  // Required!
    .build();
```

### Refresh Token Never Expires

Naver refresh tokens are **permanent** and do not rotate:

```java
// Refresh token remains the same after refresh
OAuth2NaverTokenRes newToken = tokenFunction.refresh(oldRefreshToken);
// newToken.getRefreshToken() == oldRefreshToken ✅
```

⚠️ **Security Recommendation**: Store refresh tokens securely (encrypted DB, secure vault).

### Redirect URI Must Match Exactly

The redirect URI in your request must **exactly** match the one registered in Naver Developers console:

```java
// Registered: http://localhost:8080/callback
// ✅ Correct
.redirectUri("http://localhost:8080/callback")

// ❌ Wrong (query params must match)
.redirectUri("http://localhost:8080/callback?source=app")
```

### State Parameter Validation

Always validate the state parameter to prevent CSRF attacks:

```java
// Generate and store state
State state = stateGenerator.generate("NAVER");
stateStorage.save(state.getValue(), state);

// In callback
String receivedState = request.getParameter("state");
if (!stateStorage.isValid(receivedState)) {
    throw new OAuthAuthException("Invalid state parameter");
}
```

## User Profile Response

```json
{
  "resultcode": "00",
  "message": "success",
  "response": {
    "id": "32742776",
    "nickname": "OpenAPI",
    "profile_image": "https://...",
    "email": "user@naver.com",
    "name": "홍길동"
  }
}
```

Parse the response:

```java
String userProfileJson = resourceFunction.run(token.getAccessToken());
// Use Gson or Jackson to parse JSON
JsonObject json = JsonParser.parseString(userProfileJson).getAsJsonObject();
JsonObject response = json.getAsJsonObject("response");
String email = response.get("email").getAsString();
String nickname = response.get("nickname").getAsString();
```

## Complete Example

See [OAuth20NaverServiceExample.java](src/test/java/org/scripton/oauth/connector/naver/OAuth20NaverServiceExample.java) for a complete working example.

## Spring Boot Integration

```java
@Configuration
public class NaverOAuthConfig {

    @Bean
    public OAuth2NaverConfig naverConfig(
        @Value("${naver.client-id}") String clientId,
        @Value("${naver.client-secret}") String clientSecret,
        @Value("${naver.redirect-uri}") String redirectUri
    ) {
        return OAuth2NaverConfig.builder()
            .clientId(clientId)
            .clientSecret(clientSecret)
            .redirectUri(redirectUri)
            .scope("profile,email")
            .build();
    }

    @Bean
    public OAuth2NaverAccesstokenFunction naverTokenFunction(
        OAuth2NaverConfig config,
        TokenStorage tokenStorage
    ) {
        TokenExtractor<OAuth2NaverTokenRes> extractor =
            new JsonTokenExtractor<>(OAuth2NaverTokenRes.class);
        return new OAuth2NaverAccesstokenFunction(config, extractor, tokenStorage);
    }
}
```

**application.yml**:
```yaml
naver:
  client-id: ${NAVER_CLIENT_ID}
  client-secret: ${NAVER_CLIENT_SECRET}
  redirect-uri: https://yourdomain.com/oauth/naver/callback
```

## Testing

### Configuration File

Create `~/.devenv/oauth/NAVER.cfg`:

```properties
client_id=YOUR_CLIENT_ID
client_secret=YOUR_CLIENT_SECRET
redirect_uri=http://localhost:8080/oauth/naver/callback
scope=profile,email
resource_profile_uri=https://openapi.naver.com/v1/nid/me
```

### Run Example

```bash
mvn test -Dtest=OAuth20NaverServiceExample -pl oauth-connector/connector-naver
```

## Troubleshooting

### Error: "invalid_client"

**Cause**: client_secret is missing or incorrect

**Solution**: Verify your client_secret in Naver Developers console

### Error: "redirect_uri_mismatch"

**Cause**: Redirect URI doesn't match the registered one

**Solution**: Ensure exact match including protocol (http/https) and query parameters

### Error: "invalid_grant" on refresh

**Cause**: Refresh token is invalid or expired (unlikely, as Naver refresh tokens don't expire)

**Solution**: Re-authenticate user to get new refresh token

## API Reference

See [Naver Login API Documentation](https://developers.naver.com/docs/login/api/) for official documentation.

## License

Apache License 2.0
