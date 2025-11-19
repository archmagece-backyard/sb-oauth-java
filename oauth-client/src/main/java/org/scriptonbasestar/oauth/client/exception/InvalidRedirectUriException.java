package org.scriptonbasestar.oauth.client.exception;

/**
 * Exception thrown when redirect URI is invalid or not whitelisted.
 *
 * <p>This exception helps prevent open redirect vulnerabilities by rejecting
 * invalid or unauthorized redirect URIs.</p>
 *
 * @since 1.0.0
 */
public class InvalidRedirectUriException extends OAuth2ConfigurationException {

	private static final String ERROR_CODE = "invalid_redirect_uri";

	private final String redirectUri;

	/**
	 * Creates an invalid redirect URI exception.
	 *
	 * @param message the error message
	 * @param redirectUri the invalid redirect URI
	 */
	public InvalidRedirectUriException(String message, String redirectUri) {
		super(message, ERROR_CODE, null, null);
		this.redirectUri = redirectUri;
		addContext("redirect_uri", redirectUri);
	}

	/**
	 * Creates an invalid redirect URI exception with provider.
	 *
	 * @param message the error message
	 * @param redirectUri the invalid redirect URI
	 * @param provider the OAuth provider name
	 */
	public InvalidRedirectUriException(String message, String redirectUri, String provider) {
		super(message, ERROR_CODE, provider, null);
		this.redirectUri = redirectUri;
		addContext("redirect_uri", redirectUri);
	}

	/**
	 * Gets the invalid redirect URI.
	 *
	 * @return the redirect URI that caused the exception
	 */
	public String getRedirectUri() {
		return redirectUri;
	}
}
