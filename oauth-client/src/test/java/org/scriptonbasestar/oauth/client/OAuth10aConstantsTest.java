package org.scriptonbasestar.oauth.client;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for OAuth10aConstants enum
 */
class OAuth10aConstantsTest {

  @Test
  void timestamp_shouldHaveCorrectValue() {
    assertThat(OAuth10aConstants.TIMESTAMP.getValue()).isEqualTo("oauth_timestamp");
  }

  @Test
  void signMethod_shouldHaveCorrectValue() {
    assertThat(OAuth10aConstants.SIGN_METHOD.getValue()).isEqualTo("oauth_signature_method");
  }

  @Test
  void signature_shouldHaveCorrectValue() {
    assertThat(OAuth10aConstants.SIGNATURE.getValue()).isEqualTo("oauth_signature");
  }

  @Test
  void consumerSecret_shouldHaveCorrectValue() {
    assertThat(OAuth10aConstants.CONSUMER_SECRET.getValue()).isEqualTo("oauth_consumer_secret");
  }

  @Test
  void consumerKey_shouldHaveCorrectValue() {
    assertThat(OAuth10aConstants.CONSUMER_KEY.getValue()).isEqualTo("oauth_consumer_key");
  }

  @Test
  void callback_shouldHaveCorrectValue() {
    assertThat(OAuth10aConstants.CALLBACK.getValue()).isEqualTo("oauth_callback");
  }

  @Test
  void version_shouldHaveCorrectValue() {
    assertThat(OAuth10aConstants.VERSION.getValue()).isEqualTo("oauth_version");
  }

  @Test
  void nonce_shouldHaveCorrectValue() {
    assertThat(OAuth10aConstants.NONCE.getValue()).isEqualTo("oauth_nonce");
  }

  @Test
  void token_shouldHaveCorrectValue() {
    assertThat(OAuth10aConstants.TOKEN.getValue()).isEqualTo("oauth_token");
  }

  @Test
  void tokenSecret_shouldHaveCorrectValue() {
    assertThat(OAuth10aConstants.TOKEN_SECRET.getValue()).isEqualTo("oauth_token_secret");
  }

  @Test
  void verifier_shouldHaveCorrectValue() {
    assertThat(OAuth10aConstants.VERIFIER.getValue()).isEqualTo("oauth_verifier");
  }

  @Test
  void header_shouldHaveCorrectValue() {
    assertThat(OAuth10aConstants.HEADER.getValue()).isEqualTo("Authorization");
  }

  @Test
  void toString_shouldReturnValue() {
    assertThat(OAuth10aConstants.TIMESTAMP.toString()).isEqualTo("oauth_timestamp");
    assertThat(OAuth10aConstants.HEADER.toString()).isEqualTo("Authorization");
  }

  @Test
  void values_shouldReturnAllConstants() {
    OAuth10aConstants[] values = OAuth10aConstants.values();
    assertThat(values).hasSize(12);
  }

  @Test
  void valueOf_shouldReturnCorrectConstant() {
    assertThat(OAuth10aConstants.valueOf("TIMESTAMP")).isEqualTo(OAuth10aConstants.TIMESTAMP);
    assertThat(OAuth10aConstants.valueOf("CALLBACK")).isEqualTo(OAuth10aConstants.CALLBACK);
  }
}
