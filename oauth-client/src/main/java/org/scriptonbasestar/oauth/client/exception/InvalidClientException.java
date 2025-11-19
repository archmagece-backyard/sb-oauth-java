package org.scriptonbasestar.oauth.client.exception;

/**
 * Exception thrown when client credentials are invalid.
 *
 * <p>This exception is thrown when the client ID or client secret is invalid,
 * or when the client is not authorized for the requested operation.</p>
 *
 * @since 1.0.0
 */
public class InvalidClientException extends OAuth2ConfigurationException {

	private static final String ERROR_CODE = "invalid_client";

	/**
	 * Creates an invalid client exception with a message.
	 *
	 * @param message the error message
	 */
	public InvalidClientException(String message) {
		super(message, ERROR_CODE, null, null);
	}

	/**
	 * Creates an invalid client exception with a message and provider.
	 *
	 * @param message the error message
	 * @param provider the OAuth provider name
	 */
	public InvalidClientException(String message, String provider) {
		super(message, ERROR_CODE, provider, null);
	}

	/**
	 * Creates an invalid client exception with full context.
	 *
	 * @param message the error message
	 * @param provider the OAuth provider name
	 * @param cause the underlying cause
	 */
	public InvalidClientException(String message, String provider, Throwable cause) {
		super(message, ERROR_CODE, provider, cause);
	}
}
