# Security Policy

Security guidelines and best practices for sb-oauth-java OAuth 2.0 implementation.

## Table of Contents

1. [Security Overview](#security-overview)
2. [Security Configuration](#security-configuration)
3. [Vulnerability Prevention](#vulnerability-prevention)
4. [Sensitive Data Handling](#sensitive-data-handling)
5. [Security Checklist](#security-checklist)
6. [Security Reporting](#security-reporting)

---

## Security Overview

### Security Features

sb-oauth-java implements comprehensive security measures:

✅ **CSRF Protection**
- Cryptographically secure state parameter generation
- State validation with expiration
- SecureRandom-based entropy

✅ **XSS Prevention**
- Input validation and sanitization
- Output encoding
- Content Security Policy headers

✅ **Open Redirect Prevention**
- Redirect URI whitelist validation
- Strict URI scheme and host checking
- Localhost handling for development

✅ **Sensitive Data Protection**
- Automatic masking in logs
- Secure credential storage
- Environment-based configuration

✅ **HTTPS Enforcement**
- TLS 1.2+ required in production
- HTTPS redirect URI validation
- Secure cookie attributes

---

## Security Configuration

### 1. State Generator Security

**Production Configuration:**

```java
@Configuration
public class SecurityConfig {

    @Bean
    public StateGenerator stateGenerator() {
        // Use production-optimized secure generator
        // - 256-bit random (32 bytes)
        // - Timestamp included for expiration
        // - Thread-safe SecureRandom
        return SecureStateGenerator.forProduction();
    }
}
```

**Custom Configuration:**

```java
@Bean
public StateGenerator customStateGenerator() {
    return SecureStateGenerator.builder()
        .randomBytes(32)              // 256-bit entropy
        .includeTimestamp(true)       // Enable expiration
        .separator("-")               // Custom separator
        .secureRandom(new SecureRandom())  // Custom SecureRandom instance
        .build();
}
```

**Security Properties:**

| Property | Production Value | Description |
|----------|-----------------|-------------|
| `randomBytes` | 32 | 256-bit entropy (OWASP recommended) |
| `includeTimestamp` | `true` | Enable state expiration |
| `separator` | `-` | Delimiter between random and timestamp |
| `secureRandom` | `SecureRandom` | CSPRNG for unpredictable values |

### 2. Redirect URI Validation

**Strict Validation Configuration:**

```java
@Bean
public RedirectUriValidator redirectUriValidator(
    @Value("${oauth.providers.naver.redirect-uri}") String naverRedirectUri,
    @Value("${oauth.providers.kakao.redirect-uri}") String kakaoRedirectUri,
    @Value("${oauth.providers.google.redirect-uri}") String googleRedirectUri,
    @Value("${oauth.providers.facebook.redirect-uri}") String facebookRedirectUri,
    @Value("${oauth.security.allow-localhost:false}") boolean allowLocalhost,
    @Value("${oauth.security.require-https:true}") boolean requireHttps
) {
    Set<String> allowedUris = Set.of(
        naverRedirectUri,
        kakaoRedirectUri,
        googleRedirectUri,
        facebookRedirectUri
    );

    return new RedirectUriValidator(
        allowedUris,
        allowLocalhost,    // false in production
        requireHttps       // true in production
    );
}
```

**Validation Rules:**

```yaml
oauth:
  security:
    # HTTPS enforcement
    require-https: true         # ✅ REQUIRED for production
    allow-localhost: false      # ✅ MUST be false in production

    # Redirect URI whitelist
    allowed-redirect-uris:
      - https://yourdomain.com/oauth/callback/naver
      - https://yourdomain.com/oauth/callback/kakao
      - https://yourdomain.com/oauth/callback/google
      - https://yourdomain.com/oauth/callback/facebook
```

**Security Checks:**

- ✅ URI must be in whitelist
- ✅ HTTPS scheme required (unless localhost)
- ✅ Scheme and host validation
- ✅ No open redirects allowed

### 3. Sensitive Data Masking

**Automatic Masking in Logs:**

```java
import org.scriptonbasestar.oauth.client.security.SensitiveDataMaskingUtil;

// Client Secret Masking
String clientSecret = "abcdef123456789";
log.info("Client secret: {}",
    SensitiveDataMaskingUtil.maskClientSecret(clientSecret));
// Output: Client secret: ab****89

// Access Token Masking
String accessToken = "ya29.a0AfH6SMBx...";
log.info("Access token: {}",
    SensitiveDataMaskingUtil.maskAccessToken(accessToken));
// Output: Access token: ya29****...

// Refresh Token Masking
String refreshToken = "1//0eXaMpLeToKeN...";
log.info("Refresh token: {}",
    SensitiveDataMaskingUtil.maskRefreshToken(refreshToken));
// Output: Refresh token: 1//0****...

// Authorization Code Masking
String code = "4/0AX4XfWh...";
log.info("Authorization code: {}",
    SensitiveDataMaskingUtil.maskAuthorizationCode(code));
// Output: Authorization code: 4/0A****...
```

**Custom Masking:**

```java
// Custom masking with prefix/suffix length
String value = "sensitive-data-here";
String masked = SensitiveDataMaskingUtil.mask(value, 4, 4);
// Output: sens****here
```

### 4. Credential Management

**Environment Variables (Recommended):**

```bash
# NEVER commit these to version control
export NAVER_CLIENT_ID="your_client_id"
export NAVER_CLIENT_SECRET="your_client_secret"
export KAKAO_CLIENT_ID="your_client_id"
export KAKAO_CLIENT_SECRET="your_client_secret"
```

**Spring Configuration:**

```yaml
oauth:
  providers:
    naver:
      client-id: ${NAVER_CLIENT_ID}
      client-secret: ${NAVER_CLIENT_SECRET}
```

**AWS Secrets Manager (Production):**

```java
@Configuration
public class SecretsManagerConfig {

    @Bean
    public OAuthProviderProperties oauthProviderProperties(
        SecretsManagerClient secretsClient
    ) {
        String secretArn = "arn:aws:secretsmanager:region:account:secret:oauth-credentials";
        GetSecretValueResponse response = secretsClient.getSecretValue(
            GetSecretValueRequest.builder()
                .secretId(secretArn)
                .build()
        );

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(
            response.secretString(),
            OAuthProviderProperties.class
        );
    }
}
```

**HashiCorp Vault (Enterprise):**

```yaml
spring:
  cloud:
    vault:
      uri: https://vault.yourdomain.com:8200
      token: ${VAULT_TOKEN}
      kv:
        enabled: true
        backend: secret
        default-context: oauth-credentials
```

---

## Vulnerability Prevention

### 1. CSRF (Cross-Site Request Forgery) Prevention

**How it Works:**

```
Client                    OAuth Server              Provider
  |                            |                        |
  |--- GET /oauth/authorize -->|                        |
  |                            |--- generate state ---->|
  |                            |    (SecureRandom)      |
  |<-- redirect to provider ---|                        |
  |    (with state param)      |                        |
  |                            |                        |
  |-------------------------- authorize --------------->|
  |                            |                        |
  |<------------- redirect with code & state -----------|
  |                            |                        |
  |--- GET /oauth/callback --->|                        |
  |    (code + state)          |                        |
  |                            |--- validate state ---->|
  |                            |    (check expiration)  |
  |                            |                        |
  |<-- token response ---------|                        |
```

**State Security:**

```java
// State structure: {random}-{timestamp}
// Example: "xJ9kL2mN4pQ8rT6vW5yZ3bC1dE7fG0hI-1700000000000"

State state = stateGenerator.generate();
// - 256-bit random: cryptographically secure
// - Timestamp: enables expiration validation
// - URL-safe: Base64 URL encoding
```

**Protection:**
- Unpredictable state values (256-bit entropy)
- State expiration (default: 5 minutes)
- One-time use (invalidated after validation)
- Stored server-side (Redis/Ehcache)

### 2. XSS (Cross-Site Scripting) Prevention

**Input Validation:**

```java
@RestController
public class OAuthController {

    // Validate redirect_uri parameter
    public String authorize(
        @RequestParam String provider,
        @RequestParam(required = false) String redirect_uri
    ) {
        // 1. Validate provider
        if (!ALLOWED_PROVIDERS.contains(provider)) {
            throw new InvalidProviderException(provider);
        }

        // 2. Validate redirect_uri
        if (redirect_uri != null) {
            if (!redirectUriValidator.isValid(redirect_uri)) {
                throw new InvalidRedirectUriException(redirect_uri);
            }
        }

        // Safe to proceed
    }
}
```

**Output Encoding:**

```java
// Use proper encoding for HTML output
String userInput = request.getParameter("error_description");
String encoded = HtmlUtils.htmlEscape(userInput);
model.addAttribute("error", encoded);
```

**Content Security Policy (CSP) Headers:**

```java
@Configuration
public class SecurityHeadersConfig {

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(new HandlerInterceptor() {
                    @Override
                    public void postHandle(
                        HttpServletRequest request,
                        HttpServletResponse response,
                        Object handler,
                        ModelAndView modelAndView
                    ) {
                        response.setHeader(
                            "Content-Security-Policy",
                            "default-src 'self'; " +
                            "script-src 'self' 'unsafe-inline'; " +
                            "style-src 'self' 'unsafe-inline'"
                        );
                        response.setHeader(
                            "X-Content-Type-Options",
                            "nosniff"
                        );
                        response.setHeader(
                            "X-Frame-Options",
                            "DENY"
                        );
                        response.setHeader(
                            "X-XSS-Protection",
                            "1; mode=block"
                        );
                    }
                });
            }
        };
    }
}
```

### 3. Open Redirect Prevention

**Redirect URI Validation:**

```java
public class RedirectUriValidator {

    public boolean isValid(String redirectUri) {
        if (redirectUri == null || redirectUri.trim().isEmpty()) {
            return false;
        }

        try {
            URI uri = new URI(redirectUri);

            // 1. Require scheme and host
            if (uri.getScheme() == null || uri.getHost() == null) {
                return false;
            }

            boolean isLocalhost = isLocalhost(uri.getHost());

            // 2. HTTPS enforcement (except localhost)
            if (requireHttps && !isLocalhost &&
                !"https".equalsIgnoreCase(uri.getScheme())) {
                return false;
            }

            // 3. Localhost exemption (development only)
            if (allowLocalhost && isLocalhost) {
                return true;
            }

            // 4. Whitelist validation
            return allowedRedirectUris.contains(redirectUri);

        } catch (URISyntaxException e) {
            return false;
        }
    }
}
```

**Example Attack Prevention:**

```java
// ❌ Attack attempt
String maliciousUri = "https://attacker.com/steal-tokens";
boolean valid = validator.isValid(maliciousUri);
// Returns: false (not in whitelist)

// ❌ Protocol downgrade attack
String httpUri = "http://yourdomain.com/callback";
boolean valid = validator.isValid(httpUri);
// Returns: false (HTTPS required)

// ✅ Valid redirect URI
String validUri = "https://yourdomain.com/oauth/callback/naver";
boolean valid = validator.isValid(validUri);
// Returns: true (in whitelist, HTTPS, valid host)
```

### 4. SQL Injection Prevention

**Not Applicable:**
- sb-oauth-java does not use SQL databases
- All storage is key-value based (Redis/Ehcache)
- No raw SQL queries

**If Using Database:**

```java
// ✅ Use parameterized queries
String sql = "SELECT * FROM users WHERE id = ?";
jdbcTemplate.queryForObject(sql, User.class, userId);

// ❌ Never concatenate user input
String sql = "SELECT * FROM users WHERE id = " + userId;  // VULNERABLE
```

### 5. Injection Attacks Prevention

**Command Injection:**

```java
// ✅ Safe: No shell execution
// sb-oauth-java uses HTTP clients only

// ❌ If you need to execute commands (NOT in this library)
// NEVER do this:
Runtime.getRuntime().exec("curl " + userInput);  // VULNERABLE
```

**LDAP Injection:**

Not applicable (library does not use LDAP).

**XML Injection:**

```java
// ✅ Safe: JSON-only API
// OAuth 2.0 uses JSON, not XML
```

### 6. Timing Attacks Prevention

**Constant-Time Comparison:**

```java
import java.security.MessageDigest;

public boolean validateState(String expected, String actual) {
    if (expected == null || actual == null) {
        return false;
    }

    // Use MessageDigest.isEqual for constant-time comparison
    byte[] expectedBytes = expected.getBytes(StandardCharsets.UTF_8);
    byte[] actualBytes = actual.getBytes(StandardCharsets.UTF_8);

    return MessageDigest.isEqual(expectedBytes, actualBytes);
}
```

---

## Sensitive Data Handling

### 1. Data Classification

| Data Type | Classification | Storage | Logging | Retention |
|-----------|---------------|---------|---------|-----------|
| Client ID | Public | Config | ✅ Yes | Permanent |
| Client Secret | Secret | Vault | ❌ Masked | Permanent |
| Authorization Code | Secret | Redis | ❌ Masked | 5 minutes |
| Access Token | Secret | Redis | ❌ Masked | Provider TTL |
| Refresh Token | Secret | Redis | ❌ Masked | Provider TTL |
| State | Secret | Redis | ✅ Yes | 5 minutes |
| User ID | PII | Database | ✅ Yes | Per policy |
| Email | PII | Database | ❌ No | Per policy |

### 2. Logging Guidelines

**✅ Safe to Log:**

```java
// Provider name
log.info("OAuth authorization for provider: {}", provider);

// User ID (non-PII identifier)
log.info("User authenticated: userId={}", userId);

// State value (short-lived, random)
log.debug("Generated state: {}", state.getValue());

// Masked secrets
log.info("Token received: {}",
    SensitiveDataMaskingUtil.maskAccessToken(token));
```

**❌ NEVER Log:**

```java
// ❌ Full client secret
log.info("Client secret: {}", clientSecret);

// ❌ Full access token
log.info("Access token: {}", accessToken);

// ❌ Full refresh token
log.info("Refresh token: {}", refreshToken);

// ❌ Authorization code
log.info("Auth code: {}", code);

// ❌ User email or PII
log.info("User email: {}", email);

// ❌ User password (if stored)
log.info("Password: {}", password);
```

### 3. Storage Security

**Redis Security:**

```yaml
oauth:
  storage:
    redis:
      # Enable TLS
      ssl: true

      # Authentication
      password: ${REDIS_PASSWORD}

      # Network isolation
      host: redis.internal.network  # Not public

      # Key expiration
      default-ttl: 300  # 5 minutes for state
```

**Encryption at Rest:**

```bash
# Redis with encryption
redis-server --requirepass ${REDIS_PASSWORD} \
             --tls-port 6380 \
             --tls-cert-file /path/to/cert.pem \
             --tls-key-file /path/to/key.pem \
             --tls-ca-cert-file /path/to/ca.pem
```

### 4. Data Retention

**Automatic Expiration:**

```java
// State: 5 minutes
stateStorage.save(state, Duration.ofMinutes(5));

// Authorization code: 1 minute (provider-specific)
codeStorage.save(code, Duration.ofMinutes(1));

// Access token: provider TTL (e.g., 1 hour)
tokenStorage.save(accessToken, Duration.ofHours(1));
```

**Manual Cleanup:**

```java
@Scheduled(cron = "0 0 2 * * *")  // Daily at 2 AM
public void cleanupExpiredData() {
    int deleted = stateStorage.deleteExpired();
    log.info("Cleaned up {} expired states", deleted);
}
```

---

## Security Checklist

### Pre-Production

**Configuration:**
- [ ] `require-https: true` in production
- [ ] `allow-localhost: false` in production
- [ ] All redirect URIs use HTTPS
- [ ] Client secrets stored in vault/secrets manager
- [ ] Environment variables configured
- [ ] Redis password set and strong (16+ chars)
- [ ] Redis TLS enabled
- [ ] State expiration configured (5 minutes recommended)

**Code:**
- [ ] All sensitive data masked in logs
- [ ] Input validation implemented
- [ ] Redirect URI whitelist configured
- [ ] CSRF protection enabled
- [ ] XSS prevention headers added
- [ ] Error messages don't leak sensitive info

**Infrastructure:**
- [ ] TLS 1.2+ configured
- [ ] Strong cipher suites enabled
- [ ] Firewall rules configured
- [ ] Network segmentation implemented
- [ ] Load balancer health checks configured
- [ ] DDoS protection enabled

**Monitoring:**
- [ ] Security alerts configured
- [ ] Failed authentication tracking
- [ ] Anomaly detection enabled
- [ ] Log aggregation configured
- [ ] SIEM integration (if required)

### Post-Deployment

**Verification:**
- [ ] HTTPS working correctly
- [ ] Redirect URI validation working
- [ ] State validation working
- [ ] Token expiration working
- [ ] Error handling working
- [ ] Logs properly masked
- [ ] Security headers present

**Testing:**
- [ ] Penetration testing completed
- [ ] Security scan passed (OWASP ZAP, Burp Suite)
- [ ] Dependency vulnerability scan passed
- [ ] SSL/TLS test passed (ssllabs.com)

### Ongoing

**Monthly:**
- [ ] Review access logs
- [ ] Check for security updates
- [ ] Rotate credentials (if policy requires)
- [ ] Review security alerts
- [ ] Update dependencies

**Quarterly:**
- [ ] Security audit
- [ ] Penetration testing
- [ ] Review and update security policies
- [ ] Team security training

---

## Security Reporting

### Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| 1.0.x   | ✅ Yes             |
| < 1.0   | ❌ No              |

### Reporting a Vulnerability

**DO NOT** create a public GitHub issue for security vulnerabilities.

**Instead:**

1. **Email:** security@scriptonbasestar.org
2. **Subject:** "[SECURITY] sb-oauth-java vulnerability"
3. **Include:**
   - Description of the vulnerability
   - Steps to reproduce
   - Potential impact
   - Suggested fix (if any)

**Response Time:**

- **Initial Response:** Within 48 hours
- **Status Update:** Within 7 days
- **Fix Timeline:** Depends on severity
  - Critical: 1-3 days
  - High: 7-14 days
  - Medium: 30 days
  - Low: Next release

### Security Severity Levels

**Critical:**
- Remote code execution
- Authentication bypass
- Sensitive data exposure

**High:**
- CSRF bypass
- XSS vulnerabilities
- Open redirect vulnerabilities

**Medium:**
- Information disclosure
- Missing security headers
- Weak cryptography

**Low:**
- Documentation issues
- Non-exploitable bugs

### Security Updates

Security updates will be released as:
- Patch versions for critical/high severity
- Minor versions for medium/low severity

**Notification Channels:**
- GitHub Security Advisories
- Release notes
- Email (if registered)

### Hall of Fame

We recognize security researchers who responsibly disclose vulnerabilities:

*No vulnerabilities reported yet.*

---

## Security Best Practices

### For Developers

1. **Never Commit Secrets**
   ```bash
   # Use .gitignore
   echo ".env" >> .gitignore
   echo "application-local.yml" >> .gitignore
   ```

2. **Use Strong Credentials**
   ```bash
   # Generate strong client secret
   openssl rand -base64 32
   ```

3. **Keep Dependencies Updated**
   ```bash
   mvn versions:display-dependency-updates
   mvn org.owasp:dependency-check-maven:check
   ```

4. **Review Code for Security**
   - Use static analysis tools (SpotBugs, PMD)
   - Follow OWASP guidelines
   - Peer review security-sensitive code

### For Operators

1. **Enable Security Monitoring**
   - Log all authentication attempts
   - Alert on anomalies
   - Monitor for brute force attacks

2. **Regular Security Audits**
   - Quarterly penetration testing
   - Monthly vulnerability scans
   - Annual third-party audit

3. **Incident Response Plan**
   - Define response procedures
   - Assign responsibilities
   - Practice incident scenarios

### For Users

1. **Protect Client Credentials**
   - Never share client secrets
   - Rotate credentials regularly
   - Use separate credentials per environment

2. **Monitor OAuth Activity**
   - Review authorization logs
   - Check for unauthorized access
   - Revoke unused tokens

3. **Report Suspicious Activity**
   - Contact security team immediately
   - Preserve evidence
   - Don't attempt to investigate yourself

---

## Compliance

### OWASP Top 10 Coverage

| Vulnerability | Status | Protection |
|--------------|--------|------------|
| A01: Broken Access Control | ✅ Protected | Redirect URI validation, state validation |
| A02: Cryptographic Failures | ✅ Protected | SecureRandom, TLS enforcement |
| A03: Injection | ✅ Protected | Input validation, parameterized queries |
| A04: Insecure Design | ✅ Protected | Security by design, defense in depth |
| A05: Security Misconfiguration | ⚠️ User Responsibility | Configuration guides provided |
| A06: Vulnerable Components | ⚠️ User Responsibility | Regular updates required |
| A07: Authentication Failures | ✅ Protected | State-based CSRF protection |
| A08: Software/Data Integrity | ✅ Protected | Signed artifacts, checksum verification |
| A09: Logging Failures | ✅ Protected | Sensitive data masking |
| A10: SSRF | ✅ Protected | Redirect URI whitelist |

### OAuth 2.0 Security BCP

Compliant with [RFC 6749](https://tools.ietf.org/html/rfc6749) and [OAuth 2.0 Security Best Current Practice](https://tools.ietf.org/html/draft-ietf-oauth-security-topics):

✅ **BCP-1:** State parameter for CSRF protection
✅ **BCP-2:** HTTPS for redirect URIs
✅ **BCP-3:** Redirect URI validation
✅ **BCP-4:** Short-lived authorization codes
✅ **BCP-5:** Token expiration
✅ **BCP-6:** Secure token storage
✅ **BCP-7:** Audience validation

---

## Additional Resources

- [OWASP OAuth 2.0 Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/OAuth2_Cheat_Sheet.html)
- [OAuth 2.0 Threat Model](https://tools.ietf.org/html/rfc6819)
- [OAuth 2.0 Security BCP](https://tools.ietf.org/html/draft-ietf-oauth-security-topics)
- [PRODUCTION_GUIDE.md](PRODUCTION_GUIDE.md)
- [DEPLOYMENT.md](DEPLOYMENT.md)

---

**Last Updated:** 2024-11-19
**Version:** 1.0.0
