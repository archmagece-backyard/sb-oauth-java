package org.scriptonbasestar.oauth.client.exception;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for OAuth2 exception hierarchy
 */
class OAuth2ExceptionHierarchyTest {

	@Test
	void shouldExtendOAuthException() {
		OAuth2Exception exception = new OAuth2Exception("Test");

		assertThat(exception).isInstanceOf(OAuthException.class);
		assertThat(exception).isInstanceOf(RuntimeException.class);
	}

	@Test
	void configurationExceptionShouldExtendOAuth2Exception() {
		OAuth2ConfigurationException exception = new OAuth2ConfigurationException("Test");

		assertThat(exception).isInstanceOf(OAuth2Exception.class);
		assertThat(exception).isInstanceOf(OAuth2Exception.class);
	}

	@Test
	void invalidClientShouldExtendConfigurationException() {
		InvalidClientException exception = new InvalidClientException("Test");

		assertThat(exception).isInstanceOf(OAuth2ConfigurationException.class);
		assertThat(exception.getErrorCode()).isEqualTo("invalid_client");
	}

	@Test
	void invalidRedirectUriShouldExtendConfigurationException() {
		InvalidRedirectUriException exception = new InvalidRedirectUriException(
			"Invalid URI",
			"https://malicious.com"
		);

		assertThat(exception).isInstanceOf(OAuth2ConfigurationException.class);
		assertThat(exception.getErrorCode()).isEqualTo("invalid_redirect_uri");
		assertThat(exception.getRedirectUri()).isEqualTo("https://malicious.com");
		assertThat(exception.getContext()).containsEntry("redirect_uri", "https://malicious.com");
	}

	@Test
	void missingConfigurationShouldExtendConfigurationException() {
		MissingConfigurationException exception = new MissingConfigurationException(
			"Missing property",
			"client_id"
		);

		assertThat(exception).isInstanceOf(OAuth2ConfigurationException.class);
		assertThat(exception.getErrorCode()).isEqualTo("missing_configuration");
		assertThat(exception.getMissingProperty()).isEqualTo("client_id");
		assertThat(exception.getContext()).containsEntry("missing_property", "client_id");
	}

	@Test
	void authorizationExceptionShouldExtendOAuth2Exception() {
		OAuth2AuthorizationException exception = new OAuth2AuthorizationException("Test");

		assertThat(exception).isInstanceOf(OAuth2Exception.class);
	}

	@Test
	void invalidGrantShouldExtendAuthorizationException() {
		InvalidGrantException exception = new InvalidGrantException("Test", "google");

		assertThat(exception).isInstanceOf(OAuth2AuthorizationException.class);
		assertThat(exception.getErrorCode()).isEqualTo("invalid_grant");
		assertThat(exception.getProvider()).isEqualTo("google");
	}

	@Test
	void invalidStateShouldExtendAuthorizationException() {
		InvalidStateException exception = new InvalidStateException(
			"State mismatch",
			"expected-state",
			"actual-state"
		);

		assertThat(exception).isInstanceOf(OAuth2AuthorizationException.class);
		assertThat(exception.getErrorCode()).isEqualTo("invalid_state");
		assertThat(exception.getExpectedState()).isEqualTo("expected-state");
		assertThat(exception.getActualState()).isEqualTo("actual-state");
		assertThat(exception.getContext()).containsEntry("expected_state", "expected-state");
		assertThat(exception.getContext()).containsEntry("actual_state", "actual-state");
	}

	@Test
	void accessDeniedShouldExtendAuthorizationException() {
		AccessDeniedException exception = new AccessDeniedException("User denied", "kakao");

		assertThat(exception).isInstanceOf(OAuth2AuthorizationException.class);
		assertThat(exception.getErrorCode()).isEqualTo("access_denied");
		assertThat(exception.getProvider()).isEqualTo("kakao");
	}

	@Test
	void stateExpiredShouldExtendAuthorizationException() {
		Duration maxAge = Duration.ofMinutes(10);
		Duration actualAge = Duration.ofMinutes(15);
		StateExpiredException exception = new StateExpiredException(
			"State expired",
			maxAge,
			actualAge
		);

		assertThat(exception).isInstanceOf(OAuth2AuthorizationException.class);
		assertThat(exception.getErrorCode()).isEqualTo("state_expired");
		assertThat(exception.getMaxAge()).isEqualTo(maxAge);
		assertThat(exception.getActualAge()).isEqualTo(actualAge);
		assertThat(exception.getContext()).containsEntry("max_age_seconds", 600L);
		assertThat(exception.getContext()).containsEntry("actual_age_seconds", 900L);
	}

	@Test
	void tokenExceptionShouldExtendOAuth2Exception() {
		OAuth2TokenException exception = new OAuth2TokenException("Test");

		assertThat(exception).isInstanceOf(OAuth2Exception.class);
	}

	@Test
	void tokenExpiredShouldExtendTokenException() {
		Instant expirationTime = Instant.now().minusSeconds(3600);
		TokenExpiredException exception = new TokenExpiredException(
			"Token expired",
			expirationTime,
			"naver"
		);

		assertThat(exception).isInstanceOf(OAuth2TokenException.class);
		assertThat(exception.getErrorCode()).isEqualTo("token_expired");
		assertThat(exception.getExpirationTime()).isEqualTo(expirationTime);
		assertThat(exception.getProvider()).isEqualTo("naver");
	}

	@Test
	void tokenRevokedShouldExtendTokenException() {
		TokenRevokedException exception = new TokenRevokedException("Token revoked", "google");

		assertThat(exception).isInstanceOf(OAuth2TokenException.class);
		assertThat(exception.getErrorCode()).isEqualTo("token_revoked");
		assertThat(exception.getProvider()).isEqualTo("google");
	}

	@Test
	void invalidTokenShouldExtendTokenException() {
		InvalidTokenException exception = new InvalidTokenException("Invalid token", "facebook");

		assertThat(exception).isInstanceOf(OAuth2TokenException.class);
		assertThat(exception.getErrorCode()).isEqualTo("invalid_token");
		assertThat(exception.getProvider()).isEqualTo("facebook");
	}

	@Test
	void tokenRefreshShouldExtendTokenException() {
		Throwable cause = new RuntimeException("Network error");
		TokenRefreshException exception = new TokenRefreshException(
			"Refresh failed",
			"kakao",
			cause
		);

		assertThat(exception).isInstanceOf(OAuth2TokenException.class);
		assertThat(exception.getErrorCode()).isEqualTo("token_refresh_failed");
		assertThat(exception.getProvider()).isEqualTo("kakao");
		assertThat(exception.getCause()).isEqualTo(cause);
	}

	@Test
	void networkExceptionShouldExtendOAuth2Exception() {
		OAuth2NetworkException exception = new OAuth2NetworkException("Test");

		assertThat(exception).isInstanceOf(OAuth2Exception.class);
	}

	@Test
	void connectionTimeoutShouldExtendNetworkException() {
		Duration timeout = Duration.ofSeconds(30);
		ConnectionTimeoutException exception = new ConnectionTimeoutException(
			"Connection timeout",
			timeout,
			"google",
			null
		);

		assertThat(exception).isInstanceOf(OAuth2NetworkException.class);
		assertThat(exception.getErrorCode()).isEqualTo("connection_timeout");
		assertThat(exception.getTimeout()).isEqualTo(timeout);
		assertThat(exception.getProvider()).isEqualTo("google");
		assertThat(exception.getContext()).containsEntry("timeout_seconds", 30L);
	}

	@Test
	void networkFailureShouldExtendNetworkException() {
		Throwable cause = new java.io.IOException("Connection refused");
		NetworkFailureException exception = new NetworkFailureException(
			"Network failure",
			"https://oauth.example.com/token",
			500,
			"naver",
			cause
		);

		assertThat(exception).isInstanceOf(OAuth2NetworkException.class);
		assertThat(exception.getErrorCode()).isEqualTo("network_failure");
		assertThat(exception.getEndpoint()).isEqualTo("https://oauth.example.com/token");
		assertThat(exception.getStatusCode()).isEqualTo(500);
		assertThat(exception.getProvider()).isEqualTo("naver");
		assertThat(exception.getCause()).isEqualTo(cause);
		assertThat(exception.getContext()).containsEntry("endpoint", "https://oauth.example.com/token");
		assertThat(exception.getContext()).containsEntry("status_code", 500);
	}

	@Test
	void shouldHandleInheritanceChain() {
		InvalidClientException exception = new InvalidClientException("Test", "google");

		// Should be instance of all parent types
		assertThat(exception).isInstanceOf(InvalidClientException.class);
		assertThat(exception).isInstanceOf(OAuth2ConfigurationException.class);
		assertThat(exception).isInstanceOf(OAuth2Exception.class);
		assertThat(exception).isInstanceOf(OAuthException.class);
		assertThat(exception).isInstanceOf(RuntimeException.class);
		assertThat(exception).isInstanceOf(Exception.class);
	}

	@Test
	void shouldCatchByParentType() {
		try {
			throw new InvalidGrantException("Test", "naver");
		} catch (OAuth2AuthorizationException e) {
			assertThat(e).isInstanceOf(InvalidGrantException.class);
			assertThat(e.getErrorCode()).isEqualTo("invalid_grant");
		}
	}

	@Test
	void shouldCatchByGrandParentType() {
		try {
			throw new TokenExpiredException("Test");
		} catch (OAuth2Exception e) {
			assertThat(e).isInstanceOf(TokenExpiredException.class);
			assertThat(e.getErrorCode()).isEqualTo("token_expired");
		}
	}
}
