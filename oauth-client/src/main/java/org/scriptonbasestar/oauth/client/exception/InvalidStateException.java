package org.scriptonbasestar.oauth.client.exception;

/**
 * Exception thrown when the OAuth state parameter is invalid.
 *
 * <p>This exception indicates a potential CSRF attack or session mismatch.
 * The state parameter is used to maintain state between the authorization
 * request and callback.</p>
 *
 * @since 1.0.0
 */
public class InvalidStateException extends OAuth2AuthorizationException {

	private static final String ERROR_CODE = "invalid_state";

	private final String expectedState;
	private final String actualState;

	/**
	 * Creates an invalid state exception.
	 *
	 * @param message the error message
	 */
	public InvalidStateException(String message) {
		super(message, ERROR_CODE, null, null);
		this.expectedState = null;
		this.actualState = null;
	}

	/**
	 * Creates an invalid state exception with state values.
	 *
	 * @param message the error message
	 * @param expectedState the expected state value
	 * @param actualState the actual state value received
	 */
	public InvalidStateException(String message, String expectedState, String actualState) {
		super(message, ERROR_CODE, null, null);
		this.expectedState = expectedState;
		this.actualState = actualState;
		addContext("expected_state", expectedState);
		addContext("actual_state", actualState);
	}

	/**
	 * Creates an invalid state exception with provider.
	 *
	 * @param message the error message
	 * @param expectedState the expected state value
	 * @param actualState the actual state value received
	 * @param provider the OAuth provider name
	 */
	public InvalidStateException(String message, String expectedState, String actualState, String provider) {
		super(message, ERROR_CODE, provider, null);
		this.expectedState = expectedState;
		this.actualState = actualState;
		addContext("expected_state", expectedState);
		addContext("actual_state", actualState);
	}

	/**
	 * Gets the expected state value.
	 *
	 * @return expected state
	 */
	public String getExpectedState() {
		return expectedState;
	}

	/**
	 * Gets the actual state value received.
	 *
	 * @return actual state
	 */
	public String getActualState() {
		return actualState;
	}
}
