package org.scriptonbasestar.oauth.client.exception;

import java.time.Duration;

/**
 * Exception thrown when a connection or request times out.
 *
 * <p>This exception is thrown when the OAuth provider doesn't respond
 * within the configured timeout period.</p>
 *
 * @since 1.0.0
 */
public class ConnectionTimeoutException extends OAuth2NetworkException {

	private static final String ERROR_CODE = "connection_timeout";

	private final Duration timeout;

	/**
	 * Creates a connection timeout exception.
	 *
	 * @param message the error message
	 */
	public ConnectionTimeoutException(String message) {
		super(message, ERROR_CODE, null, null);
		this.timeout = null;
	}

	/**
	 * Creates a connection timeout exception with timeout duration.
	 *
	 * @param message the error message
	 * @param timeout the timeout duration
	 */
	public ConnectionTimeoutException(String message, Duration timeout) {
		super(message, ERROR_CODE, null, null);
		this.timeout = timeout;
		if (timeout != null) {
			addContext("timeout_seconds", timeout.getSeconds());
		}
	}

	/**
	 * Creates a connection timeout exception with provider.
	 *
	 * @param message the error message
	 * @param timeout the timeout duration
	 * @param provider the OAuth provider name
	 * @param cause the underlying cause
	 */
	public ConnectionTimeoutException(String message, Duration timeout, String provider, Throwable cause) {
		super(message, ERROR_CODE, provider, cause);
		this.timeout = timeout;
		if (timeout != null) {
			addContext("timeout_seconds", timeout.getSeconds());
		}
	}

	/**
	 * Gets the timeout duration.
	 *
	 * @return timeout duration
	 */
	public Duration getTimeout() {
		return timeout;
	}
}
