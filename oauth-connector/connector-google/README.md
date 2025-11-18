# oauth-connector-google

Google OAuth 2.0 / OpenID Connect (OIDC) connector for sb-oauth-java.

## Features

- ✅ Authorization Code Grant
- ✅ Token Refresh
- ✅ Token Revocation
- ✅ OpenID Connect (OIDC) support
- ✅ Scoped permissions (profile, email, etc.)
- ✅ Global availability

## When to Use

Use Google OAuth when:
- Building apps for global users
- Need verified email addresses
- Want single sign-on with Google accounts
- Require OIDC ID tokens
- Building Android/iOS apps with Google Sign-In

## Installation

### Maven Dependency

```xml
<dependency>
    <groupId>org.scriptonbasestar.oauth</groupId>
    <artifactId>oauth-connector-google</artifactId>
    <version>sb-oauth-20181219-3-DEV</version>
</dependency>
```

### Google Cloud Console Setup

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing project
3. Enable **Google+ API** or **People API**
4. Go to **APIs & Services** → **Credentials**
5. Click **Create Credentials** → **OAuth 2.0 Client ID**
6. Configure OAuth consent screen if not done
7. Select **Web application**
8. Add authorized redirect URIs (e.g., `http://localhost:8080/oauth/callback/google`)
9. Copy **Client ID** and **Client Secret**

## Usage

### Basic Configuration

```java
import org.scripton.oauth.connector.google.OAuth2GoogleConfig;
import org.scripton.oauth.connector.google.OAuth2GoogleGenerateAuthorizeUrlFunction;
import org.scripton.oauth.connector.google.OAuth2GoogleAccessTokenEndpointFunction;
import org.scriptonbasestar.oauth.client.type.OAuthHttpVerb;

OAuth2GoogleConfig config = new OAuth2GoogleConfig(
    "YOUR_CLIENT_ID.apps.googleusercontent.com",
    "YOUR_CLIENT_SECRET",
    "http://localhost:8080/oauth/callback/google",
    "https://accounts.google.com/o/oauth2/v2/auth",
    "profile email",
    "https://oauth2.googleapis.com/token",
    OAuthHttpVerb.POST,
    "https://oauth2.googleapis.com/revoke"
);
```

### Generate Authorization URL

```java
import org.scriptonbasestar.oauth.client.nobi.state.RandomStringStateGenerator;
import org.scriptonbasestar.oauth.client.model.State;

// Generate state for CSRF protection
State state = new RandomStringStateGenerator().generate("GOOGLE");

// Create authorize URL generator
OAuth2GoogleGenerateAuthorizeUrlFunction authorizeFunction =
    new OAuth2GoogleGenerateAuthorizeUrlFunction(config);

// Generate URL
String authorizeUrl = authorizeFunction.generate(state);

// Redirect user to this URL
// https://accounts.google.com/o/oauth2/v2/auth?
//   response_type=code&
//   client_id=...&
//   redirect_uri=...&
//   scope=profile+email&
//   state=...
```

### Exchange Code for Token

```java
import org.scripton.oauth.connector.google.OAuth2GoogleTokenRes;
import org.scriptonbasestar.oauth.client.model.Verifier;
import org.scriptonbasestar.oauth.client.nobi.LocalTokenStorage;
import org.scriptonbasestar.oauth.client.nobi.token.JsonTokenExtractor;
import com.fasterxml.jackson.core.type.TypeReference;

// Setup token extractor and storage
TokenExtractor<OAuth2GoogleTokenRes> tokenExtractor =
    new JsonTokenExtractor<>(new TypeReference<OAuth2GoogleTokenRes>() {});
TokenStorage tokenStorage = new LocalTokenStorage();

// Create token function
OAuth2GoogleAccessTokenEndpointFunction tokenFunction =
    new OAuth2GoogleAccessTokenEndpointFunction(config, tokenExtractor, tokenStorage);

// Exchange authorization code for token
Verifier verifier = new Verifier("AUTHORIZATION_CODE_FROM_CALLBACK");
OAuth2GoogleTokenRes tokenRes = tokenFunction.issue(verifier, state);

// Access token details
String accessToken = tokenRes.getAccess_token();
Integer expiresIn = tokenRes.getExpires_in();        // 3600 seconds (1 hour)
String refreshToken = tokenRes.getRefresh_token();
String scope = tokenRes.getScope();
String tokenType = tokenRes.getToken_type();         // "Bearer"
String idToken = tokenRes.getId_token();             // OIDC ID Token (JWT)
```

### Refresh Access Token

```java
// Refresh when access token expires
Token refreshTokenObj = new Token(refreshToken);
OAuth2GoogleTokenRes newTokenRes = tokenFunction.refresh(refreshTokenObj);

// New tokens
String newAccessToken = newTokenRes.getAccess_token();
// Note: Google may or may not return a new refresh_token
// If not returned, continue using the old refresh token
```

### Revoke Token

```java
// Revoke access token (invalidates both access and refresh tokens)
Token accessTokenObj = new Token(accessToken);
OAuth2GoogleTokenRes revokeRes = tokenFunction.revoke(accessTokenObj);
```

### Fetch User Profile

```java
import org.scriptonbasestar.oauth.client.DefaultOAuth2ResourceFunction;

// Create resource function
OAuth2ResourceFunction<String> resourceFunction =
    new DefaultOAuth2ResourceFunction("https://www.googleapis.com/oauth2/v2/userinfo");

// Fetch user profile
String profile = resourceFunction.fetch(accessToken);

// Example response:
// {
//   "id": "1234567890",
//   "email": "user@example.com",
//   "verified_email": true,
//   "name": "John Doe",
//   "given_name": "John",
//   "family_name": "Doe",
//   "picture": "https://lh3.googleusercontent.com/...",
//   "locale": "en"
// }
```

## Google OAuth Specifics

### Scopes

Common scopes for Google OAuth:

| Scope | Description |
|-------|-------------|
| `openid` | OIDC identifier (required for ID token) |
| `profile` | User's basic profile info (name, picture) |
| `email` | User's email address |
| `https://www.googleapis.com/auth/userinfo.profile` | Full profile access |
| `https://www.googleapis.com/auth/userinfo.email` | Email access |
| `https://www.googleapis.com/auth/calendar.readonly` | Read calendar events |
| `https://www.googleapis.com/auth/drive.readonly` | Read Google Drive files |

**Usage:**
```java
// Multiple scopes (space-separated)
String scope = "openid profile email";
```

See [OAuth 2.0 Scopes for Google APIs](https://developers.google.com/identity/protocols/oauth2/scopes) for full list.

### Token Lifetimes

| Token Type | Lifetime | Rotation |
|------------|----------|----------|
| **Access Token** | 1 hour | N/A |
| **Refresh Token** | Until revoked | Sometimes rotates |
| **ID Token** | 1 hour | N/A |

⚠️ **Refresh Token Rotation**: Google may return a new refresh token during refresh. If no new refresh token is returned, keep using the old one.

### OIDC ID Token

Google returns an `id_token` (JWT) containing user identity claims:

```java
// Parse ID token (JWT)
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

String idToken = tokenRes.getId_token();
DecodedJWT jwt = JWT.decode(idToken);

String userId = jwt.getSubject();                    // User ID
String email = jwt.getClaim("email").asString();
boolean emailVerified = jwt.getClaim("email_verified").asBoolean();
String name = jwt.getClaim("name").asString();
```

### Access Token vs ID Token

| Use Case | Use |
|----------|-----|
| **Call Google APIs** | Access Token (in Authorization header) |
| **Identify user** | ID Token (parse JWT claims) |
| **Backend authentication** | ID Token (verify signature) |

### Redirect URI Restrictions

⚠️ **Important**: Google enforces strict redirect URI matching:
- Must match **exactly** (including trailing slash)
- Supports `http://localhost` for development
- Production must use `https://` (except localhost)
- Cannot use IP addresses (except `127.0.0.1`)
- Cannot use wildcards

**Example:**
```java
// Registered in Google Console
redirect_uri = "https://example.com/oauth/callback"

// ✅ Match
redirect_uri = "https://example.com/oauth/callback"

// ❌ Mismatch (trailing slash)
redirect_uri = "https://example.com/oauth/callback/"

// ❌ Mismatch (different path)
redirect_uri = "https://example.com/oauth/google/callback"
```

## Spring Boot Integration

```java
@Configuration
public class GoogleOAuthConfig {

    @Value("${oauth.google.client-id}")
    private String clientId;

    @Value("${oauth.google.client-secret}")
    private String clientSecret;

    @Value("${oauth.google.redirect-uri}")
    private String redirectUri;

    @Bean
    public OAuth2GoogleConfig googleConfig() {
        return new OAuth2GoogleConfig(
            clientId,
            clientSecret,
            redirectUri,
            "https://accounts.google.com/o/oauth2/v2/auth",
            "openid profile email",
            "https://oauth2.googleapis.com/token",
            OAuthHttpVerb.POST,
            "https://oauth2.googleapis.com/revoke"
        );
    }

    @Bean
    public OAuth2GenerateAuthorizeEndpointFunction googleAuthorizeFunction(
        OAuth2GoogleConfig config
    ) {
        return new OAuth2GoogleGenerateAuthorizeUrlFunction(config);
    }

    @Bean
    public OAuth2AccessTokenEndpointFunction<OAuth2GoogleTokenRes> googleTokenFunction(
        OAuth2GoogleConfig config,
        TokenStorage tokenStorage
    ) {
        TokenExtractor<OAuth2GoogleTokenRes> extractor =
            new JsonTokenExtractor<>(new TypeReference<OAuth2GoogleTokenRes>() {});
        return new OAuth2GoogleAccessTokenEndpointFunction(config, extractor, tokenStorage);
    }
}
```

**application.yml:**
```yaml
oauth:
  google:
    client-id: ${GOOGLE_CLIENT_ID}
    client-secret: ${GOOGLE_CLIENT_SECRET}
    redirect-uri: http://localhost:8080/oauth/callback/google
```

## Complete Example

```java
public class GoogleOAuthExample {
    public static void main(String[] args) {
        // 1. Configuration
        OAuth2GoogleConfig config = new OAuth2GoogleConfig(
            "123456.apps.googleusercontent.com",
            "GOCSPX-abc123...",
            "http://localhost:8080/callback",
            "https://accounts.google.com/o/oauth2/v2/auth",
            "openid profile email",
            "https://oauth2.googleapis.com/token",
            OAuthHttpVerb.POST,
            "https://oauth2.googleapis.com/revoke"
        );

        // 2. Generate authorization URL
        State state = new RandomStringStateGenerator().generate("GOOGLE");
        OAuth2GoogleGenerateAuthorizeUrlFunction authorizeFunction =
            new OAuth2GoogleGenerateAuthorizeUrlFunction(config);
        String url = authorizeFunction.generate(state);

        System.out.println("Visit: " + url);

        // 3. User authorizes and returns with code
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter code from callback: ");
        String code = scanner.nextLine();

        // 4. Exchange code for tokens
        TokenExtractor<OAuth2GoogleTokenRes> extractor =
            new JsonTokenExtractor<>(new TypeReference<OAuth2GoogleTokenRes>() {});
        TokenStorage storage = new LocalTokenStorage();

        OAuth2GoogleAccessTokenEndpointFunction tokenFunction =
            new OAuth2GoogleAccessTokenEndpointFunction(config, extractor, storage);

        OAuth2GoogleTokenRes tokenRes = tokenFunction.issue(new Verifier(code), state);

        // 5. Use access token
        System.out.println("Access Token: " + tokenRes.getAccess_token());
        System.out.println("ID Token: " + tokenRes.getId_token());

        // 6. Fetch user profile
        OAuth2ResourceFunction<String> resourceFunction =
            new DefaultOAuth2ResourceFunction("https://www.googleapis.com/oauth2/v2/userinfo");
        String profile = resourceFunction.fetch(tokenRes.getAccess_token());
        System.out.println("Profile: " + profile);
    }
}
```

## Comparison: Google vs Naver vs Kakao

| Feature | Google | Naver | Kakao |
|---------|--------|-------|-------|
| **Region** | 🌍 Global | 🇰🇷 Korea | 🇰🇷 Korea |
| **OIDC** | ✅ Yes | ❌ No | ❌ No |
| **client_secret** | ✅ Required | ✅ Required | ⚠️ Optional |
| **Access Token TTL** | 1 hour | 1 hour | 6 hours |
| **Refresh Token TTL** | Until revoked | Permanent | 60 days |
| **Refresh Token Rotation** | Sometimes | Never | Always |
| **Revoke Support** | ✅ Yes | ❌ No | ✅ Yes |
| **Email Verified** | ✅ Yes | ⚠️ Manual | ⚠️ Manual |
| **Use Case** | Global B2C/B2B | Korean B2C | Korean B2C |

## Troubleshooting

### Error: redirect_uri_mismatch

**Cause**: Redirect URI doesn't match exactly with registered URI

**Solution**:
1. Check Google Cloud Console → Credentials → OAuth 2.0 Client IDs
2. Ensure redirect URI matches exactly (including protocol, path, trailing slash)
3. Add all required redirect URIs (dev, staging, production)

### Error: invalid_grant

**Cause**: Authorization code expired or already used

**Solution**:
- Authorization codes expire after 10 minutes
- Codes are single-use only
- Generate a new authorization URL and try again

### Error: invalid_client

**Cause**: Invalid client_id or client_secret

**Solution**:
- Verify credentials from Google Cloud Console
- Check for typos or whitespace
- Ensure project is not disabled

### Refresh Token Not Returned

**Cause**: Google only returns refresh token on first authorization

**Solution**:
- Revoke access: https://myaccount.google.com/permissions
- Or add `access_type=offline` and `prompt=consent` to authorization URL
- Re-authorize to get new refresh token

### ID Token Validation Failed

**Cause**: Invalid signature or expired token

**Solution**:
```java
// Verify ID token signature
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

String idToken = tokenRes.getId_token();
DecodedJWT jwt = JWT.decode(idToken);

// Check expiration
if (jwt.getExpiresAt().before(new Date())) {
    throw new RuntimeException("ID token expired");
}

// Check audience
if (!jwt.getAudience().contains(clientId)) {
    throw new RuntimeException("Invalid audience");
}
```

## Additional Resources

- [Google OAuth 2.0 Documentation](https://developers.google.com/identity/protocols/oauth2)
- [OpenID Connect](https://developers.google.com/identity/protocols/oauth2/openid-connect)
- [OAuth 2.0 Scopes](https://developers.google.com/identity/protocols/oauth2/scopes)
- [People API](https://developers.google.com/people)
- [Google Sign-In for Websites](https://developers.google.com/identity/sign-in/web)

## License

Apache License 2.0
