package org.scriptonbasestar.oauth.client.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test class for OAuthBaseConfig
 */
class OAuthBaseConfigTest {

  // Test implementation of abstract class
  private static class TestOAuthConfig extends OAuthBaseConfig {
    public TestOAuthConfig(String clientId, String clientSecret) {
      super(clientId, clientSecret);
    }
  }

  @Test
  void constructor_withValidParams_shouldCreateConfig() {
    TestOAuthConfig config = new TestOAuthConfig("testClientId", "testSecret");

    assertThat(config.getClientId()).isEqualTo("testClientId");
    assertThat(config.getClientSecret()).isEqualTo("testSecret");
  }

  @Test
  void constructor_withNullSecret_shouldAllowNull() {
    // Kakao allows null secret
    TestOAuthConfig config = new TestOAuthConfig("testClientId", null);

    assertThat(config.getClientId()).isEqualTo("testClientId");
    assertThat(config.getClientSecret()).isNull();
  }

  @Test
  void constructor_withNullClientId_shouldThrowException() {
    assertThatThrownBy(() -> new TestOAuthConfig(null, "testSecret"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("clientId should not null or empty");
  }

  @Test
  void constructor_withEmptyClientId_shouldThrowException() {
    assertThatThrownBy(() -> new TestOAuthConfig("", "testSecret"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("clientId should not null or empty");
  }

  @Test
  void getClientId_shouldReturnSameValue() {
    TestOAuthConfig config = new TestOAuthConfig("myClientId", "mySecret");

    assertThat(config.getClientId()).isEqualTo("myClientId");
  }

  @Test
  void getClientSecret_shouldReturnSameValue() {
    TestOAuthConfig config = new TestOAuthConfig("myClientId", "mySecret");

    assertThat(config.getClientSecret()).isEqualTo("mySecret");
  }
}
