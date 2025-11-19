package org.scriptonbasestar.oauth.client.exception;

/**
 * Exception thrown when network communication fails.
 *
 * <p>This exception is thrown for general network failures such as
 * connection refused, host unreachable, or other I/O errors.</p>
 *
 * @since 1.0.0
 */
public class NetworkFailureException extends OAuth2NetworkException {

	private static final String ERROR_CODE = "network_failure";

	private final String endpoint;
	private final int statusCode;

	/**
	 * Creates a network failure exception.
	 *
	 * @param message the error message
	 */
	public NetworkFailureException(String message) {
		super(message, ERROR_CODE, null, null);
		this.endpoint = null;
		this.statusCode = -1;
	}

	/**
	 * Creates a network failure exception with cause.
	 *
	 * @param message the error message
	 * @param cause the underlying cause
	 */
	public NetworkFailureException(String message, Throwable cause) {
		super(message, ERROR_CODE, null, cause);
		this.endpoint = null;
		this.statusCode = -1;
	}

	/**
	 * Creates a network failure exception with endpoint and status code.
	 *
	 * @param message the error message
	 * @param endpoint the endpoint that failed
	 * @param statusCode the HTTP status code (-1 if not available)
	 * @param cause the underlying cause
	 */
	public NetworkFailureException(String message, String endpoint, int statusCode, Throwable cause) {
		super(message, ERROR_CODE, null, cause);
		this.endpoint = endpoint;
		this.statusCode = statusCode;
		if (endpoint != null) {
			addContext("endpoint", endpoint);
		}
		if (statusCode > 0) {
			addContext("status_code", statusCode);
		}
	}

	/**
	 * Creates a network failure exception with full context.
	 *
	 * @param message the error message
	 * @param endpoint the endpoint that failed
	 * @param statusCode the HTTP status code (-1 if not available)
	 * @param provider the OAuth provider name
	 * @param cause the underlying cause
	 */
	public NetworkFailureException(String message, String endpoint, int statusCode, String provider, Throwable cause) {
		super(message, ERROR_CODE, provider, cause);
		this.endpoint = endpoint;
		this.statusCode = statusCode;
		if (endpoint != null) {
			addContext("endpoint", endpoint);
		}
		if (statusCode > 0) {
			addContext("status_code", statusCode);
		}
	}

	/**
	 * Gets the failed endpoint.
	 *
	 * @return endpoint URL
	 */
	public String getEndpoint() {
		return endpoint;
	}

	/**
	 * Gets the HTTP status code.
	 *
	 * @return status code, or -1 if not available
	 */
	public int getStatusCode() {
		return statusCode;
	}
}
