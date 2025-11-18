package org.scripton.oauth.connector.naver;

import org.junit.jupiter.api.Test;
import org.scriptonbasestar.oauth.client.o20.type.AccessTokenType;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for OAuth2NaverTokenRes
 */
class OAuth2NaverTokenResTest {

	@Test
	void shouldCreateTokenResponseWithAllFields() {
		OAuth2NaverTokenRes tokenRes = new OAuth2NaverTokenRes();
		tokenRes.setAccessToken("naver_access_token");
		tokenRes.setTokenType(AccessTokenType.BEARER);
		tokenRes.setRefreshToken("naver_refresh_token");
		tokenRes.setExpiresIn(3600L);

		assertThat(tokenRes.getAccessToken()).isEqualTo("naver_access_token");
		assertThat(tokenRes.getTokenType()).isEqualTo(AccessTokenType.BEARER);
		assertThat(tokenRes.getRefreshToken()).isEqualTo("naver_refresh_token");
		assertThat(tokenRes.getExpiresIn()).isEqualTo(3600L);
	}

	@Test
	void shouldHandleNullRefreshToken() {
		OAuth2NaverTokenRes tokenRes = new OAuth2NaverTokenRes();
		tokenRes.setAccessToken("access_token");
		tokenRes.setTokenType(AccessTokenType.BEARER);
		tokenRes.setRefreshToken(null);

		assertThat(tokenRes.getRefreshToken()).isNull();
	}

	@Test
	void shouldHandleErrorResponse() {
		OAuth2NaverTokenRes tokenRes = new OAuth2NaverTokenRes();
		tokenRes.setError("invalid_request");
		tokenRes.setErrorDescription("Invalid client credentials");

		assertThat(tokenRes.getError()).isEqualTo("invalid_request");
		assertThat(tokenRes.getErrorDescription()).isEqualTo("Invalid client credentials");
	}

	@Test
	void shouldHandleDeleteSuccessResponse() {
		OAuth2NaverTokenRes tokenRes = new OAuth2NaverTokenRes();
		tokenRes.setResult("success");

		assertThat(tokenRes.getResult()).isEqualTo("success");
	}

	@Test
	void shouldSupportEqualityComparison() {
		OAuth2NaverTokenRes tokenRes1 = new OAuth2NaverTokenRes();
		tokenRes1.setAccessToken("token");
		tokenRes1.setTokenType(AccessTokenType.BEARER);

		OAuth2NaverTokenRes tokenRes2 = new OAuth2NaverTokenRes();
		tokenRes2.setAccessToken("token");
		tokenRes2.setTokenType(AccessTokenType.BEARER);

		OAuth2NaverTokenRes tokenRes3 = new OAuth2NaverTokenRes();
		tokenRes3.setAccessToken("different");
		tokenRes3.setTokenType(AccessTokenType.BEARER);

		assertThat(tokenRes1).isEqualTo(tokenRes2);
		assertThat(tokenRes1).isNotEqualTo(tokenRes3);
		assertThat(tokenRes1.hashCode()).isEqualTo(tokenRes2.hashCode());
	}

	@Test
	void shouldHandlePermanentRefreshToken() {
		// Naver provides permanent refresh tokens
		OAuth2NaverTokenRes tokenRes = new OAuth2NaverTokenRes();
		tokenRes.setAccessToken("access_token");
		tokenRes.setRefreshToken("permanent_refresh_token");
		tokenRes.setExpiresIn(3600L);

		assertThat(tokenRes.getRefreshToken()).isNotNull();
		assertThat(tokenRes.getRefreshToken()).isEqualTo("permanent_refresh_token");
	}

	@Test
	void shouldHandleVariousExpiresInValues() {
		OAuth2NaverTokenRes tokenRes = new OAuth2NaverTokenRes();

		// 1 hour
		tokenRes.setExpiresIn(3600L);
		assertThat(tokenRes.getExpiresIn()).isEqualTo(3600L);

		// 24 hours
		tokenRes.setExpiresIn(86400L);
		assertThat(tokenRes.getExpiresIn()).isEqualTo(86400L);

		// null (permanent)
		tokenRes.setExpiresIn(null);
		assertThat(tokenRes.getExpiresIn()).isNull();
	}

	@Test
	void shouldHandleToString() {
		OAuth2NaverTokenRes tokenRes = new OAuth2NaverTokenRes();
		tokenRes.setAccessToken("access_token");
		tokenRes.setTokenType(AccessTokenType.BEARER);

		String toString = tokenRes.toString();
		assertThat(toString).contains("accessToken");
		assertThat(toString).contains("tokenType");
	}
}
