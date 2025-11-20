package org.scripton.oauth.connector.facebook;

import org.scriptonbasestar.oauth.client.OAuth20Constants;
import org.scriptonbasestar.oauth.client.OAuth2AccessTokenEndpointFunction;
import org.scriptonbasestar.oauth.client.http.HttpRequest;
import org.scriptonbasestar.oauth.client.http.ParamList;
import org.scriptonbasestar.oauth.client.model.State;
import org.scriptonbasestar.oauth.client.model.Token;
import org.scriptonbasestar.oauth.client.model.Verifier;
import org.scriptonbasestar.oauth.client.nobi.TokenStorage;
import org.scriptonbasestar.oauth.client.nobi.token.TokenExtractor;
import org.scriptonbasestar.oauth.client.type.GrantType;
import org.scriptonbasestar.oauth.client.util.Preconditions;

public class OAuth2FacebookAccessTokenEndpointFunction
    implements OAuth2AccessTokenEndpointFunction<OAuth2FacebookTokenRes> {

  private final OAuth2FacebookConfig serviceConfig;
  private final TokenExtractor<OAuth2FacebookTokenRes> tokenExtractor;
  private final TokenStorage tokenStorage;

  public OAuth2FacebookAccessTokenEndpointFunction(OAuth2FacebookConfig serviceConfig,
                           TokenExtractor<OAuth2FacebookTokenRes> tokenExtractor,
                           TokenStorage tokenStorage) {
    this.serviceConfig = serviceConfig;
    this.tokenExtractor = tokenExtractor;
    this.tokenStorage = tokenStorage;
  }

  /**
   * grant_type string Y "authorization_code"
   * client_id string Y
   * client_secret string Y
   * <p>
   * code string Y
   * state string Y
   * redirect_uri string Y
   *
   * @param verifier
   * @param state
   * @return
   */
  @Override
  public OAuth2FacebookTokenRes issue(Verifier verifier, State state) {
    Preconditions.notNull(verifier, "verifier must not null");
    Preconditions.notNull(state, "state must not null");

    ParamList paramList = new ParamList();

    paramList.add(OAuth20Constants.GRANT_TYPE, GrantType.AUTHORIZATION_CODE);
    paramList.add(OAuth20Constants.CLIENT_ID, serviceConfig.getClientId());
    paramList.add(OAuth20Constants.CLIENT_SECRET, serviceConfig.getClientSecret());

    paramList.add(OAuth20Constants.CODE, verifier);
    paramList.add(OAuth20Constants.STATE, state);
    paramList.add(OAuth20Constants.REDIRECT_URI, serviceConfig.getRedirectUri());

    HttpRequest request = HttpRequest.create(serviceConfig.getAccessTokenEndpoint(), paramList);

    return tokenExtractor.extract(request.run(serviceConfig.getAccessTokenVerb()));
  }

  /**
   * grant_type string Y
   * client_id string Y
   * client_secret string Y
   * <p>
   * refresh_token string Y
   *
   * @param refreshToken
   * @return
   */
  @Override
  public OAuth2FacebookTokenRes refresh(Token refreshToken) {
    ParamList paramList = new ParamList();

    paramList.add(OAuth20Constants.GRANT_TYPE, GrantType.REFRESH_TOKEN);
    paramList.add(OAuth20Constants.CLIENT_ID, serviceConfig.getClientId());
    paramList.add(OAuth20Constants.CLIENT_SECRET, serviceConfig.getClientSecret());

    paramList.add(OAuth20Constants.REFRESH_TOKEN, refreshToken);

    HttpRequest request = HttpRequest.create(serviceConfig.getAccessTokenEndpoint(), paramList);

    return tokenExtractor.extract(request.run(serviceConfig.getAccessTokenVerb()));
  }

  /**
   * grant_type string Y
   * client_id string Y
   * client_secret string Y
   * <p>
   * access_token string Y
   *
   * @param accessToken
   * @return
   */
  @Override
  public OAuth2FacebookTokenRes revoke(Token accessToken) {
    ParamList paramList = new ParamList();

    paramList.add(OAuth20Constants.GRANT_TYPE, GrantType.REFRESH_TOKEN);
    paramList.add(OAuth20Constants.CLIENT_ID, serviceConfig.getClientId());
    paramList.add(OAuth20Constants.CLIENT_SECRET, serviceConfig.getClientSecret());

    paramList.add(OAuth20Constants.ACCESS_TOKEN, accessToken);
    paramList.add("service_provider", "NAVER");

    HttpRequest request = HttpRequest.create(serviceConfig.getAccessTokenEndpoint(), paramList);

    return tokenExtractor.extract(request.run(serviceConfig.getAccessTokenVerb()));
  }

}
