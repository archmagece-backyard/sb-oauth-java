package org.scriptonbasestar.oauth.client.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Base exception for OAuth 2.0 related errors with rich context information.
 *
 * <p>This exception provides additional context beyond standard exception messages,
 * including error codes, provider information, and timestamps for better debugging
 * and error handling.</p>
 *
 * @since 1.0.0
 */
public class OAuth2Exception extends OAuthException {

	private final String errorCode;
	private final String provider;
	private final Instant timestamp;
	private final Map<String, Object> context;

	/**
	 * Creates an exception with a message.
	 *
	 * @param message the error message
	 */
	public OAuth2Exception(String message) {
		this(message, null, null, null);
	}

	/**
	 * Creates an exception with a message and cause.
	 *
	 * @param message the error message
	 * @param cause the underlying cause
	 */
	public OAuth2Exception(String message, Throwable cause) {
		this(message, null, null, cause);
	}

	/**
	 * Creates an exception with full context.
	 *
	 * @param message the error message
	 * @param errorCode the OAuth 2.0 error code (e.g., "invalid_grant")
	 * @param provider the OAuth provider name (e.g., "naver", "google")
	 * @param cause the underlying cause
	 */
	public OAuth2Exception(String message, String errorCode, String provider, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
		this.provider = provider;
		this.timestamp = Instant.now();
		this.context = new HashMap<>();
	}

	/**
	 * Gets the OAuth 2.0 error code.
	 *
	 * @return error code, or null if not available
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Gets the OAuth provider name.
	 *
	 * @return provider name, or null if not available
	 */
	public String getProvider() {
		return provider;
	}

	/**
	 * Gets the timestamp when the exception occurred.
	 *
	 * @return exception timestamp
	 */
	public Instant getTimestamp() {
		return timestamp;
	}

	/**
	 * Gets additional context information.
	 *
	 * @return unmodifiable map of context data
	 */
	public Map<String, Object> getContext() {
		return new HashMap<>(context);
	}

	/**
	 * Adds context information to the exception.
	 *
	 * @param key context key
	 * @param value context value
	 * @return this exception for method chaining
	 */
	public OAuth2Exception addContext(String key, Object value) {
		this.context.put(key, value);
		return this;
	}

	/**
	 * Adds multiple context entries.
	 *
	 * @param contextData map of context data
	 * @return this exception for method chaining
	 */
	public OAuth2Exception addContext(Map<String, Object> contextData) {
		this.context.putAll(contextData);
		return this;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(getClass().getName());
		sb.append(": ").append(getMessage());

		if (errorCode != null) {
			sb.append(" [errorCode=").append(errorCode).append("]");
		}

		if (provider != null) {
			sb.append(" [provider=").append(provider).append("]");
		}

		sb.append(" [timestamp=").append(timestamp).append("]");

		if (!context.isEmpty()) {
			sb.append(" [context=").append(context).append("]");
		}

		return sb.toString();
	}
}
