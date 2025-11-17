package org.scriptonbasestar.oauth.client.model;

import org.scriptonbasestar.oauth.client.util.Preconditions;

/**
 * OAuth Access Token or Refresh Token
 *
 * @param value The token value
 */
public record Token(String value) implements ValueModel {

	/**
	 * Compact constructor with validation
	 */
	public Token {
		Preconditions.notEmptyString(value, "Token value must not be null or empty");
	}

	/**
	 * @deprecated Use {@link #value()} instead. This method is provided for backward compatibility.
	 */
	@Deprecated(since = "2.0", forRemoval = false)
	@Override
	public String getValue() {
		return value;
	}
}
