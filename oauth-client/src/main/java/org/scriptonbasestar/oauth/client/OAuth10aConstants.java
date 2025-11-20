package org.scriptonbasestar.oauth.client;

/**
 * @author archmagece
 * @since 2016-10-24
 */
public enum OAuth10aConstants {
  TIMESTAMP("oauth_timestamp"),
  SIGN_METHOD("oauth_signature_method"),
  SIGNATURE("oauth_signature"),
  CONSUMER_SECRET("oauth_consumer_secret"),
  CONSUMER_KEY("oauth_consumer_key"),
  CALLBACK("oauth_callback"),
  VERSION("oauth_version"),
  NONCE("oauth_nonce"),
  TOKEN("oauth_token"),
  TOKEN_SECRET("oauth_token_secret"),
  VERIFIER("oauth_verifier"),
  HEADER("Authorization");

  private final String value;

  OAuth10aConstants(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public String toString() {
    return value;
  }

}
