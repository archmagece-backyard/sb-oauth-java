package org.scriptonbasestar.oauth.client.exception;

/**
 * @author archmagece
 * @since 2016-10-24
 */
public final class OAuthAuthException
    extends OAuthException {

  private static final String DEFAULT_MSG = "인증/권한 오류";

  public OAuthAuthException(String message) {
    super(message);
  }

  public OAuthAuthException(Throwable e) {
    super(DEFAULT_MSG, e);
  }

  public OAuthAuthException(String message, Throwable e) {
    super(message, e);
  }
}
