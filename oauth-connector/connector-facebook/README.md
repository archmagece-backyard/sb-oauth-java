# oauth-connector-facebook

Facebook OAuth 2.0 connector for sb-oauth-java (Graph API).

## Features

- ✅ Authorization Code Grant
- ✅ Token Refresh (using long-lived tokens)
- ✅ Scoped permissions (public_profile, email, etc.)
- ✅ Facebook Graph API integration
- ✅ Global availability

## When to Use

Use Facebook OAuth when:
- Building social apps requiring Facebook integration
- Need access to user's Facebook profile, friends, posts
- Want Facebook Login for easy sign-up
- Targeting global audience with Facebook accounts
- Building social sharing features

## Installation

### Maven Dependency

```xml
<dependency>
    <groupId>org.scriptonbasestar.oauth</groupId>
    <artifactId>oauth-connector-facebook</artifactId>
    <version>sb-oauth-20181219-3-DEV</version>
</dependency>
```

### Facebook App Setup

1. Go to [Facebook for Developers](https://developers.facebook.com/)
2. Click **My Apps** → **Create App**
3. Select app type (e.g., **Consumer**)
4. Fill in app details and create
5. Go to **Settings** → **Basic**
6. Copy **App ID** (client_id) and **App Secret** (client_secret)
7. Add **Facebook Login** product
8. Go to **Facebook Login** → **Settings**
9. Add **Valid OAuth Redirect URIs** (e.g., `http://localhost:8080/oauth/callback/facebook`)
10. Save changes

## Usage

### Basic Configuration

```java
import org.scripton.oauth.connector.facebook.OAuth2FacebookConfig;
import org.scripton.oauth.connector.facebook.OAuth2FacebookGenerateAuthorizeEndpointFunction;
import org.scripton.oauth.connector.facebook.OAuth2FacebookAccessTokenEndpointFunction;
import org.scriptonbasestar.oauth.client.type.OAuthHttpVerb;

OAuth2FacebookConfig config = new OAuth2FacebookConfig(
    "YOUR_APP_ID",
    "YOUR_APP_SECRET",
    "http://localhost:8080/oauth/callback/facebook",
    "https://www.facebook.com/v18.0/dialog/oauth",
    "public_profile,email",
    "https://graph.facebook.com/v18.0/oauth/access_token",
    OAuthHttpVerb.POST
);
```

⚠️ **API Version**: Replace `v18.0` with the latest Facebook Graph API version. See [Facebook API Changelog](https://developers.facebook.com/docs/graph-api/changelog).

### Generate Authorization URL

```java
import org.scriptonbasestar.oauth.client.nobi.state.RandomStringStateGenerator;
import org.scriptonbasestar.oauth.client.model.State;

// Generate state for CSRF protection
State state = new RandomStringStateGenerator().generate("FACEBOOK");

// Create authorize URL generator
OAuth2FacebookGenerateAuthorizeEndpointFunction authorizeFunction =
    new OAuth2FacebookGenerateAuthorizeEndpointFunction(config);

// Generate URL
String authorizeUrl = authorizeFunction.generate(state);

// Redirect user to this URL
// https://www.facebook.com/v18.0/dialog/oauth?
//   response_type=code&
//   client_id=...&
//   redirect_uri=...&
//   scope=public_profile,email&
//   state=...
```

### Exchange Code for Token

```java
import org.scripton.oauth.connector.facebook.OAuth2FacebookTokenRes;
import org.scriptonbasestar.oauth.client.model.Verifier;
import org.scriptonbasestar.oauth.client.nobi.LocalTokenStorage;
import org.scriptonbasestar.oauth.client.nobi.token.JsonTokenExtractor;
import com.fasterxml.jackson.core.type.TypeReference;

// Setup token extractor and storage
TokenExtractor<OAuth2FacebookTokenRes> tokenExtractor =
    new JsonTokenExtractor<>(new TypeReference<OAuth2FacebookTokenRes>() {});
TokenStorage tokenStorage = new LocalTokenStorage();

// Create token function
OAuth2FacebookAccessTokenEndpointFunction tokenFunction =
    new OAuth2FacebookAccessTokenEndpointFunction(config, tokenExtractor, tokenStorage);

// Exchange authorization code for token
Verifier verifier = new Verifier("AUTHORIZATION_CODE_FROM_CALLBACK");
OAuth2FacebookTokenRes tokenRes = tokenFunction.issue(verifier, state);

// Access token details
String accessToken = tokenRes.getAccess_token();
String tokenType = tokenRes.getToken_type();       // "bearer"
Integer expiresIn = tokenRes.getExpires_in();      // 5184000 (60 days for long-lived)
```

### Exchange Short-Lived Token for Long-Lived Token

Facebook initially provides a **short-lived token** (1 hour). Exchange it for a **long-lived token** (60 days):

```java
// After getting initial token, exchange for long-lived token
String longLivedTokenUrl = String.format(
    "https://graph.facebook.com/v18.0/oauth/access_token?" +
    "grant_type=fb_exchange_token&" +
    "client_id=%s&" +
    "client_secret=%s&" +
    "fb_exchange_token=%s",
    config.getClientId(),
    config.getClientSecret(),
    accessToken
);

// Make HTTP GET request to exchange token
// Response: { "access_token": "...", "token_type": "bearer", "expires_in": 5184000 }
```

### Fetch User Profile

```java
import org.scriptonbasestar.oauth.client.DefaultOAuth2ResourceFunction;

// Create resource function for Graph API
String profileUrl = "https://graph.facebook.com/v18.0/me?fields=id,name,email,picture";
OAuth2ResourceFunction<String> resourceFunction =
    new DefaultOAuth2ResourceFunction(profileUrl);

// Fetch user profile
String profile = resourceFunction.fetch(accessToken);

// Example response:
// {
//   "id": "1234567890",
//   "name": "John Doe",
//   "email": "john@example.com",
//   "picture": {
//     "data": {
//       "url": "https://platform-lookaside.fbsbx.com/..."
//     }
//   }
// }
```

### Fetch User's Posts

```java
// Fetch user's posts (requires user_posts permission)
String postsUrl = "https://graph.facebook.com/v18.0/me/posts?limit=10";
OAuth2ResourceFunction<String> postsFunction =
    new DefaultOAuth2ResourceFunction(postsUrl);

String posts = postsFunction.fetch(accessToken);
```

## Facebook OAuth Specifics

### Scopes (Permissions)

Facebook uses granular permissions. Request only what you need:

| Scope | Description | Review Required |
|-------|-------------|-----------------|
| `public_profile` | Basic profile (id, name, picture) | ❌ No |
| `email` | User's email address | ❌ No |
| `user_friends` | List of friends using the app | ✅ Yes |
| `user_posts` | User's posts and feed | ✅ Yes |
| `user_photos` | User's photos | ✅ Yes |
| `user_birthday` | User's birthday | ✅ Yes |
| `pages_read_engagement` | Read Page content | ✅ Yes |
| `publish_video` | Upload videos | ✅ Yes |

⚠️ **App Review**: Advanced permissions require Facebook's App Review process.

**Usage:**
```java
// Multiple scopes (comma-separated)
String scope = "public_profile,email,user_friends";
```

See [Facebook Permissions Reference](https://developers.facebook.com/docs/permissions/reference) for full list.

### Token Lifetimes

| Token Type | Lifetime | How to Get |
|------------|----------|------------|
| **Short-lived Token** | 1 hour | Initial authorization |
| **Long-lived Token** | 60 days | Exchange short-lived token |
| **Page Access Token** | 60 days / Never expires | Page permissions |

⚠️ **Important**: Facebook does NOT support traditional refresh tokens. Use long-lived tokens instead.

### Graph API Versioning

Facebook uses versioned APIs (e.g., `v18.0`). Each version is supported for ~2 years.

**Best Practice:**
```java
// Use explicit version in all URLs
String authorizeEndpoint = "https://www.facebook.com/v18.0/dialog/oauth";
String tokenEndpoint = "https://graph.facebook.com/v18.0/oauth/access_token";
String graphApiUrl = "https://graph.facebook.com/v18.0/me";
```

Check current version: https://developers.facebook.com/docs/graph-api/changelog

### Redirect URI Restrictions

⚠️ **Important**: Facebook enforces strict redirect URI rules:
- Must be registered in **Facebook Login** → **Settings** → **Valid OAuth Redirect URIs**
- Must use `https://` in production (except `http://localhost` for development)
- Cannot use IP addresses
- Must match exactly (including path and query parameters)

**Example:**
```java
// Registered in Facebook Console
redirect_uri = "https://example.com/oauth/callback"

// ✅ Match
redirect_uri = "https://example.com/oauth/callback"

// ❌ Mismatch (http instead of https)
redirect_uri = "http://example.com/oauth/callback"

// ❌ Mismatch (different path)
redirect_uri = "https://example.com/oauth/facebook/callback"
```

### App Modes

Facebook apps have different modes:

| Mode | Description | OAuth Enabled |
|------|-------------|---------------|
| **Development** | Testing only, limited users | ✅ Yes |
| **Live** | Public access | ✅ Yes |

Switch to **Live** mode in **App Settings** → **Basic** → **App Mode** when ready for production.

## Spring Boot Integration

```java
@Configuration
public class FacebookOAuthConfig {

    @Value("${oauth.facebook.app-id}")
    private String appId;

    @Value("${oauth.facebook.app-secret}")
    private String appSecret;

    @Value("${oauth.facebook.redirect-uri}")
    private String redirectUri;

    @Value("${oauth.facebook.api-version:v18.0}")
    private String apiVersion;

    @Bean
    public OAuth2FacebookConfig facebookConfig() {
        return new OAuth2FacebookConfig(
            appId,
            appSecret,
            redirectUri,
            "https://www.facebook.com/" + apiVersion + "/dialog/oauth",
            "public_profile,email",
            "https://graph.facebook.com/" + apiVersion + "/oauth/access_token",
            OAuthHttpVerb.POST
        );
    }

    @Bean
    public OAuth2GenerateAuthorizeEndpointFunction facebookAuthorizeFunction(
        OAuth2FacebookConfig config
    ) {
        return new OAuth2FacebookGenerateAuthorizeEndpointFunction(config);
    }

    @Bean
    public OAuth2AccessTokenEndpointFunction<OAuth2FacebookTokenRes> facebookTokenFunction(
        OAuth2FacebookConfig config,
        TokenStorage tokenStorage
    ) {
        TokenExtractor<OAuth2FacebookTokenRes> extractor =
            new JsonTokenExtractor<>(new TypeReference<OAuth2FacebookTokenRes>() {});
        return new OAuth2FacebookAccessTokenEndpointFunction(config, extractor, tokenStorage);
    }
}
```

**application.yml:**
```yaml
oauth:
  facebook:
    app-id: ${FACEBOOK_APP_ID}
    app-secret: ${FACEBOOK_APP_SECRET}
    redirect-uri: http://localhost:8080/oauth/callback/facebook
    api-version: v18.0
```

## Complete Example

```java
public class FacebookOAuthExample {
    public static void main(String[] args) {
        // 1. Configuration
        OAuth2FacebookConfig config = new OAuth2FacebookConfig(
            "123456789012345",
            "abc123def456...",
            "http://localhost:8080/callback",
            "https://www.facebook.com/v18.0/dialog/oauth",
            "public_profile,email",
            "https://graph.facebook.com/v18.0/oauth/access_token",
            OAuthHttpVerb.POST
        );

        // 2. Generate authorization URL
        State state = new RandomStringStateGenerator().generate("FACEBOOK");
        OAuth2FacebookGenerateAuthorizeEndpointFunction authorizeFunction =
            new OAuth2FacebookGenerateAuthorizeEndpointFunction(config);
        String url = authorizeFunction.generate(state);

        System.out.println("Visit: " + url);

        // 3. User authorizes and returns with code
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter code from callback: ");
        String code = scanner.nextLine();

        // 4. Exchange code for token
        TokenExtractor<OAuth2FacebookTokenRes> extractor =
            new JsonTokenExtractor<>(new TypeReference<OAuth2FacebookTokenRes>() {});
        TokenStorage storage = new LocalTokenStorage();

        OAuth2FacebookAccessTokenEndpointFunction tokenFunction =
            new OAuth2FacebookAccessTokenEndpointFunction(config, extractor, storage);

        OAuth2FacebookTokenRes tokenRes = tokenFunction.issue(new Verifier(code), state);

        // 5. Use access token
        System.out.println("Access Token: " + tokenRes.getAccess_token());
        System.out.println("Expires In: " + tokenRes.getExpires_in() + " seconds");

        // 6. Fetch user profile
        String profileUrl = "https://graph.facebook.com/v18.0/me?fields=id,name,email,picture";
        OAuth2ResourceFunction<String> resourceFunction =
            new DefaultOAuth2ResourceFunction(profileUrl);
        String profile = resourceFunction.fetch(tokenRes.getAccess_token());
        System.out.println("Profile: " + profile);
    }
}
```

## Comparison: Facebook vs Google vs Naver

| Feature | Facebook | Google | Naver |
|---------|----------|--------|-------|
| **Region** | 🌍 Global | 🌍 Global | 🇰🇷 Korea |
| **API Versioning** | ✅ Versioned | ❌ Stable | ❌ Stable |
| **client_secret** | ✅ Required | ✅ Required | ✅ Required |
| **Short Token TTL** | 1 hour | 1 hour | 1 hour |
| **Long Token TTL** | 60 days | Until revoked | Permanent |
| **Refresh Token** | ❌ No (use long-lived) | ✅ Yes | ✅ Yes (permanent) |
| **Token Exchange** | ✅ Yes (short → long) | ❌ No | ❌ No |
| **App Review** | ✅ Required (advanced) | ❌ No | ❌ No |
| **Use Case** | Social features | Global auth | Korean B2C |

## Troubleshooting

### Error: redirect_uri_mismatch

**Cause**: Redirect URI not registered or doesn't match exactly

**Solution**:
1. Go to Facebook App → **Facebook Login** → **Settings**
2. Add redirect URI to **Valid OAuth Redirect URIs**
3. Ensure exact match (protocol, domain, path)
4. Save changes and wait a few minutes

### Error: invalid_client_id

**Cause**: Invalid App ID or app is not in correct mode

**Solution**:
- Verify App ID from **Settings** → **Basic**
- Check app mode (Development vs Live)
- Ensure app is not deleted or disabled

### Error: Permissions Error

**Cause**: Requesting permissions not approved by Facebook

**Solution**:
- Use only `public_profile` and `email` without App Review
- Submit App Review for advanced permissions
- Test with app in Development mode first

### Token Expired (Short-Lived)

**Cause**: Access token expired after 1 hour

**Solution**:
```java
// Exchange for long-lived token immediately after receiving short-lived token
String exchangeUrl = String.format(
    "https://graph.facebook.com/v18.0/oauth/access_token?" +
    "grant_type=fb_exchange_token&" +
    "client_id=%s&" +
    "client_secret=%s&" +
    "fb_exchange_token=%s",
    appId, appSecret, shortLivedToken
);
// GET request returns long-lived token (60 days)
```

### API Version Deprecated

**Cause**: Using old API version (e.g., `v3.2`)

**Solution**:
- Update all URLs to latest version (e.g., `v18.0`)
- Check [Facebook API Changelog](https://developers.facebook.com/docs/graph-api/changelog)
- Test thoroughly after version upgrade

### Email Not Returned

**Cause**: User didn't grant email permission or email not available

**Solution**:
- Ensure `email` scope is requested
- Check if user has verified email on Facebook
- Handle cases where email is null in response

```java
// Parse response safely
JSONObject profile = new JSONObject(profileJson);
String email = profile.optString("email", null);
if (email == null) {
    // Handle missing email
}
```

## Additional Resources

- [Facebook Login Documentation](https://developers.facebook.com/docs/facebook-login/)
- [Graph API Reference](https://developers.facebook.com/docs/graph-api/)
- [Permissions Reference](https://developers.facebook.com/docs/permissions/reference)
- [App Review Process](https://developers.facebook.com/docs/app-review)
- [Access Tokens Guide](https://developers.facebook.com/docs/facebook-login/guides/access-tokens)
- [Graph API Explorer](https://developers.facebook.com/tools/explorer/) - Test API calls

## License

Apache License 2.0
