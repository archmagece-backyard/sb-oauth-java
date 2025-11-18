package org.scripton.oauth.connector.facebook;

import org.junit.jupiter.api.Test;
import org.scriptonbasestar.oauth.client.o20.type.VerifierResponseType;
import org.scriptonbasestar.oauth.client.type.OAuthHttpVerb;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for OAuth2FacebookConfig
 */
class OAuth2FacebookConfigTest {

	private static final String CLIENT_ID = "test-client-id";
	private static final String CLIENT_SECRET = "test-client-secret";
	private static final String REDIRECT_URI = "https://example.com/callback";
	private static final String AUTHORIZE_ENDPOINT = "https://www.facebook.com/v3.2/dialog/oauth";
	private static final String SCOPE = "public_profile,email";
	private static final String TOKEN_ENDPOINT = "https://graph.facebook.com/v3.2/oauth/access_token";

	@Test
	void shouldCreateConfigWithAllFields() {
		OAuth2FacebookConfig config = new OAuth2FacebookConfig(
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
		assertThat(config.getAccessTokenEndpoint()).isEqualTo(TOKEN_ENDPOINT);
		assertThat(config.getAccessTokenVerb()).isEqualTo(OAuthHttpVerb.POST);
		assertThat(config.getResponseType()).isEqualTo(VerifierResponseType.CODE);
	}

	@Test
	void shouldThrowExceptionForInvalidAuthorizeEndpoint() {
		assertThatThrownBy(() -> new OAuth2FacebookConfig(
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
		assertThatThrownBy(() -> new OAuth2FacebookConfig(
				CLIENT_ID,
				CLIENT_SECRET,
				REDIRECT_URI,
				null,
				SCOPE,
				TOKEN_ENDPOINT,
				OAuthHttpVerb.POST
		)).isInstanceOf(IllegalArgumentException.class)
		  .hasMessageContaining("authorizeEndpoint");
	}

	@Test
	void shouldThrowExceptionForInvalidAccessTokenEndpoint() {
		assertThatThrownBy(() -> new OAuth2FacebookConfig(
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
		assertThatThrownBy(() -> new OAuth2FacebookConfig(
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
	void shouldThrowExceptionForNullAccessTokenVerb() {
		assertThatThrownBy(() -> new OAuth2FacebookConfig(
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
	void shouldAllowEmptyScope() {
		OAuth2FacebookConfig config = new OAuth2FacebookConfig(
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
	void shouldSupportMultipleScopes() {
		String multipleScopes = "public_profile,email,user_friends,user_photos";
		OAuth2FacebookConfig config = new OAuth2FacebookConfig(
				CLIENT_ID,
				CLIENT_SECRET,
				REDIRECT_URI,
				AUTHORIZE_ENDPOINT,
				multipleScopes,
				TOKEN_ENDPOINT,
				OAuthHttpVerb.POST
		);

		assertThat(config.getScope()).isEqualTo(multipleScopes);
		assertThat(config.getScope()).contains("public_profile");
		assertThat(config.getScope()).contains("email");
		assertThat(config.getScope()).contains("user_friends");
	}

	@Test
	void shouldSupportGetMethod() {
		OAuth2FacebookConfig config = new OAuth2FacebookConfig(
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
	void shouldAlwaysUseCodeResponseType() {
		OAuth2FacebookConfig config = new OAuth2FacebookConfig(
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
	void shouldHandleGraphAPIVersionedEndpoints() {
		String v12AuthEndpoint = "https://www.facebook.com/v12.0/dialog/oauth";
		String v12TokenEndpoint = "https://graph.facebook.com/v12.0/oauth/access_token";

		OAuth2FacebookConfig config = new OAuth2FacebookConfig(
				CLIENT_ID,
				CLIENT_SECRET,
				REDIRECT_URI,
				v12AuthEndpoint,
				SCOPE,
				v12TokenEndpoint,
				OAuthHttpVerb.POST
		);

		assertThat(config.getAuthorizeEndpoint()).contains("v12.0");
		assertThat(config.getAccessTokenEndpoint()).contains("v12.0");
	}

	@Test
	void shouldHandleToString() {
		OAuth2FacebookConfig config = new OAuth2FacebookConfig(
				CLIENT_ID,
				CLIENT_SECRET,
				REDIRECT_URI,
				AUTHORIZE_ENDPOINT,
				SCOPE,
				TOKEN_ENDPOINT,
				OAuthHttpVerb.POST
		);

		String toString = config.toString();
		assertThat(toString).isNotNull();
	}
}
