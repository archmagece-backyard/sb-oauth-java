package org.scripton.oauth.connector.google;

import lombok.Getter;
import org.scriptonbasestar.oauth.client.config.OAuthBaseConfig;
import org.scriptonbasestar.oauth.client.o20.type.VerifierResponseType;
import org.scriptonbasestar.oauth.client.type.OAuthHttpVerb;
import org.scriptonbasestar.oauth.client.util.Preconditions;

@Getter
public class OAuth2GoogleConfig
		extends OAuthBaseConfig {

	private final String redirectUri;
	private final String authorizeEndpoint;
	private final String scope;
	private final VerifierResponseType responseType = VerifierResponseType.CODE;
	private final String accessTokenEndpoint;
	private final OAuthHttpVerb accessTokenVerb;
	private final String revokeUrl;

	/**
	 * @param redirectUri
	 * @param authorizeEndpoint   https://accounts.google.com/o/oauth2/v2/auth
	 * @param scope               https://www.googleapis.com/auth/userinfo.profile
	 * @param accessTokenEndpoint https://www.googleapis.com/oauth2/v4/token
	 * @param accessTokenVerb     OAuthHttpVerb.POST
	 * @param revokeUrl           https://accounts.google.com/o/oauth2/revoke
	 */
	public OAuth2GoogleConfig(
			String clientId,
			String clientSecret,
			String redirectUri,
			String authorizeEndpoint,
			String scope,
			String accessTokenEndpoint,
			OAuthHttpVerb accessTokenVerb,
			String revokeUrl) {
		super(clientId, clientSecret);
		Preconditions.customPattern(
				authorizeEndpoint,
				"https?://[a-zA-Z0-9.-]+(:[0-9]+)?(/.*)?",
				"authorizeEndpoint must not null or empty, and must full uri string");
		Preconditions.notNull(scope, "scope must not null but empty is allowed");
		Preconditions.customPattern(
				accessTokenEndpoint,
				"https?://[a-zA-Z0-9.-]+(:[0-9]+)?(/.*)?",
				"accessTokenEndpoint must not null or empty, and must full uri string");
		Preconditions.notNull(accessTokenVerb, "accessTokenVerb must not null");
		this.redirectUri = redirectUri;
		this.authorizeEndpoint = authorizeEndpoint;
		this.scope = scope;
		this.accessTokenEndpoint = accessTokenEndpoint;
		this.accessTokenVerb = accessTokenVerb;
		this.revokeUrl = revokeUrl;
	}
}

