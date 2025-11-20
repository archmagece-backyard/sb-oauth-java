package org.scriptonbasestar.oauth.client.nobi.state;

import org.scriptonbasestar.oauth.client.model.State;

/**
 * @author archmagece
 * @since 2016-10-27
 */
public interface StateGenerator {
  //TODO 수정. 파라미터......
  State generate(String... values);
}
