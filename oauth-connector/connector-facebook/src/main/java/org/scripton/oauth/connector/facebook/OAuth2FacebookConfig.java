package org.scripton.oauth.connector.facebook;

import lombok.Getter;
import org.scriptonbasestar.oauth.client.config.OAuthBaseConfig;
import org.scriptonbasestar.oauth.client.o20.type.VerifierResponseType;
import org.scriptonbasestar.oauth.client.type.OAuthHttpVerb;
import org.scriptonbasestar.tool.core.check.Check;
import org.scriptonbasestar.tool.core.check.MatchPattern;

@Getter
public class OAuth2FacebookConfig
		extends OAuthBaseConfig {

	private final String redirectUri;
	private final String authorizeEndpoint;
	private final String scope;
	private final VerifierResponseType responseType = VerifierResponseType.CODE;
	private final String accessTokenEndpoint;
	private final OAuthHttpVerb accessTokenVerb;

	/**
	 * @param redirectUri
	 * @param authorizeEndpoint   https://www.facebook.com/v3.2/dialog/oauth
	 * @param scope
	 * @param accessTokenEndpoint https://graph.facebook.com/v3.2/oauth/access_token
	 * @param accessTokenVerb     OAuthHttpVerb.POST
	 */
	public OAuth2FacebookConfig(
			String clientId,
			String clientSecret,
			String redirectUri,
			String authorizeEndpoint,
			String scope,
			String accessTokenEndpoint,
			OAuthHttpVerb accessTokenVerb) {
		super(clientId, clientSecret);
		Check.customPattern(authorizeEndpoint,
							MatchPattern.url,
							"authorizeEndpoint must not null or empty, and must full uri string");
		Check.notNull(scope, "scope must not null but empty is allowed");
		Check.customPattern(accessTokenEndpoint,
							MatchPattern.url,
							"accessTokenEndpoint must not null or empty, and must full uri string");
		Check.notNull(accessTokenVerb, "accessTokenVerb must not null");
		this.redirectUri = redirectUri;
		this.authorizeEndpoint = authorizeEndpoint;
		this.scope = scope;
		this.accessTokenEndpoint = accessTokenEndpoint;
		this.accessTokenVerb = accessTokenVerb;
	}
}
