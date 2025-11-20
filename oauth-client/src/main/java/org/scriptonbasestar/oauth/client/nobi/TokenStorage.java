package org.scriptonbasestar.oauth.client.nobi;

import org.scriptonbasestar.oauth.client.model.Token;

public interface TokenStorage {
  Token load(String id);

  void store(String id, Token token);

  void drop(String id);
}
