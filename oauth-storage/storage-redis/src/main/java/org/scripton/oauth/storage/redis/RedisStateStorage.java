package org.scripton.oauth.storage.redis;

import org.scriptonbasestar.oauth.client.model.State;
import org.scriptonbasestar.oauth.client.nobi.StateStorage;
import redis.clients.jedis.Jedis;

public class RedisStateStorage implements StateStorage {

  private Jedis jedis;

  public RedisStateStorage(Jedis client) {
    this.jedis = client;
  }

  @Override
  public void add(String userId, State state) {

  }

  @Override
  public void exists(String userId, State state) {

  }
}
