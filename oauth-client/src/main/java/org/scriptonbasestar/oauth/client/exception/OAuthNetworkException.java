package org.scriptonbasestar.oauth.client.exception;

/**
 * @author archmagece
 * @since 2016-10-24
 */
public final class OAuthNetworkException
		extends OAuthException {

	private static final String DEFAULT_MSG = "네트워크 오류";

	public OAuthNetworkException(String message) {
		super(message);
	}

	public OAuthNetworkException(Throwable e) {
		super(DEFAULT_MSG, e);
	}

	public OAuthNetworkException(String message, Throwable e) {
		super(message, e);
	}
}
