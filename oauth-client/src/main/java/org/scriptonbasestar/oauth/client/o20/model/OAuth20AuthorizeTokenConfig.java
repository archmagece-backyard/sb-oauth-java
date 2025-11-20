package org.scriptonbasestar.oauth.client.o20.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.scriptonbasestar.oauth.client.nobi.token.TokenExtractor;
import org.scriptonbasestar.oauth.client.o20.type.VerifierResponseType;

/**
 * @author archmagece
 * @since 2016-10-24
 */
@Getter
@Builder
public class OAuth20AuthorizeTokenConfig {
  @NonNull
  private String authorizeEndpoint;
  //@NonNull
  private String redirectUri;
  @NonNull
  private TokenExtractor tokenFormatNobi;
  private boolean oobSupport;
  //oauth spec 필수
  //3.1.1.  Response Type
  //https://tools.ietf.org/html/rfc6749#section-3.1.1
  //표준규격 안지켜지는 부분이 많음 대부분 기본값 CODE 셋팅
  //facebook, naver, kakao 기본값 CODE
  //google 필수
  private VerifierResponseType responseType;
  //없으면 안되겠지만 보통 기본 scope 나옴
  private String scope;

  public static class OAuth20AuthorizeTokenConfigBuilder {
    private boolean oobSupport = false;
    private VerifierResponseType responseType = VerifierResponseType.CODE;
  }

}
