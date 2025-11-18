package org.scripton.oauth.connector.kakao;

import org.junit.jupiter.api.Test;
import org.scriptonbasestar.oauth.client.o20.type.AccessTokenType;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for OAuth2KakaoTokenRes
 */
class OAuth2KakaoTokenResTest {

	@Test
	void shouldCreateTokenResponseWithAllFields() {
		OAuth2KakaoTokenRes tokenRes = new OAuth2KakaoTokenRes();
		tokenRes.setAccessToken("kakao_access_token");
		tokenRes.setTokenType(AccessTokenType.BEARER);
		tokenRes.setRefreshToken("kakao_refresh_token");
		tokenRes.setExpiresIn(21600L); // 6 hours
		tokenRes.setRefreshTokenExpiresIn(5184000L); // 60 days
		tokenRes.setScope("profile_nickname,account_email");

		assertThat(tokenRes.getAccessToken()).isEqualTo("kakao_access_token");
		assertThat(tokenRes.getTokenType()).isEqualTo(AccessTokenType.BEARER);
		assertThat(tokenRes.getRefreshToken()).isEqualTo("kakao_refresh_token");
		assertThat(tokenRes.getExpiresIn()).isEqualTo(21600L);
		assertThat(tokenRes.getRefreshTokenExpiresIn()).isEqualTo(5184000L);
		assertThat(tokenRes.getScope()).isEqualTo("profile_nickname,account_email");
	}

	@Test
	void shouldHandleRefreshTokenExpiration() {
		OAuth2KakaoTokenRes tokenRes = new OAuth2KakaoTokenRes();
		tokenRes.setAccessToken("access_token");
		tokenRes.setRefreshToken("refresh_token");
		tokenRes.setRefreshTokenExpiresIn(5184000L); // 60 days in seconds

		assertThat(tokenRes.getRefreshTokenExpiresIn()).isEqualTo(5184000L);

		// Verify 60 days calculation
		long days = tokenRes.getRefreshTokenExpiresIn() / 86400;
		assertThat(days).isEqualTo(60);
	}

	@Test
	void shouldHandleNullRefreshToken() {
		OAuth2KakaoTokenRes tokenRes = new OAuth2KakaoTokenRes();
		tokenRes.setAccessToken("access_token");
		tokenRes.setTokenType(AccessTokenType.BEARER);
		tokenRes.setRefreshToken(null);
		tokenRes.setRefreshTokenExpiresIn(null);

		assertThat(tokenRes.getRefreshToken()).isNull();
		assertThat(tokenRes.getRefreshTokenExpiresIn()).isNull();
	}

	@Test
	void shouldHandleTypicalAccessTokenExpiration() {
		OAuth2KakaoTokenRes tokenRes = new OAuth2KakaoTokenRes();
		tokenRes.setAccessToken("access_token");
		tokenRes.setExpiresIn(21600L); // 6 hours

		assertThat(tokenRes.getExpiresIn()).isEqualTo(21600L);

		// Verify 6 hours calculation
		long hours = tokenRes.getExpiresIn() / 3600;
		assertThat(hours).isEqualTo(6);
	}

	@Test
	void shouldHandleMultipleScopes() {
		OAuth2KakaoTokenRes tokenRes = new OAuth2KakaoTokenRes();
		tokenRes.setAccessToken("access_token");
		tokenRes.setScope("profile_nickname,account_email,talk_message");

		assertThat(tokenRes.getScope()).contains("profile_nickname");
		assertThat(tokenRes.getScope()).contains("account_email");
		assertThat(tokenRes.getScope()).contains("talk_message");
	}

	@Test
	void shouldSupportEqualityComparison() {
		OAuth2KakaoTokenRes tokenRes1 = new OAuth2KakaoTokenRes();
		tokenRes1.setAccessToken("token");
		tokenRes1.setTokenType(AccessTokenType.BEARER);
		tokenRes1.setExpiresIn(21600L);

		OAuth2KakaoTokenRes tokenRes2 = new OAuth2KakaoTokenRes();
		tokenRes2.setAccessToken("token");
		tokenRes2.setTokenType(AccessTokenType.BEARER);
		tokenRes2.setExpiresIn(21600L);

		OAuth2KakaoTokenRes tokenRes3 = new OAuth2KakaoTokenRes();
		tokenRes3.setAccessToken("different");
		tokenRes3.setTokenType(AccessTokenType.BEARER);
		tokenRes3.setExpiresIn(21600L);

		assertThat(tokenRes1).isEqualTo(tokenRes2);
		assertThat(tokenRes1).isNotEqualTo(tokenRes3);
		assertThat(tokenRes1.hashCode()).isEqualTo(tokenRes2.hashCode());
	}

	@Test
	void shouldHandleEmptyScope() {
		OAuth2KakaoTokenRes tokenRes = new OAuth2KakaoTokenRes();
		tokenRes.setAccessToken("access_token");
		tokenRes.setScope("");

		assertThat(tokenRes.getScope()).isEmpty();
	}

	@Test
	void shouldHandleNullScope() {
		OAuth2KakaoTokenRes tokenRes = new OAuth2KakaoTokenRes();
		tokenRes.setAccessToken("access_token");
		tokenRes.setScope(null);

		assertThat(tokenRes.getScope()).isNull();
	}

	@Test
	void shouldHandleToString() {
		OAuth2KakaoTokenRes tokenRes = new OAuth2KakaoTokenRes();
		tokenRes.setAccessToken("access_token");
		tokenRes.setTokenType(AccessTokenType.BEARER);
		tokenRes.setRefreshTokenExpiresIn(5184000L);

		String toString = tokenRes.toString();
		assertThat(toString).contains("accessToken");
		assertThat(toString).contains("tokenType");
		assertThat(toString).contains("refreshTokenExpiresIn");
	}
}
