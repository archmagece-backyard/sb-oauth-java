# OAuth 2.0 Spring Boot Starter

Spring Boot Auto-Configuration for sb-oauth-java library, providing zero-configuration OAuth 2.0 integration for Korean and global providers.

## Features

- **Zero Configuration**: Auto-configures OAuth beans based on properties
- **Multi-Provider Support**: Naver, Kakao, Google, Facebook out-of-the-box
- **Flexible Storage**: Local (dev), Redis (production), or Ehcache (single-server)
- **Type-Safe Properties**: IDE autocomplete support via configuration metadata
- **Conditional Activation**: Enable/disable providers individually
- **Production-Ready**: Battle-tested patterns and error handling

## Quick Start

### 1. Add Dependency

Add the starter and desired provider connector to your `pom.xml`:

```xml
<dependencies>
    <!-- Spring Boot Starter -->
    <dependency>
        <groupId>org.scriptonbasestar.oauth</groupId>
        <artifactId>integration-spring-boot-starter</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>

    <!-- Add provider connectors you need -->
    <dependency>
        <groupId>org.scriptonbasestar.oauth</groupId>
        <artifactId>connector-naver</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>

    <dependency>
        <groupId>org.scriptonbasestar.oauth</groupId>
        <artifactId>connector-kakao</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

### 2. Configure Properties

Add OAuth configuration to `application.yml`:

```yaml
oauth2:
  providers:
    naver:
      client-id: YOUR_NAVER_CLIENT_ID
      client-secret: YOUR_NAVER_CLIENT_SECRET
      redirect-uri: http://localhost:8080/oauth/callback/naver
      scope: profile,email

    kakao:
      client-id: YOUR_KAKAO_REST_API_KEY
      client-secret: YOUR_KAKAO_CLIENT_SECRET  # Optional
      redirect-uri: http://localhost:8080/oauth/callback/kakao
      scope: profile_nickname,account_email

  storage:
    type: local  # local, redis, or ehcache
```

### 3. Use Auto-Configured Beans

Inject and use the auto-configured OAuth beans:

```java
@RestController
public class OAuthController {

    private final OAuth2NaverAuthFunction naverAuthFunction;
    private final OAuth2NaverAccesstokenFunction naverTokenFunction;

    // Constructor injection - beans auto-configured!
    public OAuthController(
            OAuth2NaverAuthFunction naverAuthFunction,
            OAuth2NaverAccesstokenFunction naverTokenFunction) {
        this.naverAuthFunction = naverAuthFunction;
        this.naverTokenFunction = naverTokenFunction;
    }

    @GetMapping("/oauth/naver/login")
    public String login(HttpSession session) {
        String state = UUID.randomUUID().toString();
        session.setAttribute("oauth_state", state);

        String authUrl = naverAuthFunction.getAuthUrl(state);
        return "redirect:" + authUrl;
    }

    @GetMapping("/oauth/callback/naver")
    public String callback(
            @RequestParam("code") String code,
            @RequestParam("state") String state,
            HttpSession session) {

        // Verify state
        String savedState = (String) session.getAttribute("oauth_state");
        if (!state.equals(savedState)) {
            throw new IllegalStateException("Invalid state parameter");
        }

        // Exchange code for token
        OAuth2NaverTokenRes token = naverTokenFunction.issue(code, state);
        session.setAttribute("access_token", token.getAccess_token());

        return "redirect:/profile";
    }
}
```

That's it! No manual bean configuration needed.

## Configuration Reference

### Provider Configuration

All providers share the same configuration structure:

```yaml
oauth2:
  providers:
    {provider-name}:
      client-id: "..."           # Required: OAuth client ID
      client-secret: "..."       # Required for Naver, Google, Facebook; Optional for Kakao
      redirect-uri: "..."        # Required: Callback URL
      scope: "..."               # Optional: Space or comma-separated scopes
      enabled: true              # Optional: Enable/disable provider (default: true)
```

### Supported Providers

#### Naver

```yaml
oauth2:
  providers:
    naver:
      client-id: YOUR_CLIENT_ID
      client-secret: YOUR_CLIENT_SECRET  # Required
      redirect-uri: http://localhost:8080/oauth/callback/naver
      scope: profile,email               # Default: profile
```

**Auto-configured beans:**
- `OAuth2NaverConfig naverConfig`
- `OAuth2NaverAuthFunction naverAuthFunction`
- `OAuth2NaverAccesstokenFunction naverTokenFunction`
- `TokenExtractor<OAuth2NaverTokenRes> naverTokenExtractor`

#### Kakao

```yaml
oauth2:
  providers:
    kakao:
      client-id: YOUR_REST_API_KEY
      client-secret: YOUR_CLIENT_SECRET  # Optional
      redirect-uri: http://localhost:8080/oauth/callback/kakao
      scope: profile_nickname,account_email
```

**Auto-configured beans:**
- `OAuth2KakaoConfig kakaoConfig`
- `OAuth2KakaoAuthFunction kakaoAuthFunction`
- `OAuth2KakaoAccessTokenFunction kakaoTokenFunction`
- `TokenExtractor<OAuth2KakaoTokenRes> kakaoTokenExtractor`

#### Google

```yaml
oauth2:
  providers:
    google:
      client-id: YOUR_CLIENT_ID
      client-secret: YOUR_CLIENT_SECRET  # Required
      redirect-uri: http://localhost:8080/oauth/callback/google
      scope: openid,profile,email        # Default: openid profile email
```

**Auto-configured beans:**
- `OAuth2GoogleConfig googleConfig`
- `OAuth2GoogleAuthFunction googleAuthFunction`
- `OAuth2GoogleAccessTokenFunction googleTokenFunction`
- `TokenExtractor<OAuth2GoogleTokenRes> googleTokenExtractor`

#### Facebook

```yaml
oauth2:
  providers:
    facebook:
      client-id: YOUR_APP_ID
      client-secret: YOUR_APP_SECRET     # Required
      redirect-uri: http://localhost:8080/oauth/callback/facebook
      scope: email,public_profile        # Default: email,public_profile
```

**Auto-configured beans:**
- `OAuth2FacebookConfig facebookConfig`
- `OAuth2FacebookAuthFunction facebookAuthFunction`
- `OAuth2FacebookAccessTokenFunction facebookTokenFunction`
- `TokenExtractor<OAuth2FacebookTokenRes> facebookTokenExtractor`

### Storage Configuration

#### Local Storage (Development)

In-memory storage. **WARNING**: Tokens are lost on server restart.

```yaml
oauth2:
  storage:
    type: local
```

#### Redis Storage (Production)

Recommended for multi-server deployments.

```yaml
oauth2:
  storage:
    type: redis
    redis:
      host: localhost
      port: 6379
      password: your-password
      database: 0
      key-prefix: "oauth2:tokens:"

# Also configure Spring Boot Redis
spring:
  data:
    redis:
      host: ${oauth2.storage.redis.host}
      port: ${oauth2.storage.redis.port}
      password: ${oauth2.storage.redis.password}
      database: ${oauth2.storage.redis.database}
```

**Required dependency:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

#### Ehcache Storage (Single-Server Production)

Suitable for single-server production deployments.

```yaml
oauth2:
  storage:
    type: ehcache
    ehcache:
      cache-name: oauth2-tokens
      max-entries: 1000
      ttl-seconds: 7200
```

**Required dependency:**
```xml
<dependency>
    <groupId>javax.cache</groupId>
    <artifactId>cache-api</artifactId>
</dependency>
<dependency>
    <groupId>org.ehcache</groupId>
    <artifactId>ehcache</artifactId>
</dependency>
```

## Advanced Usage

### Multi-Provider Support

Enable and use multiple providers simultaneously:

```yaml
oauth2:
  providers:
    naver:
      client-id: ${NAVER_CLIENT_ID}
      client-secret: ${NAVER_CLIENT_SECRET}
      redirect-uri: ${BASE_URL}/oauth/callback/naver
      scope: profile,email

    kakao:
      client-id: ${KAKAO_CLIENT_ID}
      redirect-uri: ${BASE_URL}/oauth/callback/kakao
      scope: profile_nickname,account_email

    google:
      client-id: ${GOOGLE_CLIENT_ID}
      client-secret: ${GOOGLE_CLIENT_SECRET}
      redirect-uri: ${BASE_URL}/oauth/callback/google
```

```java
@Service
public class MultiProviderOAuthService {

    private final OAuth2NaverAuthFunction naverAuth;
    private final OAuth2KakaoAuthFunction kakaoAuth;
    private final OAuth2GoogleAuthFunction googleAuth;

    // All injected automatically!
    public MultiProviderOAuthService(
            OAuth2NaverAuthFunction naverAuth,
            OAuth2KakaoAuthFunction kakaoAuth,
            OAuth2GoogleAuthFunction googleAuth) {
        this.naverAuth = naverAuth;
        this.kakaoAuth = kakaoAuth;
        this.googleAuth = googleAuth;
    }

    public String getAuthUrl(String provider, String state) {
        return switch (provider.toLowerCase()) {
            case "naver" -> naverAuth.getAuthUrl(state);
            case "kakao" -> kakaoAuth.getAuthUrl(state);
            case "google" -> googleAuth.getAuthUrl(state);
            default -> throw new IllegalArgumentException("Unknown provider: " + provider);
        };
    }
}
```

### Conditional Bean Creation

Providers are only configured when their dependencies are present and enabled:

```yaml
oauth2:
  providers:
    naver:
      enabled: true   # Provider activated
      # ...

    kakao:
      enabled: false  # Provider disabled - beans not created
      # ...
```

### Custom Bean Overrides

Override auto-configured beans if needed:

```java
@Configuration
public class CustomOAuthConfig {

    @Bean
    public TokenStorage customTokenStorage() {
        // Your custom storage implementation
        return new MyCustomTokenStorage();
    }

    @Bean
    public StateGenerator customStateGenerator() {
        // Your custom state generator
        return new MyCustomStateGenerator();
    }
}
```

## Production Deployment

### Environment Variables

Use environment variables for sensitive data:

```yaml
oauth2:
  providers:
    naver:
      client-id: ${NAVER_CLIENT_ID}
      client-secret: ${NAVER_CLIENT_SECRET}
      redirect-uri: ${APP_BASE_URL}/oauth/callback/naver

  storage:
    type: redis
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD}
```

### Redis with Docker Compose

```yaml
version: '3.8'
services:
  app:
    build: .
    environment:
      - REDIS_HOST=redis
      - NAVER_CLIENT_ID=${NAVER_CLIENT_ID}
      - NAVER_CLIENT_SECRET=${NAVER_CLIENT_SECRET}
    depends_on:
      - redis

  redis:
    image: redis:7-alpine
    command: redis-server --requirepass ${REDIS_PASSWORD}
    volumes:
      - redis-data:/data

volumes:
  redis-data:
```

## IDE Support

The starter includes configuration metadata for IDE autocomplete:

1. IntelliJ IDEA: Autocomplete works automatically
2. Eclipse: Install Spring Tools Suite (STS)
3. VS Code: Install Spring Boot Extension Pack

Just type `oauth2.` in `application.yml` to see all available properties!

## Troubleshooting

### Beans Not Auto-Configured

**Problem**: OAuth beans are not created

**Solutions**:
1. Ensure connector dependency is added (e.g., `connector-naver`)
2. Check `client-id` is configured in properties
3. Verify `enabled: true` (or not set, as true is default)
4. Check Spring Boot version (requires 3.0+)

### Multiple Bean Candidates

**Problem**: `No qualifying bean of type 'TokenStorage'`

**Solution**: Specify storage type explicitly:
```yaml
oauth2:
  storage:
    type: local  # or redis, ehcache
```

### Redis Connection Failed

**Problem**: Cannot connect to Redis

**Solutions**:
1. Verify Redis is running: `redis-cli ping`
2. Check connection properties (host, port, password)
3. Ensure `spring-boot-starter-data-redis` dependency is added
4. Check firewall/network settings

### Provider-Specific Issues

See the main [FAQ.md](../../docs/FAQ.md) for provider-specific troubleshooting.

## Migration from Manual Configuration

### Before (Manual Configuration)

```java
@Configuration
public class OAuthConfig {

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;

    @Bean
    public OAuth2NaverConfig naverConfig() {
        return OAuth2NaverConfig.builder()
            .clientId(clientId)
            .clientSecret(clientSecret)
            .redirectUri("http://localhost:8080/callback")
            .scope("profile")
            .build();
    }

    @Bean
    public OAuth2NaverAuthFunction naverAuthFunction(OAuth2NaverConfig config) {
        return new OAuth2NaverAuthFunction(config);
    }

    @Bean
    public OAuth2NaverAccesstokenFunction naverTokenFunction(
            OAuth2NaverConfig config,
            TokenExtractor<OAuth2NaverTokenRes> extractor,
            TokenStorage storage) {
        return new OAuth2NaverAccesstokenFunction(config, extractor, storage);
    }

    @Bean
    public TokenExtractor<OAuth2NaverTokenRes> naverTokenExtractor() {
        return new JsonTokenExtractor<>(new TypeReference<>() {});
    }

    @Bean
    public TokenStorage tokenStorage() {
        return new LocalTokenStorage();
    }
}
```

### After (Auto-Configuration)

```yaml
# application.yml
oauth2:
  providers:
    naver:
      client-id: YOUR_CLIENT_ID
      client-secret: YOUR_CLIENT_SECRET
      redirect-uri: http://localhost:8080/callback
      scope: profile
```

That's it! All beans are auto-configured. Just inject and use:

```java
@RestController
public class OAuthController {
    private final OAuth2NaverAuthFunction authFunction;

    // Constructor injection - no manual configuration needed!
    public OAuthController(OAuth2NaverAuthFunction authFunction) {
        this.authFunction = authFunction;
    }
}
```

## Examples

See complete working examples:

- [spring-boot-basic](../../examples/spring-boot-basic) - Single provider (Naver) integration
- [multi-provider](../../examples/multi-provider) - Multiple providers with unified user model
- [redis-storage](../../examples/redis-storage) - Production setup with Redis and token refresh

## Documentation

- [User Guide](../../docs/USER_GUIDE.md) - Step-by-step tutorials
- [Architecture](../../docs/ARCHITECTURE.md) - Design and internal architecture
- [FAQ](../../docs/FAQ.md) - Common questions and troubleshooting
- [Migration Guide](../../docs/MIGRATION.md) - Migrating from other libraries

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](../../LICENSE) file for details.
