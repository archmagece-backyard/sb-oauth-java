package org.scriptonbasestar.oauth.client;

import org.scriptonbasestar.oauth.client.http.ParamList;
import org.scriptonbasestar.oauth.client.http.ParamUtil;
import org.scriptonbasestar.oauth.client.model.State;
import org.scriptonbasestar.oauth.client.o20.type.VerifierResponseType;
import org.scriptonbasestar.oauth.client.util.Preconditions;

public class DefaultOAuth2GenerateAuthorizeEndpointFunction
    implements OAuth2GenerateAuthorizeEndpointFunction {

  private final String authorizeEndpoint;
  private final String redirectUri;
  private final String clientId;
  private final VerifierResponseType responseType;
  private final String scope;

  public DefaultOAuth2GenerateAuthorizeEndpointFunction(
      String authorizeEndpoint,
      String redirectUri,
      String clientId,
      VerifierResponseType responseType,
      String scope) {
    Preconditions.notEmptyString(authorizeEndpoint, "authorizeEndpoint must not null or empty");
    Preconditions.notEmptyString(redirectUri, "redirectUri must not null or empty");
    Preconditions.customPattern(
        redirectUri,
        "https?://[a-zA-Z0-9.-]+(:[0-9]+)?(/.*)?",
        "redirectUri must not null. Check oauth service OOB support.");
    Preconditions.notEmptyString(clientId, "clientId must not null or empty");
    Preconditions.notNull(responseType, "responseType must not null or empty");
    Preconditions.notEmptyString(clientId, "clientId must not null or empty");

    this.authorizeEndpoint = authorizeEndpoint;
    this.redirectUri = redirectUri;
    this.clientId = clientId;
    this.responseType = responseType;
    this.scope = scope;
  }

  @Override
  public String generate(State state) {
    Preconditions.notNull(state, "state must not null");
    return ParamUtil.generateOAuthQuery(
        authorizeEndpoint,
        ParamList.create()
            .add(OAuth20Constants.CLIENT_ID, clientId)
            .add(OAuth20Constants.REDIRECT_URI, redirectUri)
            .add(OAuth20Constants.RESPONSE_TYPE, responseType)
            .add(OAuth20Constants.SCOPE, scope)
            .add(OAuth20Constants.STATE, state));
  }
}
