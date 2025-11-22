package org.scriptonbasestar.oauth.client;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for OAuth20Constants enum
 */
class OAuth20ConstantsTest {

	@Test
	void clientId_shouldHaveCorrectValue() {
		assertThat(OAuth20Constants.CLIENT_ID.getValue()).isEqualTo("client_id");
	}

	@Test
	void clientSecret_shouldHaveCorrectValue() {
		assertThat(OAuth20Constants.CLIENT_SECRET.getValue()).isEqualTo("client_secret");
	}

	@Test
	void redirectUri_shouldHaveCorrectValue() {
		assertThat(OAuth20Constants.REDIRECT_URI.getValue()).isEqualTo("redirect_uri");
	}

	@Test
	void scope_shouldHaveCorrectValue() {
		assertThat(OAuth20Constants.SCOPE.getValue()).isEqualTo("scope");
	}

	@Test
	void responseType_shouldHaveCorrectValue() {
		assertThat(OAuth20Constants.RESPONSE_TYPE.getValue()).isEqualTo("response_type");
	}

	@Test
	void state_shouldHaveCorrectValue() {
		assertThat(OAuth20Constants.STATE.getValue()).isEqualTo("state");
	}

	@Test
	void grantType_shouldHaveCorrectValue() {
		assertThat(OAuth20Constants.GRANT_TYPE.getValue()).isEqualTo("grant_type");
	}

	@Test
	void code_shouldHaveCorrectValue() {
		assertThat(OAuth20Constants.CODE.getValue()).isEqualTo("code");
	}

	@Test
	void accessToken_shouldHaveCorrectValue() {
		assertThat(OAuth20Constants.ACCESS_TOKEN.getValue()).isEqualTo("access_token");
	}

	@Test
	void refreshToken_shouldHaveCorrectValue() {
		assertThat(OAuth20Constants.REFRESH_TOKEN.getValue()).isEqualTo("refresh_token");
	}

	@Test
	void delete_shouldHaveCorrectValue() {
		assertThat(OAuth20Constants.DELETE.getValue()).isEqualTo("revoke");
	}

	@Test
	void allValues_shouldReturnConsistentValues() {
		for (OAuth20Constants constant : OAuth20Constants.values()) {
			assertThat(constant.getValue()).isNotNull();
			assertThat(constant.getValue()).isNotEmpty();
		}
	}
}
