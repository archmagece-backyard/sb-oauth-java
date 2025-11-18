# oauth-client

Core OAuth 2.0 client library providing standardized interfaces and implementations for OAuth authentication flows.

## Overview

This is the foundational module of sb-oauth-java that defines:
- OAuth 2.0 core interfaces
- HTTP communication layer
- Token and state management
- Exception handling
- Storage interfaces

All OAuth provider connectors depend on this module.

## Key Components

### Interfaces

**OAuth2GenerateAuthorizeEndpointFunction**
```java
public interface OAuth2GenerateAuthorizeEndpointFunction {
    String generate(State state);
}
```
Generates OAuth authorization URLs with required parameters.

**OAuth2AccessTokenEndpointFunction<TOKEN_RES>**
```java
public interface OAuth2AccessTokenEndpointFunction<TOKEN_RES extends TokenPack> {
    TOKEN_RES issue(Verifier verifier, State state);
    TOKEN_RES refresh(Token refreshToken);
    TOKEN_RES revoke(Token accessToken);
}
```
Manages OAuth token lifecycle (issue, refresh, revoke).

**OAuth2ResourceFunction<RESOURCE>**
```java
public interface OAuth2ResourceFunction<RESOURCE> {
    RESOURCE run(Token accessToken);
}
```
Fetches user information from OAuth resource servers.

### Models

**Token** - Represents OAuth tokens (access_token, refresh_token)
**State** - CSRF protection parameter
**Verifier** - Authorization code from OAuth callback
**TokenPack** - Interface for token response models

### Storage Interfaces

**TokenStorage**
```java
public interface TokenStorage {
    void save(String key, Token token);
    Token get(String key);
    void remove(String key);
}
```

**StateStorage**
```java
public interface StateStorage {
    void save(String key, State state);
    State get(String key);
    boolean isValid(String stateValue);
    void remove(String key);
}
```

Implementations:
- `LocalTokenStorage` - In-memory storage (default)
- Redis and Ehcache implementations in `oauth-storage` modules

### Exception Hierarchy

```
OAuthException
├── OAuthNetworkException
├── OAuthNetworkRemoteException
├── OAuthParsingException
├── OAuthParamException
├── OAuthInitException
└── OAuthAuthException
```

## Usage Example

### Basic OAuth Flow

```java
// 1. Configure OAuth provider
OAuth2NaverConfig config = OAuth2NaverConfig.builder()
    .clientId("YOUR_CLIENT_ID")
    .clientSecret("YOUR_CLIENT_SECRET")
    .redirectUri("http://localhost:8080/callback")
    .scope("profile,email")
    .build();

// 2. Generate authorization URL
OAuth2GenerateAuthorizeEndpointFunction authFunction =
    new OAuth2NaverGenerateAuthorizeEndpointFunction(config);
State state = new RandomStringStateGenerator().generate("NAVER");
String authUrl = authFunction.generate(state);

// Redirect user to authUrl

// 3. Handle callback and issue token
Verifier code = new Verifier(request.getParameter("code"));
TokenExtractor<OAuth2NaverTokenRes> tokenExtractor =
    new JsonTokenExtractor<>(OAuth2NaverTokenRes.class);
TokenStorage tokenStorage = new LocalTokenStorage();

OAuth2AccessTokenEndpointFunction<OAuth2NaverTokenRes> tokenFunction =
    new OAuth2NaverAccesstokenFunction(config, tokenExtractor, tokenStorage);
OAuth2NaverTokenRes token = tokenFunction.issue(code, state);

// 4. Fetch user information
OAuth2ResourceFunction<String> resourceFunction =
    new DefaultOAuth2ResourceFunction(config.getResourceProfileUri());
String userProfile = resourceFunction.run(token.getAccessToken());
```

### Token Refresh

```java
Token refreshToken = tokenStorage.get("user123:refresh_token");
OAuth2NaverTokenRes newToken = tokenFunction.refresh(refreshToken);
```

### Token Revocation

```java
Token accessToken = tokenStorage.get("user123:access_token");
tokenFunction.revoke(accessToken);
```

## HTTP Communication

Uses Apache HttpClient 5.x for HTTP/2 support and improved performance.

**HttpRequest** class provides:
- `get(url, headers)` - HTTP GET requests
- `post(url, params, headers)` - HTTP POST requests with form data
- Automatic error handling and response parsing

## Token Extractors

**JsonTokenExtractor** - Extracts tokens from JSON responses
**ParamStyleTokenExtractor** - Extracts tokens from URL-encoded responses

Example:
```java
TokenExtractor<OAuth2NaverTokenRes> extractor =
    new JsonTokenExtractor<>(OAuth2NaverTokenRes.class);
```

## State Generators

**RandomStringStateGenerator** - Generates random string states
**JsonStateGenerator** - Generates JSON-formatted states

Example:
```java
State state = new RandomStringStateGenerator().generate("PROVIDER_NAME");
stateStorage.save(state.getValue(), state);
```

## Security Features

### CSRF Protection
```java
// Generate state
State state = stateGenerator.generate("NAVER");
stateStorage.save(state.getValue(), state);

// Validate state in callback
String receivedState = request.getParameter("state");
if (!stateStorage.isValid(receivedState)) {
    throw new OAuthAuthException("Invalid state - possible CSRF attack");
}
stateStorage.remove(receivedState);
```

### Secure Token Storage
- Never store tokens in cookies or local storage
- Use server-side session or database
- Implement token encryption for sensitive data

### HTTPS Enforcement
Always use HTTPS in production for redirect URIs and API endpoints.

## Configuration

**OAuthBaseConfig** - Base configuration class

Common properties:
- `clientId` - OAuth application client ID
- `clientSecret` - OAuth application secret
- `redirectUri` - Callback URL after authorization

Provider-specific configs extend this base class.

## Dependencies

```xml
<dependency>
    <groupId>org.scriptonbasestar.oauth</groupId>
    <artifactId>oauth-client</artifactId>
    <version>sb-oauth-20181219-3-DEV</version>
</dependency>
```

Required dependencies (managed by parent POM):
- Apache HttpClient 5.4.1
- Gson 2.11.0
- Jackson 2.18.2
- SLF4J 2.0.16
- Commons Codec 1.17.1

## Testing

Run tests:
```bash
mvn test -pl oauth-client
```

## Contributing

See [CONTRIBUTING.md](../CONTRIBUTING.md) for development guidelines.

## License

Apache License 2.0
