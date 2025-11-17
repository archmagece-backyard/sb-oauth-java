package org.scriptonbasestar.oauth.client.exception;

/**
 * @author archmagece
 * @since 2016-11-01
 */
public final class OAuthParamException
		extends OAuthException {

	private static final String DEFAULT_MSG = "파라미터 오류";

	public OAuthParamException(String message) {
		super(message);
	}

	public OAuthParamException(Throwable e) {
		super(DEFAULT_MSG, e);
	}

	public OAuthParamException(String message, Throwable e) {
		super(message, e);
	}
}
