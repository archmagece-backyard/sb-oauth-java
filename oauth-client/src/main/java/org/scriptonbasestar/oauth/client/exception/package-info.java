/**
 * OAuth 2.0 exception hierarchy for structured error handling.
 *
 * <p>This package provides a comprehensive exception hierarchy aligned with OAuth 2.0
 * error codes and best practices. All exceptions include rich context information
 * for debugging and monitoring.</p>
 *
 * <h2>Exception Hierarchy</h2>
 *
 * <pre>
 * {@link org.scriptonbasestar.oauth.client.exception.OAuth2Exception} (base)
 *   ├─ {@link org.scriptonbasestar.oauth.client.exception.OAuth2ConfigurationException}
 *   │   ├─ {@link org.scriptonbasestar.oauth.client.exception.InvalidClientException} (invalid_client)
 *   │   ├─ {@link org.scriptonbasestar.oauth.client.exception.InvalidRedirectUriException} (invalid_redirect_uri)
 *   │   └─ {@link org.scriptonbasestar.oauth.client.exception.MissingConfigurationException} (missing_configuration)
 *   │
 *   ├─ {@link org.scriptonbasestar.oauth.client.exception.OAuth2AuthorizationException}
 *   │   ├─ {@link org.scriptonbasestar.oauth.client.exception.InvalidGrantException} (invalid_grant)
 *   │   ├─ {@link org.scriptonbasestar.oauth.client.exception.InvalidStateException} (invalid_state)
 *   │   ├─ {@link org.scriptonbasestar.oauth.client.exception.AccessDeniedException} (access_denied)
 *   │   └─ {@link org.scriptonbasestar.oauth.client.exception.StateExpiredException} (state_expired)
 *   │
 *   ├─ {@link org.scriptonbasestar.oauth.client.exception.OAuth2TokenException}
 *   │   ├─ {@link org.scriptonbasestar.oauth.client.exception.TokenExpiredException} (token_expired)
 *   │   ├─ {@link org.scriptonbasestar.oauth.client.exception.TokenRevokedException} (token_revoked)
 *   │   ├─ {@link org.scriptonbasestar.oauth.client.exception.InvalidTokenException} (invalid_token)
 *   │   └─ {@link org.scriptonbasestar.oauth.client.exception.TokenRefreshException} (token_refresh_failed)
 *   │
 *   └─ {@link org.scriptonbasestar.oauth.client.exception.OAuth2NetworkException}
 *       ├─ {@link org.scriptonbasestar.oauth.client.exception.ConnectionTimeoutException} (connection_timeout)
 *       └─ {@link org.scriptonbasestar.oauth.client.exception.NetworkFailureException} (network_failure)
 * </pre>
 *
 * <h2>Core Features</h2>
 *
 * <h3>1. Rich Context Information</h3>
 * <p>All exceptions extend {@link org.scriptonbasestar.oauth.client.exception.OAuth2Exception}
 * and include:</p>
 * <ul>
 *   <li><b>Error Code</b> - OAuth 2.0 standard error code</li>
 *   <li><b>Provider</b> - OAuth provider name (naver, kakao, google, facebook)</li>
 *   <li><b>Timestamp</b> - When the exception occurred</li>
 *   <li><b>Context Map</b> - Additional debugging information</li>
 * </ul>
 *
 * <h3>2. Method Chaining</h3>
 * <p>Exceptions support fluent API for adding context:</p>
 * <pre>{@code
 * throw new OAuth2Exception("OAuth failed", "custom_error", "naver", cause)
 *     .addContext("user_id", userId)
 *     .addContext("request_id", requestId)
 *     .addContext("endpoint", endpoint);
 * }</pre>
 *
 * <h3>3. Type-Safe Error Handling</h3>
 * <p>Catch exceptions by category:</p>
 * <pre>{@code
 * try {
 *     // OAuth operation
 * } catch (OAuth2ConfigurationException e) {
 *     // Configuration errors - fix settings
 * } catch (OAuth2AuthorizationException e) {
 *     // Authorization errors - user interaction needed
 * } catch (OAuth2TokenException e) {
 *     // Token errors - refresh or re-authenticate
 * } catch (OAuth2NetworkException e) {
 *     // Network errors - retry
 * }
 * }</pre>
 *
 * <h2>Configuration Exceptions</h2>
 *
 * <p>Thrown when OAuth configuration is invalid or missing.</p>
 *
 * <h3>InvalidClientException</h3>
 * <pre>{@code
 * // Client credentials invalid
 * throw new InvalidClientException("Invalid client ID", "naver");
 * }</pre>
 *
 * <h3>InvalidRedirectUriException</h3>
 * <pre>{@code
 * // Redirect URI not whitelisted
 * throw new InvalidRedirectUriException(
 *     "Redirect URI not whitelisted",
 *     "https://malicious.com/callback",
 *     "google"
 * );
 * }</pre>
 *
 * <h3>MissingConfigurationException</h3>
 * <pre>{@code
 * // Required configuration missing
 * throw new MissingConfigurationException(
 *     "Client secret is required",
 *     "client_secret",
 *     "kakao"
 * );
 * }</pre>
 *
 * <h2>Authorization Exceptions</h2>
 *
 * <p>Thrown during OAuth authorization flow.</p>
 *
 * <h3>InvalidGrantException</h3>
 * <pre>{@code
 * // Authorization code invalid or expired
 * throw new InvalidGrantException(
 *     "Authorization code expired",
 *     "naver"
 * );
 * }</pre>
 *
 * <h3>InvalidStateException</h3>
 * <pre>{@code
 * // CSRF protection - state mismatch
 * throw new InvalidStateException(
 *     "State mismatch (potential CSRF attack)",
 *     expectedState,
 *     actualState,
 *     "google"
 * );
 * }</pre>
 *
 * <h3>AccessDeniedException</h3>
 * <pre>{@code
 * // User denied authorization
 * throw new AccessDeniedException("User denied access", "kakao");
 * }</pre>
 *
 * <h3>StateExpiredException</h3>
 * <pre>{@code
 * // State expired (timeout)
 * throw new StateExpiredException(
 *     "State expired",
 *     Duration.ofMinutes(10),  // max age
 *     Duration.ofMinutes(15)   // actual age
 * );
 * }</pre>
 *
 * <h2>Token Exceptions</h2>
 *
 * <p>Thrown during token operations (issue, refresh, validate).</p>
 *
 * <h3>TokenExpiredException</h3>
 * <pre>{@code
 * // Access token expired
 * throw new TokenExpiredException(
 *     "Access token expired",
 *     Instant.now().minusSeconds(3600),
 *     "naver"
 * );
 * }</pre>
 *
 * <h3>TokenRevokedException</h3>
 * <pre>{@code
 * // Token explicitly revoked
 * throw new TokenRevokedException("Token has been revoked", "google");
 * }</pre>
 *
 * <h3>InvalidTokenException</h3>
 * <pre>{@code
 * // Token format invalid
 * throw new InvalidTokenException("Malformed token", "kakao");
 * }</pre>
 *
 * <h3>TokenRefreshException</h3>
 * <pre>{@code
 * // Refresh token failed
 * throw new TokenRefreshException(
 *     "Refresh token expired",
 *     "facebook",
 *     cause
 * );
 * }</pre>
 *
 * <h2>Network Exceptions</h2>
 *
 * <p>Thrown when network communication fails.</p>
 *
 * <h3>ConnectionTimeoutException</h3>
 * <pre>{@code
 * // Request timeout
 * throw new ConnectionTimeoutException(
 *     "Request timeout",
 *     Duration.ofSeconds(30),
 *     "google",
 *     cause
 * );
 * }</pre>
 *
 * <h3>NetworkFailureException</h3>
 * <pre>{@code
 * // Network error
 * throw new NetworkFailureException(
 *     "Connection refused",
 *     "https://oauth.example.com/token",
 *     503,  // HTTP status code
 *     "naver",
 *     cause
 * );
 * }</pre>
 *
 * <h2>Error Handling Best Practices</h2>
 *
 * <h3>1. Catch by Category</h3>
 * <pre>{@code
 * try {
 *     oauth2Service.login();
 * } catch (InvalidStateException e) {
 *     // CSRF attack detected
 *     log.error("CSRF validation failed: {}", e.getMessage());
 *     return "redirect:/error?csrf";
 * } catch (OAuth2AuthorizationException e) {
 *     // Other authorization errors
 *     log.warn("Authorization failed: {}", e.getErrorCode());
 *     return "redirect:/login?error=" + e.getErrorCode();
 * } catch (OAuth2NetworkException e) {
 *     // Network issues - retry
 *     log.error("Network error: {}", e.getMessage(), e);
 *     retryService.scheduleRetry();
 * }
 * }</pre>
 *
 * <h3>2. Add Context for Debugging</h3>
 * <pre>{@code
 * try {
 *     tokenResponse = exchangeCode(code);
 * } catch (OAuth2Exception e) {
 *     throw e.addContext("code_length", code.length())
 *            .addContext("provider", "naver")
 *            .addContext("user_id", userId);
 * }
 * }</pre>
 *
 * <h3>3. Log with Context</h3>
 * <pre>{@code
 * catch (OAuth2Exception e) {
 *     log.error("OAuth error [code={}, provider={}, timestamp={}]: {}",
 *         e.getErrorCode(),
 *         e.getProvider(),
 *         e.getTimestamp(),
 *         e.getMessage(),
 *         e);
 *
 *     // Log context if available
 *     if (!e.getContext().isEmpty()) {
 *         log.debug("Error context: {}", e.getContext());
 *     }
 * }
 * }</pre>
 *
 * <h3>4. User-Friendly Error Messages</h3>
 * <pre>{@code
 * catch (AccessDeniedException e) {
 *     return "You denied access. Please try again.";
 * } catch (StateExpiredException e) {
 *     return "Login session expired. Please start over.";
 * } catch (OAuth2NetworkException e) {
 *     return "Service temporarily unavailable. Please try again later.";
 * }
 * }</pre>
 *
 * <h2>Error Code Reference</h2>
 *
 * <p>OAuth 2.0 standard error codes:</p>
 * <ul>
 *   <li><b>invalid_client</b> - Client authentication failed</li>
 *   <li><b>invalid_grant</b> - Invalid authorization code or refresh token</li>
 *   <li><b>invalid_request</b> - Malformed request</li>
 *   <li><b>invalid_scope</b> - Invalid or excessive scope</li>
 *   <li><b>invalid_token</b> - Token is invalid or malformed</li>
 *   <li><b>unauthorized_client</b> - Client not authorized</li>
 *   <li><b>unsupported_grant_type</b> - Grant type not supported</li>
 *   <li><b>access_denied</b> - Resource owner denied access</li>
 * </ul>
 *
 * @see org.scriptonbasestar.oauth.client.exception.OAuth2Exception
 * @see <a href="https://tools.ietf.org/html/rfc6749#section-5.2">RFC 6749 Section 5.2 - Error Response</a>
 * @since 1.0.0
 */
package org.scriptonbasestar.oauth.client.exception;
