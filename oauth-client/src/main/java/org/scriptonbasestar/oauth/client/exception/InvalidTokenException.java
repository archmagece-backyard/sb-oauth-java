package org.scriptonbasestar.oauth.client.exception;

/**
 * Exception thrown when a token is invalid or malformed.
 *
 * <p>This exception indicates that the token format is invalid,
 * the token signature is invalid, or the token cannot be verified.</p>
 *
 * @since 1.0.0
 */
public class InvalidTokenException extends OAuth2TokenException {

	private static final String ERROR_CODE = "invalid_token";

	/**
	 * Creates an invalid token exception.
	 *
	 * @param message the error message
	 */
	public InvalidTokenException(String message) {
		super(message, ERROR_CODE, null, null);
	}

	/**
	 * Creates an invalid token exception with provider.
	 *
	 * @param message the error message
	 * @param provider the OAuth provider name
	 */
	public InvalidTokenException(String message, String provider) {
		super(message, ERROR_CODE, provider, null);
	}

	/**
	 * Creates an invalid token exception with full context.
	 *
	 * @param message the error message
	 * @param provider the OAuth provider name
	 * @param cause the underlying cause
	 */
	public InvalidTokenException(String message, String provider, Throwable cause) {
		super(message, ERROR_CODE, provider, cause);
	}
}
