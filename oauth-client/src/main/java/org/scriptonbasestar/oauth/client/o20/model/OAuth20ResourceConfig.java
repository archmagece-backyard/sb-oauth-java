package org.scriptonbasestar.oauth.client.o20.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.scriptonbasestar.oauth.client.nobi.token.TokenExtractor;
import org.scriptonbasestar.oauth.client.o20.type.VerifierResponseType;
import org.scriptonbasestar.oauth.client.type.OAuthHttpVerb;
import org.scriptonbasestar.oauth.client.type.SignatureType;

/**
 * @author archmagece
 * @since 2016-10-24
 */
@Getter
@Builder
public class OAuth20ResourceConfig {
  @NonNull
  private String authorizeEndpoint;
  private boolean oobSupport;
  private VerifierResponseType responseFormatType;
  @NonNull
  private String accessTokenEndpoint;
  private OAuthHttpVerb accessTokenVerb;
  private SignatureType signatureType;
  @NonNull
  private String redirectUri;
  private String scope;
  @NonNull
  private TokenExtractor tokenFormatNobi;

  public static class OAuth20ResourceConfigBuilder {
    private boolean oobSupport = false;
    private VerifierResponseType responseFormatType = VerifierResponseType.CODE;
    private OAuthHttpVerb accessTokenVerb = OAuthHttpVerb.GET;
    private SignatureType signatureType = SignatureType.Header;
  }

}
