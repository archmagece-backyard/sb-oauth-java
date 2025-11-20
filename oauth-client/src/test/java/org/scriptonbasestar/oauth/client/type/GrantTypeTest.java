package org.scriptonbasestar.oauth.client.type;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for GrantType enum
 */
class GrantTypeTest {

	@Test
	void authorization_code_shouldHaveCorrectValue() {
		assertThat(GrantType.AUTHORIZATION_CODE.getValue()).isEqualTo("authorization_code");
	}

	@Test
	void password_shouldHaveCorrectValue() {
		assertThat(GrantType.PASSWORD.getValue()).isEqualTo("password");
	}

	@Test
	void client_credentials_shouldHaveCorrectValue() {
		assertThat(GrantType.CLIENT_CREDENTIALS.getValue()).isEqualTo("client_credentials");
	}

	@Test
	void refresh_token_shouldHaveCorrectValue() {
		assertThat(GrantType.REFRESH_TOKEN.getValue()).isEqualTo("refresh_token");
	}

	@Test
	void allValues_shouldReturnConsistentValues() {
		for (GrantType type : GrantType.values()) {
			assertThat(type.getValue()).isNotNull();
			assertThat(type.getValue()).isNotEmpty();
		}
	}
}
