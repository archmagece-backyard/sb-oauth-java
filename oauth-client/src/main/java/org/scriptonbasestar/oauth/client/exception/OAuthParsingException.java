package org.scriptonbasestar.oauth.client.exception;

/**
 * @author archmagece
 * @since 2016-11-01
 */
public final class OAuthParsingException
		extends OAuthException {

	private static final String DEFAULT_MSG = "파싱 오류";

	public OAuthParsingException(String message) {
		super(message);
	}

	public OAuthParsingException(Throwable e) {
		super(DEFAULT_MSG, e);
	}

	public OAuthParsingException(String message, Throwable e) {
		super(message, e);
	}
}
