package org.scriptonbasestar.oauth.client.exception;

/**
 * Exception thrown during OAuth 2.0 token operations.
 *
 * <p>This exception covers errors related to token issuance, refresh,
 * validation, and revocation.</p>
 *
 * @since 1.0.0
 */
public class OAuth2TokenException extends OAuth2Exception {

	/**
	 * Creates a token exception with a message.
	 *
	 * @param message the error message
	 */
	public OAuth2TokenException(String message) {
		super(message);
	}

	/**
	 * Creates a token exception with a message and cause.
	 *
	 * @param message the error message
	 * @param cause the underlying cause
	 */
	public OAuth2TokenException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Creates a token exception with full context.
	 *
	 * @param message the error message
	 * @param errorCode the OAuth 2.0 error code
	 * @param provider the provider name
	 * @param cause the underlying cause
	 */
	public OAuth2TokenException(String message, String errorCode, String provider, Throwable cause) {
		super(message, errorCode, provider, cause);
	}
}
