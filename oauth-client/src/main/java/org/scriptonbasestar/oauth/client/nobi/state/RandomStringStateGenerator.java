package org.scriptonbasestar.oauth.client.nobi.state;

import lombok.extern.slf4j.Slf4j;
import org.scriptonbasestar.oauth.client.model.State;

/**
 * @author archmagece
 * @since 2016-10-26 16
 */
@Slf4j
public class RandomStringStateGenerator
    implements StateGenerator {

  private final char separator;

  public RandomStringStateGenerator() {
    this.separator = '-';
  }

  public RandomStringStateGenerator(char separator) {
    this.separator = separator;
  }

  @Override
  public State generate(String... values) {
    return new State(values[0] + separator + System.currentTimeMillis());
  }
}
