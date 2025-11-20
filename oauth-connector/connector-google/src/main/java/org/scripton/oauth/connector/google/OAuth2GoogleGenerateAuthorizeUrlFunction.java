package org.scripton.oauth.connector.google;

import org.scriptonbasestar.oauth.client.OAuth20Constants;
import org.scriptonbasestar.oauth.client.OAuth2GenerateAuthorizeEndpointFunction;
import org.scriptonbasestar.oauth.client.http.ParamList;
import org.scriptonbasestar.oauth.client.http.ParamUtil;
import org.scriptonbasestar.oauth.client.model.State;
import org.scriptonbasestar.oauth.client.util.Preconditions;

public class OAuth2GoogleGenerateAuthorizeUrlFunction
    implements OAuth2GenerateAuthorizeEndpointFunction {

  private final OAuth2GoogleConfig config;

  public OAuth2GoogleGenerateAuthorizeUrlFunction(OAuth2GoogleConfig config) {
    this.config = config;
  }

  /**
   * client_id string Y
   * redirect_uri string Y
   * scope string Y
   * access_type string Recommended online/offline
   * state string Recommended
   * include_granted_scopes string N true/false
   * login_hint string N 유저 정보를 알고있는경우 email/id 등
   * prompt string N none/consent/select_account
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
                           .add(OAuth20Constants.SCOPE, config.getScope())
                           .add(OAuth20Constants.STATE, state));
  }
}
