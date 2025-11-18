package org.scripton.oauth.connector.google;

import org.junit.jupiter.api.Test;
import org.scriptonbasestar.oauth.client.o20.type.AccessTokenType;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for OAuth2GoogleTokenRes
 */
class OAuth2GoogleTokenResTest {

	@Test
	void shouldCreateTokenResponseWithAllFields() {
		OAuth2GoogleTokenRes tokenRes = new OAuth2GoogleTokenRes();
		tokenRes.setAccessToken("google_access_token");
		tokenRes.setTokenType(AccessTokenType.BEARER);
		tokenRes.setRefreshToken("google_refresh_token");
		tokenRes.setExpiresIn(3600L);
		tokenRes.setIdToken("eyJhbGciOiJSUzI1NiIsImtpZCI6IjEifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20ifQ.signature");
		tokenRes.setScope("openid profile email");

		assertThat(tokenRes.getAccessToken()).isEqualTo("google_access_token");
		assertThat(tokenRes.getTokenType()).isEqualTo(AccessTokenType.BEARER);
		assertThat(tokenRes.getRefreshToken()).isEqualTo("google_refresh_token");
		assertThat(tokenRes.getExpiresIn()).isEqualTo(3600L);
		assertThat(tokenRes.getIdToken()).isNotNull();
		assertThat(tokenRes.getScope()).isEqualTo("openid profile email");
	}

	@Test
	void shouldHandleOIDCIdToken() {
		OAuth2GoogleTokenRes tokenRes = new OAuth2GoogleTokenRes();
		String idToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjEifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJzdWIiOiIxMjM0NTY3ODkwIn0.signature";
		tokenRes.setIdToken(idToken);

		assertThat(tokenRes.getIdToken()).isEqualTo(idToken);
		assertThat(tokenRes.getIdToken()).contains(".");
	}

	@Test
	void shouldHandleNullRefreshToken() {
		OAuth2GoogleTokenRes tokenRes = new OAuth2GoogleTokenRes();
		tokenRes.setAccessToken("access_token");
		tokenRes.setTokenType(AccessTokenType.BEARER);
		tokenRes.setRefreshToken(null);

		assertThat(tokenRes.getRefreshToken()).isNull();
	}

	@Test
	void shouldHandleErrorResponse() {
		OAuth2GoogleTokenRes tokenRes = new OAuth2GoogleTokenRes();
		tokenRes.setError("invalid_grant");
		tokenRes.setErrorDescription("Invalid authorization code");

		assertThat(tokenRes.getError()).isEqualTo("invalid_grant");
		assertThat(tokenRes.getErrorDescription()).isEqualTo("Invalid authorization code");
	}

	@Test
	void shouldHandleDeleteSuccessResponse() {
		OAuth2GoogleTokenRes tokenRes = new OAuth2GoogleTokenRes();
		tokenRes.setResult("success");

		assertThat(tokenRes.getResult()).isEqualTo("success");
	}

	@Test
	void shouldHandleOpenIDConnectScopes() {
		OAuth2GoogleTokenRes tokenRes = new OAuth2GoogleTokenRes();
		tokenRes.setAccessToken("access_token");
		tokenRes.setScope("openid profile email https://www.googleapis.com/auth/userinfo.email");

		assertThat(tokenRes.getScope()).contains("openid");
		assertThat(tokenRes.getScope()).contains("profile");
		assertThat(tokenRes.getScope()).contains("email");
	}

	@Test
	void shouldSupportEqualityComparison() {
		OAuth2GoogleTokenRes tokenRes1 = new OAuth2GoogleTokenRes();
		tokenRes1.setAccessToken("token");
		tokenRes1.setTokenType(AccessTokenType.BEARER);
		tokenRes1.setIdToken("id_token");

		OAuth2GoogleTokenRes tokenRes2 = new OAuth2GoogleTokenRes();
		tokenRes2.setAccessToken("token");
		tokenRes2.setTokenType(AccessTokenType.BEARER);
		tokenRes2.setIdToken("id_token");

		OAuth2GoogleTokenRes tokenRes3 = new OAuth2GoogleTokenRes();
		tokenRes3.setAccessToken("different");
		tokenRes3.setTokenType(AccessTokenType.BEARER);
		tokenRes3.setIdToken("id_token");

		assertThat(tokenRes1).isEqualTo(tokenRes2);
		assertThat(tokenRes1).isNotEqualTo(tokenRes3);
		assertThat(tokenRes1.hashCode()).isEqualTo(tokenRes2.hashCode());
	}

	@Test
	void shouldHandleTypicalExpiresInValue() {
		OAuth2GoogleTokenRes tokenRes = new OAuth2GoogleTokenRes();
		tokenRes.setAccessToken("access_token");
		tokenRes.setExpiresIn(3600L); // 1 hour

		assertThat(tokenRes.getExpiresIn()).isEqualTo(3600L);
	}

	@Test
	void shouldHandleNullIdToken() {
		OAuth2GoogleTokenRes tokenRes = new OAuth2GoogleTokenRes();
		tokenRes.setAccessToken("access_token");
		tokenRes.setIdToken(null);

		assertThat(tokenRes.getIdToken()).isNull();
	}

	@Test
	void shouldHandleToString() {
		OAuth2GoogleTokenRes tokenRes = new OAuth2GoogleTokenRes();
		tokenRes.setAccessToken("access_token");
		tokenRes.setTokenType(AccessTokenType.BEARER);
		tokenRes.setIdToken("id_token");

		String toString = tokenRes.toString();
		assertThat(toString).contains("accessToken");
		assertThat(toString).contains("tokenType");
		assertThat(toString).contains("idToken");
	}
}
