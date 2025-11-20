package org.scriptonbasestar.oauth.client.nobi.token;

import org.scriptonbasestar.oauth.client.TokenPack;

public class PrintTokenExtractor<TOKEN extends TokenPack>
    implements TokenExtractor<TOKEN> {
  @Override
  public TOKEN extract(String responseString) {
    System.out.println("================================");
    System.out.println(responseString);
    System.out.println("================================");
    return null;
  }
}
