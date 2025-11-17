package org.scriptonbasestar.oauth.client.exception;

/**
 * @author archmagece
 * @since 2016-10-24
 */
public final class OAuthNetworkRemoteException
		extends OAuthException {

	private static final String DEFAULT_MSG = "원격 서버 접속 오류";

	public OAuthNetworkRemoteException(String message) {
		super(message);
	}

	public OAuthNetworkRemoteException(Throwable e) {
		super(DEFAULT_MSG, e);
	}

	public OAuthNetworkRemoteException(String message, Throwable e) {
		super(message, e);
	}
}
