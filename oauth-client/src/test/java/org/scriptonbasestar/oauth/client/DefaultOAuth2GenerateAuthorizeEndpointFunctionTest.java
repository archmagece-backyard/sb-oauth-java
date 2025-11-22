package org.scriptonbasestar.oauth.client;

import org.junit.jupiter.api.Test;
import org.scriptonbasestar.oauth.client.model.State;
import org.scriptonbasestar.oauth.client.o20.type.VerifierResponseType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test class for DefaultOAuth2GenerateAuthorizeEndpointFunction
 */
class DefaultOAuth2GenerateAuthorizeEndpointFunctionTest {

  @Test
  void constructor_withValidParams_shouldCreateFunction() {
    DefaultOAuth2GenerateAuthorizeEndpointFunction function =
        new DefaultOAuth2GenerateAuthorizeEndpointFunction(
            "https://oauth.example.com/authorize",
            "http://localhost:8080/callback",
            "testClientId",
            VerifierResponseType.CODE,
            "read write"
        );

    assertThat(function).isNotNull();
  }

  @Test
  void generate_withValidState_shouldReturnAuthorizeUrl() {
    DefaultOAuth2GenerateAuthorizeEndpointFunction function =
        new DefaultOAuth2GenerateAuthorizeEndpointFunction(
            "https://oauth.example.com/authorize",
            "http://localhost:8080/callback",
            "testClientId",
            VerifierResponseType.CODE,
            "read write"
        );

    String result = function.generate(new State("test-state-123"));

    assertThat(result).startsWith("https://oauth.example.com/authorize?");
    assertThat(result).contains("client_id=testClientId");
    assertThat(result).contains("redirect_uri=http");
    assertThat(result).contains("response_type=code");
    assertThat(result).contains("scope=read+write");
    assertThat(result).contains("state=test-state-123");
  }

  @Test
  void constructor_withNullAuthorizeEndpoint_shouldThrowException() {
    assertThatThrownBy(() -> new DefaultOAuth2GenerateAuthorizeEndpointFunction(
        null,
        "http://localhost:8080/callback",
        "testClientId",
        VerifierResponseType.CODE,
        "read"
    ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("authorizeEndpoint must not null or empty");
  }

  @Test
  void constructor_withEmptyAuthorizeEndpoint_shouldThrowException() {
    assertThatThrownBy(() -> new DefaultOAuth2GenerateAuthorizeEndpointFunction(
        "",
        "http://localhost:8080/callback",
        "testClientId",
        VerifierResponseType.CODE,
        "read"
    ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("authorizeEndpoint must not null or empty");
  }

  @Test
  void constructor_withNullRedirectUri_shouldThrowException() {
    assertThatThrownBy(() -> new DefaultOAuth2GenerateAuthorizeEndpointFunction(
        "https://oauth.example.com/authorize",
        null,
        "testClientId",
        VerifierResponseType.CODE,
        "read"
    ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("redirectUri must not null or empty");
  }

  @Test
  void constructor_withInvalidRedirectUri_shouldThrowException() {
    assertThatThrownBy(() -> new DefaultOAuth2GenerateAuthorizeEndpointFunction(
        "https://oauth.example.com/authorize",
        "invalid-url",
        "testClientId",
        VerifierResponseType.CODE,
        "read"
    ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("redirectUri must not null");
  }

  @Test
  void constructor_withNullClientId_shouldThrowException() {
    assertThatThrownBy(() -> new DefaultOAuth2GenerateAuthorizeEndpointFunction(
        "https://oauth.example.com/authorize",
        "http://localhost:8080/callback",
        null,
        VerifierResponseType.CODE,
        "read"
    ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("clientId must not null or empty");
  }

  @Test
  void constructor_withNullResponseType_shouldThrowException() {
    assertThatThrownBy(() -> new DefaultOAuth2GenerateAuthorizeEndpointFunction(
        "https://oauth.example.com/authorize",
        "http://localhost:8080/callback",
        "testClientId",
        null,
        "read"
    ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("responseType must not null");
  }

  @Test
  void generate_withNullState_shouldThrowException() {
    DefaultOAuth2GenerateAuthorizeEndpointFunction function =
        new DefaultOAuth2GenerateAuthorizeEndpointFunction(
            "https://oauth.example.com/authorize",
            "http://localhost:8080/callback",
            "testClientId",
            VerifierResponseType.CODE,
            "read"
        );

    assertThatThrownBy(() -> function.generate(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("state must not null");
  }

  @Test
  void generate_withHttpsRedirectUri_shouldWork() {
    DefaultOAuth2GenerateAuthorizeEndpointFunction function =
        new DefaultOAuth2GenerateAuthorizeEndpointFunction(
            "https://oauth.example.com/authorize",
            "https://example.com:8443/oauth/callback",
            "testClientId",
            VerifierResponseType.TOKEN,
            "email profile"
        );

    String result = function.generate(new State("secure-state"));

    assertThat(result).contains("response_type=token");
    assertThat(result).contains("redirect_uri=https");
    assertThat(result).contains("scope=email+profile");
  }
}
