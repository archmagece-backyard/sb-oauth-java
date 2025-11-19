package org.scriptonbasestar.oauth.client.exception;

import java.time.Duration;

/**
 * Exception thrown when the OAuth state has expired.
 *
 * <p>This exception is thrown when the time between authorization request
 * and callback exceeds the maximum allowed duration.</p>
 *
 * @since 1.0.0
 */
public class StateExpiredException extends OAuth2AuthorizationException {

	private static final String ERROR_CODE = "state_expired";

	private final Duration maxAge;
	private final Duration actualAge;

	/**
	 * Creates a state expired exception.
	 *
	 * @param message the error message
	 */
	public StateExpiredException(String message) {
		super(message, ERROR_CODE, null, null);
		this.maxAge = null;
		this.actualAge = null;
	}

	/**
	 * Creates a state expired exception with age information.
	 *
	 * @param message the error message
	 * @param maxAge the maximum allowed age
	 * @param actualAge the actual age of the state
	 */
	public StateExpiredException(String message, Duration maxAge, Duration actualAge) {
		super(message, ERROR_CODE, null, null);
		this.maxAge = maxAge;
		this.actualAge = actualAge;
		if (maxAge != null) {
			addContext("max_age_seconds", maxAge.getSeconds());
		}
		if (actualAge != null) {
			addContext("actual_age_seconds", actualAge.getSeconds());
		}
	}

	/**
	 * Gets the maximum allowed age.
	 *
	 * @return maximum age
	 */
	public Duration getMaxAge() {
		return maxAge;
	}

	/**
	 * Gets the actual age of the state.
	 *
	 * @return actual age
	 */
	public Duration getActualAge() {
		return actualAge;
	}
}
