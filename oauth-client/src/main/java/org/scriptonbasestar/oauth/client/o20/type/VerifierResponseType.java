package org.scriptonbasestar.oauth.client.o20.type;

import org.scriptonbasestar.oauth.client.model.ValueModel;

/**
 * @author archmagece
 * @since 2016-10-31
 */
public enum VerifierResponseType
    implements ValueModel {

  CODE("code"),
  TOKEN("token");

  private final String value;

  VerifierResponseType(String value) {
    this.value = value;
  }

  @Override
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return value;
  }

}
