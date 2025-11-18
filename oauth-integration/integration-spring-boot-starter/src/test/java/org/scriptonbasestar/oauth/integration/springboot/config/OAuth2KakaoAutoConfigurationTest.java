package org.scriptonbasestar.oauth.integration.springboot.config;

import org.junit.jupiter.api.Test;
import org.scripton.oauth.connector.kakao.OAuth2KakaoAccessTokenFunction;
import org.scripton.oauth.connector.kakao.OAuth2KakaoAuthFunction;
import org.scripton.oauth.connector.kakao.OAuth2KakaoConfig;
import org.scriptonbasestar.oauth.client.nobi.token.TokenExtractor;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for OAuth2KakaoAutoConfiguration
 */
class OAuth2KakaoAutoConfigurationTest {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(
					OAuth2AutoConfiguration.class,
					OAuth2KakaoAutoConfiguration.class
			));

	@Test
	void shouldNotConfigureWhenClientIdNotProvided() {
		this.contextRunner
				.run(context -> {
					assertThat(context).doesNotHaveBean(OAuth2KakaoConfig.class);
					assertThat(context).doesNotHaveBean(OAuth2KakaoAuthFunction.class);
					assertThat(context).doesNotHaveBean(OAuth2KakaoAccessTokenFunction.class);
				});
	}

	@Test
	void shouldConfigureKakaoBeansWhenPropertiesProvided() {
		this.contextRunner
				.withPropertyValues(
						"oauth2.providers.kakao.client-id=test-kakao-client-id",
						"oauth2.providers.kakao.redirect-uri=http://localhost:8080/callback/kakao",
						"oauth2.providers.kakao.scope=profile_nickname,account_email"
				)
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasSingleBean(OAuth2KakaoConfig.class);
					assertThat(context).hasSingleBean(OAuth2KakaoAuthFunction.class);
					assertThat(context).hasSingleBean(OAuth2KakaoAccessTokenFunction.class);

					OAuth2KakaoConfig config = context.getBean(OAuth2KakaoConfig.class);
					assertThat(config.getClientId()).isEqualTo("test-kakao-client-id");
					assertThat(config.getRedirectUri()).isEqualTo("http://localhost:8080/callback/kakao");
					assertThat(config.getScope()).isEqualTo("profile_nickname,account_email");
				});
	}

	@Test
	void shouldConfigureKakaoWithOptionalClientSecret() {
		this.contextRunner
				.withPropertyValues(
						"oauth2.providers.kakao.client-id=test-kakao-client-id",
						"oauth2.providers.kakao.client-secret=test-kakao-secret",
						"oauth2.providers.kakao.redirect-uri=http://localhost:8080/callback/kakao"
				)
				.run(context -> {
					assertThat(context).hasNotFailed();
					OAuth2KakaoConfig config = context.getBean(OAuth2KakaoConfig.class);
					assertThat(config.getClientSecret()).isEqualTo("test-kakao-secret");
				});
	}

	@Test
	void shouldConfigureKakaoWithoutClientSecret() {
		// Kakao allows client_secret to be optional
		this.contextRunner
				.withPropertyValues(
						"oauth2.providers.kakao.client-id=test-kakao-client-id",
						"oauth2.providers.kakao.redirect-uri=http://localhost:8080/callback/kakao"
				)
				.run(context -> {
					assertThat(context).hasNotFailed();
					OAuth2KakaoConfig config = context.getBean(OAuth2KakaoConfig.class);
					assertThat(config.getClientSecret()).isNull();
				});
	}

	@Test
	void shouldNotConfigureWhenEnabledIsFalse() {
		this.contextRunner
				.withPropertyValues(
						"oauth2.providers.kakao.client-id=test-kakao-client-id",
						"oauth2.providers.kakao.redirect-uri=http://localhost:8080/callback/kakao",
						"oauth2.providers.kakao.enabled=false"
				)
				.run(context -> {
					assertThat(context).doesNotHaveBean(OAuth2KakaoConfig.class);
					assertThat(context).doesNotHaveBean(OAuth2KakaoAuthFunction.class);
					assertThat(context).doesNotHaveBean(OAuth2KakaoAccessTokenFunction.class);
				});
	}

	@Test
	void shouldConfigureAuthFunctionBean() {
		this.contextRunner
				.withPropertyValues(
						"oauth2.providers.kakao.client-id=test-kakao-client-id",
						"oauth2.providers.kakao.redirect-uri=http://localhost:8080/callback/kakao"
				)
				.run(context -> {
					assertThat(context).hasSingleBean(OAuth2KakaoAuthFunction.class);
					OAuth2KakaoAuthFunction authFunction = context.getBean(OAuth2KakaoAuthFunction.class);
					assertThat(authFunction).isNotNull();

					String authUrl = authFunction.getAuthUrl("test-state");
					assertThat(authUrl).contains("https://kauth.kakao.com/oauth/authorize");
					assertThat(authUrl).contains("client_id=test-kakao-client-id");
					assertThat(authUrl).contains("state=test-state");
				});
	}

	@Test
	void shouldDefaultToEnabledTrue() {
		this.contextRunner
				.withPropertyValues(
						"oauth2.providers.kakao.client-id=test-kakao-client-id",
						"oauth2.providers.kakao.redirect-uri=http://localhost:8080/callback/kakao"
						// enabled not set - should default to true
				)
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasSingleBean(OAuth2KakaoConfig.class);
				});
	}
}
