package org.scriptonbasestar.oauth.client;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for OAuthConstants
 */
class OAuthConstantsTest {

  @Test
  void constants_shouldHaveCorrectValues() {
    assertThat(OAuthConstants.REALM).isEqualTo("realm");
    assertThat(OAuthConstants.PARAM_PREFIX).isEqualTo("oauth_");
    assertThat(OAuthConstants.OUT_OF_BAND).isEqualTo("oob");
    assertThat(OAuthConstants.SCOPE).isEqualTo("scope");
  }

  @Test
  void paramPrefix_shouldBeUsableForBuilding() {
    String paramName = OAuthConstants.PARAM_PREFIX + "token";
    assertThat(paramName).isEqualTo("oauth_token");
  }
}
