package org.scriptonbasestar.oauth.client.exception;

import java.time.Instant;

/**
 * Exception thrown when an OAuth token has expired.
 *
 * <p>This exception is thrown when attempting to use an access token
 * that has exceeded its validity period.</p>
 *
 * @since 1.0.0
 */
public class TokenExpiredException extends OAuth2TokenException {

	private static final String ERROR_CODE = "token_expired";

	private final Instant expirationTime;

	/**
	 * Creates a token expired exception.
	 *
	 * @param message the error message
	 */
	public TokenExpiredException(String message) {
		super(message, ERROR_CODE, null, null);
		this.expirationTime = null;
	}

	/**
	 * Creates a token expired exception with expiration time.
	 *
	 * @param message the error message
	 * @param expirationTime when the token expired
	 */
	public TokenExpiredException(String message, Instant expirationTime) {
		super(message, ERROR_CODE, null, null);
		this.expirationTime = expirationTime;
		if (expirationTime != null) {
			addContext("expiration_time", expirationTime.toString());
		}
	}

	/**
	 * Creates a token expired exception with provider.
	 *
	 * @param message the error message
	 * @param expirationTime when the token expired
	 * @param provider the OAuth provider name
	 */
	public TokenExpiredException(String message, Instant expirationTime, String provider) {
		super(message, ERROR_CODE, provider, null);
		this.expirationTime = expirationTime;
		if (expirationTime != null) {
			addContext("expiration_time", expirationTime.toString());
		}
	}

	/**
	 * Gets the token expiration time.
	 *
	 * @return expiration time
	 */
	public Instant getExpirationTime() {
		return expirationTime;
	}
}
