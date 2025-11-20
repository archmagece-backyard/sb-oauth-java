package org.scriptonbasestar.oauth.client.http;

import org.scriptonbasestar.oauth.client.OAuth20Constants;
import org.scriptonbasestar.oauth.client.model.ValueModel;
import org.scriptonbasestar.oauth.client.util.Preconditions;

import java.util.Arrays;

/**
 * @author archmagece
 * @since 2016-10-25
 * <p>
 * TODO generic.
 * TODO ParamUtil - ParamList 안으로
 * TODO
 */
public final class Param {
  protected String key;
  protected String[] values;

  public Param(String key, String... values) {
    Preconditions.notEmptyString(key, "Param.key will not empty");
    Preconditions.notEmpty(values, "Param.value will not empty");
    this.key = key;
    this.values = values;
  }

  public Param(OAuth20Constants key, String... values) {
    this(key.getValue(), values);
  }

  public Param(String key, ValueModel... values) {
    this(key, modelToStringArray(values));
  }

  public Param(OAuth20Constants key, ValueModel... values) {
    this(key.getValue(), modelToStringArray(values));
  }

  private static String[] modelToStringArray(ValueModel... value) {
    return Arrays.stream(value)
        .map(ValueModel::getValue)
        .toArray(String[]::new);
  }

  public String getKey() {
    return key;
  }

  public String[] getValues() {
    return values;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Param param = (Param) o;
    return key.equals(param.key) && Arrays.equals(values, param.values);
  }

  @Override
  public int hashCode() {
    int result = key.hashCode();
    result = 31 * result + Arrays.hashCode(values);
    return result;
  }

}
