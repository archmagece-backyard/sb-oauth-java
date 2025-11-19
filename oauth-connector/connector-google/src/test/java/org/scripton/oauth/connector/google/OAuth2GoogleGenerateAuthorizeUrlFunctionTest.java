package org.scripton.oauth.connector.google;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.scriptonbasestar.oauth.client.model.State;
import org.scriptonbasestar.oauth.client.type.OAuthHttpVerb;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for OAuth2GoogleGenerateAuthorizeUrlFunction
 */
class OAuth2GoogleGenerateAuthorizeUrlFunctionTest {

	private static final String CLIENT_ID = "test-client-id";
	private static final String CLIENT_SECRET = "test-client-secret";
	private static final String REDIRECT_URI = "https://example.com/callback";
	private static final String AUTHORIZE_ENDPOINT = "https://accounts.google.com/o/oauth2/v2/auth";
	private static final String SCOPE = "openid profile email";
	private static final String TOKEN_ENDPOINT = "https://oauth2.googleapis.com/token";

	private OAuth2GoogleConfig config;
	private OAuth2GoogleGenerateAuthorizeUrlFunction function;

	@BeforeEach
	void setUp() {
		config = new OAuth2GoogleConfig(
				CLIENT_ID,
				CLIENT_SECRET,
				REDIRECT_URI,
				AUTHORIZE_ENDPOINT,
				SCOPE,
				TOKEN_ENDPOINT,
				OAuthHttpVerb.POST
		);
		function = new OAuth2GoogleGenerateAuthorizeUrlFunction(config);
	}

	@Test
	void shouldGenerateAuthorizeUrl() {
		State state = new State("test-state-value");

		String url = function.generate(state);

		assertThat(url).isNotNull();
		assertThat(url).startsWith(AUTHORIZE_ENDPOINT);
		assertThat(url).contains("client_id=" + CLIENT_ID);
		assertThat(url).contains("state=test-state-value");
		assertThat(url).contains("response_type=code");
	}

	@Test
	void shouldIncludeScope() throws UnsupportedEncodingException {
		State state = new State("test-state");

		String url = function.generate(state);

		assertThat(url).contains("scope=");
		String decoded = URLDecoder.decode(url, "UTF-8");
		assertThat(decoded).contains("openid");
		assertThat(decoded).contains("profile");
		assertThat(decoded).contains("email");
	}

	@Test
	void shouldIncludeRedirectUri() throws UnsupportedEncodingException {
		State state = new State("test-state");

		String url = function.generate(state);

		assertThat(url).contains("redirect_uri=");
		String decoded = URLDecoder.decode(url, "UTF-8");
		assertThat(decoded).contains(REDIRECT_URI);
	}

	@Test
	void shouldThrowExceptionForNullState() {
		assertThatThrownBy(() -> function.generate(null))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("state");
	}

	@Test
	void shouldHandleSpecialCharactersInState() {
		State state = new State("state-with-special!@#$%^&*()");

		String url = function.generate(state);

		assertThat(url).isNotNull();
		assertThat(url).contains("state=");
	}

	@Test
	void shouldHandleUnicodeInState() {
		State state = new State("상태값_한글_🔒");

		String url = function.generate(state);

		assertThat(url).isNotNull();
		assertThat(url).contains("state=");
	}

	@Test
	void shouldIncludeAllRequiredParameters() {
		State state = new State("test-state");

		String url = function.generate(state);

		// Required parameters for Google OAuth
		assertThat(url).contains("client_id=");
		assertThat(url).contains("redirect_uri=");
		assertThat(url).contains("response_type=");
		assertThat(url).contains("scope=");
		assertThat(url).contains("state=");
	}

	@Test
	void shouldUseCodeAsResponseType() {
		State state = new State("test-state");

		String url = function.generate(state);

		assertThat(url).contains("response_type=code");
	}

	@Test
	void shouldHandleOIDCScopes() throws UnsupportedEncodingException {
		State state = new State("test-state");

		String url = function.generate(state);

		String decoded = URLDecoder.decode(url, "UTF-8");
		// Google OIDC scopes
		assertThat(decoded).contains("openid");
	}

	@Test
	void shouldGenerateConsistentUrls() {
		State state = new State("consistent-state");

		String url1 = function.generate(state);
		String url2 = function.generate(state);

		assertThat(url1).isEqualTo(url2);
	}

	@Test
	void shouldHandleLongStateValue() {
		String longStateValue = "a".repeat(100);
		State state = new State(longStateValue);

		String url = function.generate(state);

		assertThat(url).isNotNull();
		assertThat(url).contains("state=");
	}
}
