package org.scriptonbasestar.oauth.client.o20.model;

import org.scriptonbasestar.oauth.client.nobi.token.TokenExtractor;
import org.scriptonbasestar.oauth.client.o20.type.VerifierResponseType;
import org.scriptonbasestar.oauth.client.type.OAuthHttpVerb;
import org.scriptonbasestar.oauth.client.type.SignatureType;

/**
 * @author archmagece
 * @since 2016-10-24
 */
public class OAuth20ResourceConfig {
  private String authorizeEndpoint;
  private boolean oobSupport;
  private VerifierResponseType responseFormatType;
  private String accessTokenEndpoint;
  private OAuthHttpVerb accessTokenVerb;
  private SignatureType signatureType;
  private String redirectUri;
  private String scope;
  private TokenExtractor tokenFormatNobi;

  public static class OAuth20ResourceConfigBuilder {
    private boolean oobSupport = false;
    private VerifierResponseType responseFormatType = VerifierResponseType.CODE;
    private OAuthHttpVerb accessTokenVerb = OAuthHttpVerb.GET;
    private SignatureType signatureType = SignatureType.Header;
  }

  public String getAuthorizeEndpoint() {
    return authorizeEndpoint;
  }

  public boolean isOobSupport() {
    return oobSupport;
  }

  public VerifierResponseType getResponseFormatType() {
    return responseFormatType;
  }

  public String getAccessTokenEndpoint() {
    return accessTokenEndpoint;
  }

  public OAuthHttpVerb getAccessTokenVerb() {
    return accessTokenVerb;
  }

  public SignatureType getSignatureType() {
    return signatureType;
  }

  public String getRedirectUri() {
    return redirectUri;
  }

  public String getScope() {
    return scope;
  }

  public TokenExtractor getTokenFormatNobi() {
    return tokenFormatNobi;
  }

}
