package org.scripton.oauth.storage.redis;

import org.scriptonbasestar.oauth.client.model.Token;
import org.scriptonbasestar.oauth.client.nobi.TokenStorage;
import redis.clients.jedis.Jedis;

public class RedisTokenStorage implements TokenStorage {

  private final Jedis jedis;

  public RedisTokenStorage(Jedis client) {
    this.jedis = client;
  }

  @Override
  public Token load(String id) {
    return new Token(jedis.get(id));
  }

  @Override
  public void store(String id, Token token) {
    jedis.set(id, token.getValue());
  }

  @Override
  public void drop(String id) {
    jedis.del(id);
  }
}
