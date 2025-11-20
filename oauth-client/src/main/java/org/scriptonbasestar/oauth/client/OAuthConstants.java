package org.scriptonbasestar.oauth.client;


/**
 * @author archmagece
 * @since 2016-10-24
 */
public final class OAuthConstants {

  private OAuthConstants() {
    throw new AssertionError("Utility class should not be instantiated");
  }

  public static final String REALM = "realm";
  public static final String PARAM_PREFIX = "oauth_";
  public static final String OUT_OF_BAND = "oob";
  public static final String SCOPE = "scope";

}
