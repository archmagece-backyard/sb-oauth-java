package org.scriptonbasestar.oauth.client;

@FunctionalInterface
public interface OAuth2ResourceFunction<T> {
  T run(String accessToken);
}
