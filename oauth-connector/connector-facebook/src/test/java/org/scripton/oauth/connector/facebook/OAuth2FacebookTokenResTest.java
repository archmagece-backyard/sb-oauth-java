package org.scripton.oauth.connector.facebook;

import org.junit.jupiter.api.Test;
import org.scriptonbasestar.oauth.client.o20.type.AccessTokenType;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for OAuth2FacebookTokenRes
 */
class OAuth2FacebookTokenResTest {

	@Test
	void shouldCreateTokenResponseWithAllFields() {
		OAuth2FacebookTokenRes tokenRes = new OAuth2FacebookTokenRes();
		tokenRes.setAccessToken("facebook_access_token");
		tokenRes.setTokenType(AccessTokenType.BEARER);
		tokenRes.setRefreshToken("facebook_refresh_token");
		tokenRes.setExpiresIn(5184000L); // 60 days

		assertThat(tokenRes.getAccessToken()).isEqualTo("facebook_access_token");
		assertThat(tokenRes.getTokenType()).isEqualTo(AccessTokenType.BEARER);
		assertThat(tokenRes.getRefreshToken()).isEqualTo("facebook_refresh_token");
		assertThat(tokenRes.getExpiresIn()).isEqualTo(5184000L);
	}

	@Test
	void shouldHandleLongLivedTokenExpiration() {
		OAuth2FacebookTokenRes tokenRes = new OAuth2FacebookTokenRes();
		tokenRes.setAccessToken("long_lived_token");
		tokenRes.setExpiresIn(5184000L); // 60 days for long-lived tokens

		long days = tokenRes.getExpiresIn() / 86400;
		assertThat(days).isEqualTo(60);
	}

	@Test
	void shouldHandleShortLivedTokenExpiration() {
		OAuth2FacebookTokenRes tokenRes = new OAuth2FacebookTokenRes();
		tokenRes.setAccessToken("short_lived_token");
		tokenRes.setExpiresIn(3600L); // 1 hour for short-lived tokens

		assertThat(tokenRes.getExpiresIn()).isEqualTo(3600L);
	}

	@Test
	void shouldHandleNullRefreshToken() {
		OAuth2FacebookTokenRes tokenRes = new OAuth2FacebookTokenRes();
		tokenRes.setAccessToken("access_token");
		tokenRes.setTokenType(AccessTokenType.BEARER);
		tokenRes.setRefreshToken(null);

		assertThat(tokenRes.getRefreshToken()).isNull();
	}

	@Test
	void shouldHandleErrorResponse() {
		OAuth2FacebookTokenRes tokenRes = new OAuth2FacebookTokenRes();
		tokenRes.setError("invalid_grant");
		tokenRes.setErrorDescription("Invalid authorization code");

		assertThat(tokenRes.getError()).isEqualTo("invalid_grant");
		assertThat(tokenRes.getErrorDescription()).isEqualTo("Invalid authorization code");
	}

	@Test
	void shouldHandleDeleteSuccessResponse() {
		OAuth2FacebookTokenRes tokenRes = new OAuth2FacebookTokenRes();
		tokenRes.setResult("success");

		assertThat(tokenRes.getResult()).isEqualTo("success");
	}

	@Test
	void shouldHandleGraphAPISpecificErrors() {
		OAuth2FacebookTokenRes tokenRes = new OAuth2FacebookTokenRes();
		tokenRes.setError("OAuthException");
		tokenRes.setErrorDescription("Error validating access token");

		assertThat(tokenRes.getError()).isEqualTo("OAuthException");
		assertThat(tokenRes.getErrorDescription()).contains("access token");
	}

	@Test
	void shouldSupportEqualityComparison() {
		OAuth2FacebookTokenRes tokenRes1 = new OAuth2FacebookTokenRes();
		tokenRes1.setAccessToken("token");
		tokenRes1.setTokenType(AccessTokenType.BEARER);
		tokenRes1.setExpiresIn(3600L);

		OAuth2FacebookTokenRes tokenRes2 = new OAuth2FacebookTokenRes();
		tokenRes2.setAccessToken("token");
		tokenRes2.setTokenType(AccessTokenType.BEARER);
		tokenRes2.setExpiresIn(3600L);

		OAuth2FacebookTokenRes tokenRes3 = new OAuth2FacebookTokenRes();
		tokenRes3.setAccessToken("different");
		tokenRes3.setTokenType(AccessTokenType.BEARER);
		tokenRes3.setExpiresIn(3600L);

		assertThat(tokenRes1).isEqualTo(tokenRes2);
		assertThat(tokenRes1).isNotEqualTo(tokenRes3);
		assertThat(tokenRes1.hashCode()).isEqualTo(tokenRes2.hashCode());
	}

	@Test
	void shouldHandleTypicalExpiresInValue() {
		OAuth2FacebookTokenRes tokenRes = new OAuth2FacebookTokenRes();
		tokenRes.setAccessToken("access_token");
		tokenRes.setExpiresIn(5184000L); // Facebook default: 60 days

		assertThat(tokenRes.getExpiresIn()).isEqualTo(5184000L);
	}

	@Test
	void shouldHandleToString() {
		OAuth2FacebookTokenRes tokenRes = new OAuth2FacebookTokenRes();
		tokenRes.setAccessToken("access_token");
		tokenRes.setTokenType(AccessTokenType.BEARER);
		tokenRes.setExpiresIn(3600L);

		String toString = tokenRes.toString();
		assertThat(toString).contains("accessToken");
		assertThat(toString).contains("tokenType");
		assertThat(toString).contains("expiresIn");
	}
}
