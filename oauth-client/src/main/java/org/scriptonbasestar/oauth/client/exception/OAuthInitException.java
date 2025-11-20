package org.scriptonbasestar.oauth.client.exception;

/**
 * @author archmagece
 * @since 2016-10-24
 */
public final class OAuthInitException
    extends OAuthException {

  private static final String DEFAULT_MSG = "초기화 실패";

  public OAuthInitException(String message) {
    super(message);
  }

  public OAuthInitException(Throwable e) {
    super(DEFAULT_MSG, e);
  }

  public OAuthInitException(String message, Throwable e) {
    super(message, e);
  }
}
