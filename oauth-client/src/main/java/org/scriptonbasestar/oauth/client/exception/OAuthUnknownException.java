package org.scriptonbasestar.oauth.client.exception;

public final class OAuthUnknownException
		extends OAuthException {

	private static final String DEFAULT_MSG = "알 수 없는 오류";

	public OAuthUnknownException(String message) {
		super(message);
	}

	public OAuthUnknownException(Throwable e) {
		super(DEFAULT_MSG, e);
	}

	public OAuthUnknownException(String message, Throwable e) {
		super(message, e);
	}
}
