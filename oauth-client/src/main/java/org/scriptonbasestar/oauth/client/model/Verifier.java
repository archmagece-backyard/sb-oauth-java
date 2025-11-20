package org.scriptonbasestar.oauth.client.model;

import org.scriptonbasestar.oauth.client.util.Preconditions;

/**
 * OAuth Authorization Code (Verifier)
 *
 * @param value The authorization code value
 * @author archmagece
 * @since 2016-10-29
 */
public record Verifier(String value) implements ValueModel {

  /**
   * Compact constructor with validation
   */
  public Verifier {
    Preconditions.notEmptyString(value, "Verifier value must not be null or empty");
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
