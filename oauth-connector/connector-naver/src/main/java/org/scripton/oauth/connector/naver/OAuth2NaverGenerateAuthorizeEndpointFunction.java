package org.scripton.oauth.connector.naver;

import org.scriptonbasestar.oauth.client.OAuth20Constants;
import org.scriptonbasestar.oauth.client.OAuth2GenerateAuthorizeEndpointFunction;
import org.scriptonbasestar.oauth.client.http.ParamList;
import org.scriptonbasestar.oauth.client.http.ParamUtil;
import org.scriptonbasestar.oauth.client.model.State;
import org.scriptonbasestar.oauth.client.util.Preconditions;

public class OAuth2NaverGenerateAuthorizeEndpointFunction
    implements OAuth2GenerateAuthorizeEndpointFunction {

  private final OAuth2NaverConfig config;

  public OAuth2NaverGenerateAuthorizeEndpointFunction(OAuth2NaverConfig config) {
    this.config = config;
  }

  /**
   * response_type string Y "code"
   * client_id string Y
   * redirect_uri string Y
   * state string Y
   * scope string N null
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
//      .add(OAuth20Constants.SCOPE, )
                           .add(OAuth20Constants.STATE, state));
  }
}
