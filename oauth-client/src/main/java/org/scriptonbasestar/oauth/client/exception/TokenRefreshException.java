package org.scriptonbasestar.oauth.client.exception;

/**
 * Exception thrown when token refresh fails.
 *
 * <p>This exception is thrown when attempting to refresh an access token
 * using a refresh token fails, typically due to an invalid or expired
 * refresh token.</p>
 *
 * @since 1.0.0
 */
public class TokenRefreshException extends OAuth2TokenException {

	private static final String ERROR_CODE = "token_refresh_failed";

	/**
	 * Creates a token refresh exception.
	 *
	 * @param message the error message
	 */
	public TokenRefreshException(String message) {
		super(message, ERROR_CODE, null, null);
	}

	/**
	 * Creates a token refresh exception with provider.
	 *
	 * @param message the error message
	 * @param provider the OAuth provider name
	 */
	public TokenRefreshException(String message, String provider) {
		super(message, ERROR_CODE, provider, null);
	}

	/**
	 * Creates a token refresh exception with full context.
	 *
	 * @param message the error message
	 * @param provider the OAuth provider name
	 * @param cause the underlying cause
	 */
	public TokenRefreshException(String message, String provider, Throwable cause) {
		super(message, ERROR_CODE, provider, cause);
	}
}
