package org.scriptonbasestar.oauth.client.exception;

/**
 * Base sealed class for all OAuth-related exceptions.
 * This sealed hierarchy enables exhaustive pattern matching and better compile-time safety.
 *
 * @author archmagece
 * @since 2016-10-24
 */
public sealed class OAuthException extends RuntimeException
	permits OAuthAuthException,
			OAuthInitException,
			OAuthNetworkException,
			OAuthNetworkRemoteException,
			OAuthParamException,
			OAuthParsingException,
			OAuthUnknownException {

	private static final String DEFAULT_MSG = "알 수 없는 실패";

	public OAuthException(String message) {
		super(message);
	}

	public OAuthException(Throwable e) {
		super(DEFAULT_MSG, e);
	}

	public OAuthException(String message, Throwable e) {
		super(message, e);
	}
}
