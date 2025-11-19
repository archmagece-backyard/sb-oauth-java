package org.scriptonbasestar.oauth.client.exception;

/**
 * Exception thrown when attempting to use a revoked token.
 *
 * <p>This exception indicates that the token has been explicitly revoked
 * and can no longer be used.</p>
 *
 * @since 1.0.0
 */
public class TokenRevokedException extends OAuth2TokenException {

	private static final String ERROR_CODE = "token_revoked";

	/**
	 * Creates a token revoked exception.
	 *
	 * @param message the error message
	 */
	public TokenRevokedException(String message) {
		super(message, ERROR_CODE, null, null);
	}

	/**
	 * Creates a token revoked exception with provider.
	 *
	 * @param message the error message
	 * @param provider the OAuth provider name
	 */
	public TokenRevokedException(String message, String provider) {
		super(message, ERROR_CODE, provider, null);
	}
}
