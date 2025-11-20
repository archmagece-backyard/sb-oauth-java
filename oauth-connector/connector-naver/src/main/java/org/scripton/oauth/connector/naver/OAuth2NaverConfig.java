package org.scripton.oauth.connector.naver;

import org.scriptonbasestar.oauth.client.config.OAuthBaseConfig;
import org.scriptonbasestar.oauth.client.o20.type.VerifierResponseType;
import org.scriptonbasestar.oauth.client.type.OAuthHttpVerb;
import org.scriptonbasestar.oauth.client.util.Preconditions;

public class OAuth2NaverConfig
    extends OAuthBaseConfig {

  private final String redirectUri;
  private final String authorizeEndpoint;
  private final String scope;
  private final VerifierResponseType responseType = VerifierResponseType.CODE;
  private final String accessTokenUri;
  private final OAuthHttpVerb accessTokenVerb;

  /**
   * @param redirectUri
   * @param authorizeEndpoint
   * @param scope
   * @param accessTokenEndpoint
   * @param accessTokenVerb
   */
  public OAuth2NaverConfig(
      String clientId,
      String clientSecret,
      String redirectUri,
      String authorizeEndpoint,
      String scope,
      String accessTokenEndpoint,
      OAuthHttpVerb accessTokenVerb) {
    super(clientId, clientSecret);
    Preconditions.validUrl(
        authorizeEndpoint,
        "authorizeEndpoint must not null or empty, and must full uri string");
    Preconditions.notNull(scope, "scope must not null but empty is allowed");
    Preconditions.validUrl(
        accessTokenEndpoint,
        "accessTokenEndpoint must not null or empty, and must full uri string");
    Preconditions.notNull(accessTokenVerb, "accessTokenVerb must not null");
    this.redirectUri = redirectUri;
    this.authorizeEndpoint = authorizeEndpoint;
    this.scope = scope;
    this.accessTokenUri = accessTokenEndpoint;
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

  public String getAccessTokenUri() {
    return accessTokenUri;
  }

  public OAuthHttpVerb getAccessTokenVerb() {
    return accessTokenVerb;
  }
}
