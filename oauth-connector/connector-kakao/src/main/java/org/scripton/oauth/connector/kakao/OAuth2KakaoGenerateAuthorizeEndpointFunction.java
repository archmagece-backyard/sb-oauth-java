package org.scripton.oauth.connector.kakao;

import org.scriptonbasestar.oauth.client.OAuth20Constants;
import org.scriptonbasestar.oauth.client.OAuth2GenerateAuthorizeEndpointFunction;
import org.scriptonbasestar.oauth.client.http.ParamList;
import org.scriptonbasestar.oauth.client.http.ParamUtil;
import org.scriptonbasestar.oauth.client.model.State;
import org.scriptonbasestar.oauth.client.util.Preconditions;

public class OAuth2KakaoGenerateAuthorizeEndpointFunction
		implements OAuth2GenerateAuthorizeEndpointFunction {

	private final OAuth2KakaoConfig config;

	public OAuth2KakaoGenerateAuthorizeEndpointFunction(OAuth2KakaoConfig config) {
		this.config = config;
	}

	/**
	 * client_id string Y
	 * redirect_uri string Y
	 * response_type string Y
	 * state string N
	 * encode_state boolean N "false"
	 *
	 * @param state
	 * @return
	 */
	@Override
	public String generate(State state) {
		Preconditions.notNull(state, "state must not null");

		return ParamUtil.generateOAuthQuery(config.getAuthorizeEndpoint(),
											ParamList.create()
													 .add(OAuth20Constants.CLIENT_ID, config.getClientId())
													 .add(OAuth20Constants.REDIRECT_URI, config.getRedirectUri())
													 .add(OAuth20Constants.RESPONSE_TYPE, config.getResponseType())
													 .add(OAuth20Constants.STATE, state)
													 .add("encode_state", "false"));
	}
}
