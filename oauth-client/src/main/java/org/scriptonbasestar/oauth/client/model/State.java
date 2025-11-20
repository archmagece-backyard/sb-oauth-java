package org.scriptonbasestar.oauth.client.model;

import org.scriptonbasestar.oauth.client.util.Preconditions;

/**
 * OAuth State parameter for CSRF protection
 *
 * @param value The state value
 * @author archmagece
 * @since 2016-10-27
 */
public record State(String value) implements ValueModel {

  /**
   * Compact constructor with validation
   */
  public State {
    Preconditions.notEmptyString(value, "State value must not be null or empty");
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
