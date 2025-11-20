package org.scriptonbasestar.oauth.client;

import org.scriptonbasestar.oauth.client.o20.type.AccessTokenType;

public interface TokenPack {
  String getAccessToken();

  AccessTokenType getTokenType();

  String getRefreshToken();

  Long getExpiresIn();
}
