package org.scriptonbasestar.oauth.client.exception;

/**
 * Exception thrown when the authorization grant is invalid.
 *
 * <p>This exception is thrown when:</p>
 * <ul>
 *   <li>The authorization code is invalid or expired</li>
 *   <li>The authorization code has already been used</li>
 *   <li>The redirect URI doesn't match the one used in authorization</li>
 * </ul>
 *
 * @since 1.0.0
 */
public class InvalidGrantException extends OAuth2AuthorizationException {

	private static final String ERROR_CODE = "invalid_grant";

	/**
	 * Creates an invalid grant exception.
	 *
	 * @param message the error message
	 */
	public InvalidGrantException(String message) {
		super(message, ERROR_CODE, null, null);
	}

	/**
	 * Creates an invalid grant exception with provider.
	 *
	 * @param message the error message
	 * @param provider the OAuth provider name
	 */
	public InvalidGrantException(String message, String provider) {
		super(message, ERROR_CODE, provider, null);
	}

	/**
	 * Creates an invalid grant exception with full context.
	 *
	 * @param message the error message
	 * @param provider the OAuth provider name
	 * @param cause the underlying cause
	 */
	public InvalidGrantException(String message, String provider, Throwable cause) {
		super(message, ERROR_CODE, provider, cause);
	}
}
