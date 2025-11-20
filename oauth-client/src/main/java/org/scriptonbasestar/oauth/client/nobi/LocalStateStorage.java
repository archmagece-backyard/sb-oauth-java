package org.scriptonbasestar.oauth.client.nobi;

import org.scriptonbasestar.oauth.client.model.PairModel;
import org.scriptonbasestar.oauth.client.model.State;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LocalStateStorage
    implements StateStorage {

  private final String empty = "";
  private final ConcurrentMap<PairModel<String, State>, String> map = new ConcurrentHashMap<>();

  @Override
  public void add(String userId, State state) {
    map.put(new PairModel<>(userId, state), empty);
  }

  @Override
  public void exists(String userId, State state) {
    map.containsKey(new PairModel<>(userId, state));
  }
}
