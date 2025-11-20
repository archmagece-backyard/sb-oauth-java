package org.scripton.oauth.storage.ehcache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.scriptonbasestar.oauth.client.model.Token;
import org.scriptonbasestar.oauth.client.nobi.TokenStorage;

public class EhcacheTokenStorage implements TokenStorage {

  private Cache cache;

  public EhcacheTokenStorage(Cache cache) {
    this.cache = cache;
  }

  @Override
  public Token load(String id) {
    return (Token) cache.get(id).getObjectValue();
  }

  @Override
  public void store(String id, Token token) {
    cache.put(new Element(id, token));
  }

  @Override
  public void drop(String id) {
    cache.remove(id);
  }
}
