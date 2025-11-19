package org.scriptonbasestar.oauth.client.exception;

/**
 * Exception thrown when OAuth 2.0 configuration is invalid or missing.
 *
 * <p>This exception indicates problems with client configuration such as
 * missing client ID, invalid redirect URI, or other configuration errors.</p>
 *
 * @since 1.0.0
 */
public class OAuth2ConfigurationException extends OAuth2Exception {

	/**
	 * Creates a configuration exception with a message.
	 *
	 * @param message the error message
	 */
	public OAuth2ConfigurationException(String message) {
		super(message);
	}

	/**
	 * Creates a configuration exception with a message and cause.
	 *
	 * @param message the error message
	 * @param cause the underlying cause
	 */
	public OAuth2ConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Creates a configuration exception with full context.
	 *
	 * @param message the error message
	 * @param errorCode the error code
	 * @param provider the provider name
	 * @param cause the underlying cause
	 */
	public OAuth2ConfigurationException(String message, String errorCode, String provider, Throwable cause) {
		super(message, errorCode, provider, cause);
	}
}
