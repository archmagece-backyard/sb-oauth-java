package org.scriptonbasestar.oauth.client.exception;

/**
 * Exception thrown when network communication with OAuth provider fails.
 *
 * <p>This exception covers network-related errors such as connection failures,
 * timeouts, and other I/O errors during OAuth operations.</p>
 *
 * @since 1.0.0
 */
public class OAuth2NetworkException extends OAuth2Exception {

	/**
	 * Creates a network exception with a message.
	 *
	 * @param message the error message
	 */
	public OAuth2NetworkException(String message) {
		super(message);
	}

	/**
	 * Creates a network exception with a message and cause.
	 *
	 * @param message the error message
	 * @param cause the underlying cause
	 */
	public OAuth2NetworkException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Creates a network exception with full context.
	 *
	 * @param message the error message
	 * @param errorCode the error code
	 * @param provider the provider name
	 * @param cause the underlying cause
	 */
	public OAuth2NetworkException(String message, String errorCode, String provider, Throwable cause) {
		super(message, errorCode, provider, cause);
	}
}
