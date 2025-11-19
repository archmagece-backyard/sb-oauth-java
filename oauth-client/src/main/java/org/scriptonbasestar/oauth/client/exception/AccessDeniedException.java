package org.scriptonbasestar.oauth.client.exception;

/**
 * Exception thrown when the user denies authorization.
 *
 * <p>This exception is thrown when the user explicitly denies the authorization
 * request or cancels the login process.</p>
 *
 * @since 1.0.0
 */
public class AccessDeniedException extends OAuth2AuthorizationException {

	private static final String ERROR_CODE = "access_denied";

	/**
	 * Creates an access denied exception.
	 *
	 * @param message the error message
	 */
	public AccessDeniedException(String message) {
		super(message, ERROR_CODE, null, null);
	}

	/**
	 * Creates an access denied exception with provider.
	 *
	 * @param message the error message
	 * @param provider the OAuth provider name
	 */
	public AccessDeniedException(String message, String provider) {
		super(message, ERROR_CODE, provider, null);
	}
}
