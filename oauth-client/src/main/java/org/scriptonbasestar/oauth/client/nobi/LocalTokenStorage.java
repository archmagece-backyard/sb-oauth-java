package org.scriptonbasestar.oauth.client.nobi;

import org.scriptonbasestar.oauth.client.model.Token;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LocalTokenStorage
    implements TokenStorage {

  private final ConcurrentMap<String, Token> map = new ConcurrentHashMap<>();

  @Override
  public Token load(String id) {
    return map.get(id);
  }

  @Override
  public void store(String id, Token token) {
    map.put(id, token);
  }

  @Override
  public void drop(String id) {
    map.remove(id);
  }
}
