/**
 * OAuth 2.0 security utilities for production-ready applications.
 *
 * <p>This package provides essential security components for implementing OAuth 2.0
 * authentication securely. All utilities follow industry best practices and OWASP
 * recommendations for OAuth security.</p>
 *
 * <h2>Core Components</h2>
 *
 * <h3>1. SecureStateGenerator</h3>
 * <p>Generates cryptographically secure state values for CSRF protection.</p>
 *
 * <p><b>Features:</b></p>
 * <ul>
 *   <li>Uses {@link java.security.SecureRandom} for unpredictable values</li>
 *   <li>256-bit (32 bytes) random data by default</li>
 *   <li>Base64 URL-safe encoding</li>
 *   <li>Optional timestamp for expiration validation</li>
 * </ul>
 *
 * <p><b>Example Usage:</b></p>
 * <pre>{@code
 * // Production configuration
 * StateGenerator generator = SecureStateGenerator.forProduction();
 * State state = generator.generate();
 *
 * // Validate expiration
 * boolean expired = generator.isExpired(state, Duration.ofMinutes(10));
 * if (expired) {
 *     throw new StateExpiredException("State has expired");
 * }
 * }</pre>
 *
 * <h3>2. RedirectUriValidator</h3>
 * <p>Prevents open redirect vulnerabilities by validating redirect URIs against a whitelist.</p>
 *
 * <p><b>Security Checks:</b></p>
 * <ul>
 *   <li>URI format validation (scheme, host, path)</li>
 *   <li>Whitelist verification</li>
 *   <li>HTTPS enforcement (production mode)</li>
 *   <li>localhost handling (development mode)</li>
 * </ul>
 *
 * <p><b>Example Usage:</b></p>
 * <pre>{@code
 * // Production mode - HTTPS required
 * RedirectUriValidator validator = RedirectUriValidator.forProduction(
 *     "https://yourdomain.com/oauth/callback"
 * );
 *
 * // Validate redirect URI
 * if (!validator.isValid(redirectUri)) {
 *     throw new InvalidRedirectUriException("Invalid redirect URI", redirectUri);
 * }
 *
 * // Development mode - localhost allowed
 * RedirectUriValidator devValidator = RedirectUriValidator.forDevelopment(
 *     "http://localhost:8080/oauth/callback"
 * );
 * }</pre>
 *
 * <h3>3. SensitiveDataMaskingUtil</h3>
 * <p>Masks sensitive data in logs to prevent accidental exposure.</p>
 *
 * <p><b>Masking Strategies:</b></p>
 * <ul>
 *   <li>{@code maskClientSecret()} - Shows first 4 and last 4 characters</li>
 *   <li>{@code maskAccessToken()} - Shows first 8 and last 4 characters</li>
 *   <li>{@code maskRefreshToken()} - Shows first 8 and last 4 characters</li>
 *   <li>{@code maskAuthorizationCode()} - Shows first 6 and last 4 characters</li>
 *   <li>{@code maskCompletely()} - Complete masking (***)</li>
 * </ul>
 *
 * <p><b>Example Usage:</b></p>
 * <pre>{@code
 * // Mask in logs
 * log.info("Token issued: {}",
 *     SensitiveDataMaskingUtil.maskAccessToken(token));
 * // Output: Token issued: access_t***1234
 *
 * // Auto-detect sensitive fields
 * String masked = SensitiveDataMaskingUtil.maskIfSensitive(
 *     "client_secret", "secret_value_12345");
 * // Output: secr***2345
 * }</pre>
 *
 * <h2>Security Best Practices</h2>
 *
 * <h3>CSRF Protection</h3>
 * <p>Always use {@link org.scriptonbasestar.oauth.client.security.SecureStateGenerator}
 * for generating state parameters. Never use predictable values like timestamps or
 * sequential numbers.</p>
 *
 * <pre>{@code
 * // ✅ Good - Cryptographically secure
 * StateGenerator generator = SecureStateGenerator.forProduction();
 * State state = generator.generate();
 *
 * // ❌ Bad - Predictable
 * State state = new State(String.valueOf(System.currentTimeMillis()));
 * }</pre>
 *
 * <h3>Redirect URI Validation</h3>
 * <p>Always validate redirect URIs to prevent open redirect attacks. Use a strict
 * whitelist approach.</p>
 *
 * <pre>{@code
 * // ✅ Good - Whitelist validation
 * RedirectUriValidator validator = new RedirectUriValidator(
 *     Set.of("https://app.example.com/callback"),
 *     false,  // Don't allow localhost in production
 *     true    // Require HTTPS
 * );
 *
 * // ❌ Bad - No validation
 * // Accepting any redirect URI is a security vulnerability
 * }</pre>
 *
 * <h3>Secure Logging</h3>
 * <p>Never log sensitive information in plain text. Always mask tokens, secrets,
 * and credentials.</p>
 *
 * <pre>{@code
 * // ✅ Good - Masked logging
 * log.debug("Access token: {}",
 *     SensitiveDataMaskingUtil.maskAccessToken(token));
 *
 * // ❌ Bad - Exposing secrets
 * log.debug("Access token: {}", token);
 * }</pre>
 *
 * <h2>Production Checklist</h2>
 *
 * <p>Before deploying to production:</p>
 * <ol>
 *   <li>Use {@code SecureStateGenerator.forProduction()} (256-bit)</li>
 *   <li>Enable HTTPS enforcement in {@code RedirectUriValidator}</li>
 *   <li>Disable localhost in {@code RedirectUriValidator}</li>
 *   <li>Set appropriate state expiration time (recommended: 10 minutes)</li>
 *   <li>Mask all sensitive data in logs</li>
 *   <li>Use external state storage (Redis) for session clustering</li>
 * </ol>
 *
 * <h2>OWASP Compliance</h2>
 *
 * <p>This package helps mitigate OWASP Top 10 vulnerabilities:</p>
 * <ul>
 *   <li><b>A01:2021 Broken Access Control</b> - Redirect URI validation</li>
 *   <li><b>A02:2021 Cryptographic Failures</b> - SecureRandom usage</li>
 *   <li><b>A03:2021 Injection</b> - URI validation</li>
 *   <li><b>A04:2021 Insecure Design</b> - CSRF protection</li>
 *   <li><b>A09:2021 Security Logging Failures</b> - Data masking</li>
 * </ul>
 *
 * @see org.scriptonbasestar.oauth.client.security.SecureStateGenerator
 * @see org.scriptonbasestar.oauth.client.security.RedirectUriValidator
 * @see org.scriptonbasestar.oauth.client.security.SensitiveDataMaskingUtil
 * @see org.scriptonbasestar.oauth.client.exception OAuth2Exception hierarchy
 * @since 1.0.0
 */
package org.scriptonbasestar.oauth.client.security;
