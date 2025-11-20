package org.scriptonbasestar.oauth.client.nobi.token;

import org.scriptonbasestar.oauth.client.TokenPack;

/**
 * @author archmagece
 * @since 2016-10-26 16
 */
public interface TokenExtractor<TOKEN extends TokenPack> {
  TOKEN extract(String responseString);
}
