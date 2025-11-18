package org.scripton.oauth.connector.naver;

import org.junit.jupiter.api.Test;
import org.scriptonbasestar.oauth.client.o20.type.VerifierResponseType;
import org.scriptonbasestar.oauth.client.type.OAuthHttpVerb;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for OAuth2NaverConfig
 */
class OAuth2NaverConfigTest {

	private static final String CLIENT_ID = "naver_client_id";
	private static final String CLIENT_SECRET = "naver_client_secret";
	private static final String REDIRECT_URI = "http://localhost:8080/callback";
	private static final String AUTHORIZE_ENDPOINT = "https://nid.naver.com/oauth2.0/authorize";
	private static final String SCOPE = "profile,email";
	private static final String TOKEN_ENDPOINT = "https://nid.naver.com/oauth2.0/token";

	@Test
	void shouldCreateConfigWithValidParameters() {
		OAuth2NaverConfig config = new OAuth2NaverConfig(
				CLIENT_ID,
				CLIENT_SECRET,
				REDIRECT_URI,
				AUTHORIZE_ENDPOINT,
				SCOPE,
				TOKEN_ENDPOINT,
				OAuthHttpVerb.POST
		);

		assertThat(config.getClientId()).isEqualTo(CLIENT_ID);
		assertThat(config.getClientSecret()).isEqualTo(CLIENT_SECRET);
		assertThat(config.getRedirectUri()).isEqualTo(REDIRECT_URI);
		assertThat(config.getAuthorizeEndpoint()).isEqualTo(AUTHORIZE_ENDPOINT);
		assertThat(config.getScope()).isEqualTo(SCOPE);
		assertThat(config.getAccessTokenUri()).isEqualTo(TOKEN_ENDPOINT);
		assertThat(config.getAccessTokenVerb()).isEqualTo(OAuthHttpVerb.POST);
		assertThat(config.getResponseType()).isEqualTo(VerifierResponseType.CODE);
	}

	@Test
	void shouldThrowExceptionForInvalidAuthorizeEndpoint() {
		assertThatThrownBy(() -> new OAuth2NaverConfig(
				CLIENT_ID,
				CLIENT_SECRET,
				REDIRECT_URI,
				"invalid-url",
				SCOPE,
				TOKEN_ENDPOINT,
				OAuthHttpVerb.POST
		)).isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("authorizeEndpoint");
	}

	@Test
	void shouldThrowExceptionForNullAuthorizeEndpoint() {
		assertThatThrownBy(() -> new OAuth2NaverConfig(
				CLIENT_ID,
				CLIENT_SECRET,
				REDIRECT_URI,
				null,
				SCOPE,
				TOKEN_ENDPOINT,
				OAuthHttpVerb.POST
		)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void shouldThrowExceptionForInvalidTokenEndpoint() {
		assertThatThrownBy(() -> new OAuth2NaverConfig(
				CLIENT_ID,
				CLIENT_SECRET,
				REDIRECT_URI,
				AUTHORIZE_ENDPOINT,
				SCOPE,
				"invalid-url",
				OAuthHttpVerb.POST
		)).isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("accessTokenEndpoint");
	}

	@Test
	void shouldThrowExceptionForNullScope() {
		assertThatThrownBy(() -> new OAuth2NaverConfig(
				CLIENT_ID,
				CLIENT_SECRET,
				REDIRECT_URI,
				AUTHORIZE_ENDPOINT,
				null,
				TOKEN_ENDPOINT,
				OAuthHttpVerb.POST
		)).isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("scope");
	}

	@Test
	void shouldAllowEmptyScope() {
		OAuth2NaverConfig config = new OAuth2NaverConfig(
				CLIENT_ID,
				CLIENT_SECRET,
				REDIRECT_URI,
				AUTHORIZE_ENDPOINT,
				"",
				TOKEN_ENDPOINT,
				OAuthHttpVerb.POST
		);

		assertThat(config.getScope()).isEmpty();
	}

	@Test
	void shouldThrowExceptionForNullAccessTokenVerb() {
		assertThatThrownBy(() -> new OAuth2NaverConfig(
				CLIENT_ID,
				CLIENT_SECRET,
				REDIRECT_URI,
				AUTHORIZE_ENDPOINT,
				SCOPE,
				TOKEN_ENDPOINT,
				null
		)).isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("accessTokenVerb");
	}

	@Test
	void shouldSupportGetMethod() {
		OAuth2NaverConfig config = new OAuth2NaverConfig(
				CLIENT_ID,
				CLIENT_SECRET,
				REDIRECT_URI,
				AUTHORIZE_ENDPOINT,
				SCOPE,
				TOKEN_ENDPOINT,
				OAuthHttpVerb.GET
		);

		assertThat(config.getAccessTokenVerb()).isEqualTo(OAuthHttpVerb.GET);
	}

	@Test
	void shouldHaveCodeResponseType() {
		OAuth2NaverConfig config = new OAuth2NaverConfig(
				CLIENT_ID,
				CLIENT_SECRET,
				REDIRECT_URI,
				AUTHORIZE_ENDPOINT,
				SCOPE,
				TOKEN_ENDPOINT,
				OAuthHttpVerb.POST
		);

		assertThat(config.getResponseType()).isEqualTo(VerifierResponseType.CODE);
	}

	@Test
	void shouldHandleMultipleScopes() {
		String multipleScopes = "profile,email,birthday,gender";
		OAuth2NaverConfig config = new OAuth2NaverConfig(
				CLIENT_ID,
				CLIENT_SECRET,
				REDIRECT_URI,
				AUTHORIZE_ENDPOINT,
				multipleScopes,
				TOKEN_ENDPOINT,
				OAuthHttpVerb.POST
		);

		assertThat(config.getScope()).isEqualTo(multipleScopes);
	}

	@Test
	void shouldHandleHttpsEndpoints() {
		OAuth2NaverConfig config = new OAuth2NaverConfig(
				CLIENT_ID,
				CLIENT_SECRET,
				REDIRECT_URI,
				"https://nid.naver.com/oauth2.0/authorize",
				SCOPE,
				"https://nid.naver.com/oauth2.0/token",
				OAuthHttpVerb.POST
		);

		assertThat(config.getAuthorizeEndpoint()).startsWith("https://");
		assertThat(config.getAccessTokenUri()).startsWith("https://");
	}
}
