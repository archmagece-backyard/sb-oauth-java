package org.scripton.oauth.connector.naver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.scriptonbasestar.oauth.client.model.State;
import org.scriptonbasestar.oauth.client.model.Token;
import org.scriptonbasestar.oauth.client.model.Verifier;
import org.scriptonbasestar.oauth.client.nobi.TokenStorage;
import org.scriptonbasestar.oauth.client.nobi.token.TokenExtractor;
import org.scriptonbasestar.oauth.client.o20.type.AccessTokenType;
import org.scriptonbasestar.oauth.client.type.OAuthHttpVerb;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("OAuth2NaverAccesstokenFunction 테스트")
class OAuth2NaverAccesstokenFunctionTest {

	@Mock
	private TokenExtractor<OAuth2NaverTokenRes> tokenExtractor;

	@Mock
	private TokenStorage tokenStorage;

	private OAuth2NaverConfig config;
	private OAuth2NaverAccesstokenFunction function;

	@BeforeEach
	void setUp() {
		config = new OAuth2NaverConfig(
				"test-client-id",
				"test-client-secret",
				"http://localhost:8080/callback",
				"https://nid.naver.com/oauth2.0/authorize",
				"profile,email",
				"https://nid.naver.com/oauth2.0/token",
				OAuthHttpVerb.POST
		);

		function = new OAuth2NaverAccesstokenFunction(config, tokenExtractor, tokenStorage);
	}

	@Test
	@DisplayName("issue: 정상적인 verifier와 state로 토큰 발급 성공")
	void issue_WithValidVerifierAndState_ReturnsToken() {
		// Given
		Verifier verifier = new Verifier("test-authorization-code");
		State state = new State("test-state-value");

		OAuth2NaverTokenRes expectedToken = new OAuth2NaverTokenRes(
			"test-access-token",
			AccessTokenType.BEARER,
			"test-refresh-token",
			3600L,
			null,
			null,
			null
		);

		when(tokenExtractor.extract(anyString())).thenReturn(expectedToken);

		// When
		OAuth2NaverTokenRes result = function.issue(verifier, state);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.accessToken()).isEqualTo("test-access-token");
		assertThat(result.tokenType()).isEqualTo(AccessTokenType.BEARER);
		assertThat(result.refreshToken()).isEqualTo("test-refresh-token");
		assertThat(result.expiresIn()).isEqualTo(3600L);

		verify(tokenExtractor).extract(anyString());
	}

	@Test
	@DisplayName("issue: null verifier로 호출 시 예외 발생")
	void issue_WithNullVerifier_ThrowsException() {
		// Given
		State state = new State("test-state");

		// When & Then
		assertThatThrownBy(() -> function.issue(null, state))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("verifier must not null");
	}

	@Test
	@DisplayName("issue: null state로 호출 시 예외 발생")
	void issue_WithNullState_ThrowsException() {
		// Given
		Verifier verifier = new Verifier("test-code");

		// When & Then
		assertThatThrownBy(() -> function.issue(verifier, null))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("state must not null");
	}

	@Test
	@DisplayName("refresh: 정상적인 refresh token으로 토큰 갱신 성공")
	void refresh_WithValidRefreshToken_ReturnsNewToken() {
		// Given
		Token refreshToken = new Token("test-refresh-token");

		OAuth2NaverTokenRes expectedToken = new OAuth2NaverTokenRes(
			"new-access-token",
			AccessTokenType.BEARER,
			"new-refresh-token",
			3600L,
			null,
			null,
			null
		);

		when(tokenExtractor.extract(anyString())).thenReturn(expectedToken);

		// When
		OAuth2NaverTokenRes result = function.refresh(refreshToken);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.accessToken()).isEqualTo("new-access-token");
		assertThat(result.refreshToken()).isEqualTo("new-refresh-token");

		verify(tokenExtractor).extract(anyString());
	}

	@Test
	@DisplayName("revoke: 정상적인 access token으로 토큰 폐기 성공")
	void revoke_WithValidAccessToken_ReturnsSuccessResponse() {
		// Given
		Token accessToken = new Token("test-access-token");

		OAuth2NaverTokenRes expectedResponse = new OAuth2NaverTokenRes(
			null,
			null,
			null,
			null,
			"success",
			null,
			null
		);

		when(tokenExtractor.extract(anyString())).thenReturn(expectedResponse);

		// When
		OAuth2NaverTokenRes result = function.revoke(accessToken);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.result()).isEqualTo("success");

		verify(tokenExtractor).extract(anyString());
	}

	@Test
	@DisplayName("Config: 설정값이 올바르게 주입되는지 확인")
	void config_ShouldBeInjectedCorrectly() {
		// Then
		assertThat(config.getClientId()).isEqualTo("test-client-id");
		assertThat(config.getClientSecret()).isEqualTo("test-client-secret");
		assertThat(config.getRedirectUri()).isEqualTo("http://localhost:8080/callback");
		assertThat(config.getAuthorizeEndpoint()).isEqualTo("https://nid.naver.com/oauth2.0/authorize");
		assertThat(config.getScope()).isEqualTo("profile,email");
		assertThat(config.getAccessTokenUri()).isEqualTo("https://nid.naver.com/oauth2.0/token");
		assertThat(config.getAccessTokenVerb()).isEqualTo(OAuthHttpVerb.POST);
	}
}
