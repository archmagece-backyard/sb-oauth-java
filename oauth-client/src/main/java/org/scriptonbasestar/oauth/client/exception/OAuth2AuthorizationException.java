package org.scriptonbasestar.oauth.client.exception;

/**
 * Exception thrown during OAuth 2.0 authorization process.
 *
 * <p>This exception covers errors that occur during the authorization code flow,
 * including invalid grants, state mismatches, and access denied errors.</p>
 *
 * @since 1.0.0
 */
public class OAuth2AuthorizationException extends OAuth2Exception {

	/**
	 * Creates an authorization exception with a message.
	 *
	 * @param message the error message
	 */
	public OAuth2AuthorizationException(String message) {
		super(message);
	}

	/**
	 * Creates an authorization exception with a message and cause.
	 *
	 * @param message the error message
	 * @param cause the underlying cause
	 */
	public OAuth2AuthorizationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Creates an authorization exception with full context.
	 *
	 * @param message the error message
	 * @param errorCode the OAuth 2.0 error code
	 * @param provider the provider name
	 * @param cause the underlying cause
	 */
	public OAuth2AuthorizationException(String message, String errorCode, String provider, Throwable cause) {
		super(message, errorCode, provider, cause);
	}
}
