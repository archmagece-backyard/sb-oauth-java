package org.scripton.oauth.connector.kakao;

import org.scriptonbasestar.oauth.client.config.OAuthBaseConfig;
import org.scriptonbasestar.oauth.client.o20.type.VerifierResponseType;
import org.scriptonbasestar.oauth.client.type.OAuthHttpVerb;
import org.scriptonbasestar.oauth.client.util.Preconditions;

/**
 * @author archmagece
 * @since 2017-09-12
 */
public class OAuth2KakaoConfig
    extends OAuthBaseConfig {

  private final String redirectUri;
  private final String authorizeEndpoint;
  private final String scope;
  private final VerifierResponseType responseType = VerifierResponseType.CODE;
  private final String accessTokenEndpoint;
  private final OAuthHttpVerb accessTokenVerb;

  /**
   * @param redirectUri
   * @param authorizeEndpoint
   * @param scope
   * @param accessTokenEndpoint
   * @param accessTokenVerb     OAuthHttpVerb.POST
   */
  public OAuth2KakaoConfig(
      String clientId,
      String clientSecret,
      String redirectUri,
      String authorizeEndpoint,
      String scope,
      String accessTokenEndpoint,
      OAuthHttpVerb accessTokenVerb) {
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
  }

  public String getRedirectUri() {
    return redirectUri;
  }

  public String getAuthorizeEndpoint() {
    return authorizeEndpoint;
  }

  public String getScope() {
    return scope;
  }

  public VerifierResponseType getResponseType() {
    return responseType;
  }

  public String getAccessTokenEndpoint() {
    return accessTokenEndpoint;
  }

  public OAuthHttpVerb getAccessTokenVerb() {
    return accessTokenVerb;
  }
}
