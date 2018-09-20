package org.scriptonbasestar.oauth.connector.kakao;

import lombok.Data;
import org.scriptonbasestar.oauth.client.o20.type.VerifierResponseType;
import org.scriptonbasestar.oauth.client.type.OAuthHttpVerb;
import org.scriptonbasestar.tool.core.check.Check;

import javax.validation.constraints.NotNull;

/**
 * @author chaeeung.e
 * @since 2017-09-12
 */
@Data
public class OAuth2KakaoConfig {
	private static final String DEFAULT_AUTHORIZE = "https://accounts.google.com/o/oauth2/auth";
	private static final String DEFAULT_TOKEN = "https://accounts.google.com/o/oauth2/token";

	private final String authorizeUri;
	@NotNull
	private final VerifierResponseType responseType = VerifierResponseType.CODE;
	private final String tokenUri;
	private final OAuthHttpVerb tokenVerb;

	public OAuth2KakaoConfig() {
		this(DEFAULT_AUTHORIZE, DEFAULT_TOKEN, OAuthHttpVerb.POST);
	}

	public OAuth2KakaoConfig(String authorizeUri, String tokenUri, OAuthHttpVerb tokenVerb) {
		Check.urlDomainPattern(authorizeUri, "authorizeUri must not null or empty, and must full uri string");
		Check.urlDomainPattern(tokenUri, "tokenUri must not null or empty, and must full uri string");
		Check.notNull(tokenVerb, "tokenVerb must not null");
		this.authorizeUri = authorizeUri;
		this.tokenUri = tokenUri;
		this.tokenVerb = tokenVerb;
	}
}
