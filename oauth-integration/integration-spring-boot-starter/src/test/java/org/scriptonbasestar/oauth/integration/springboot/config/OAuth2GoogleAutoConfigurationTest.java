package org.scriptonbasestar.oauth.integration.springboot.config;

import org.junit.jupiter.api.Test;
import org.scripton.oauth.connector.google.OAuth2GoogleAccessTokenFunction;
import org.scripton.oauth.connector.google.OAuth2GoogleAuthFunction;
import org.scripton.oauth.connector.google.OAuth2GoogleConfig;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for OAuth2GoogleAutoConfiguration
 */
class OAuth2GoogleAutoConfigurationTest {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(
					OAuth2AutoConfiguration.class,
					OAuth2GoogleAutoConfiguration.class
			));

	@Test
	void shouldNotConfigureWhenClientIdNotProvided() {
		this.contextRunner
				.run(context -> {
					assertThat(context).doesNotHaveBean(OAuth2GoogleConfig.class);
					assertThat(context).doesNotHaveBean(OAuth2GoogleAuthFunction.class);
					assertThat(context).doesNotHaveBean(OAuth2GoogleAccessTokenFunction.class);
				});
	}

	@Test
	void shouldConfigureGoogleBeansWhenPropertiesProvided() {
		this.contextRunner
				.withPropertyValues(
						"oauth2.providers.google.client-id=test-google-client-id",
						"oauth2.providers.google.client-secret=test-google-secret",
						"oauth2.providers.google.redirect-uri=http://localhost:8080/callback/google",
						"oauth2.providers.google.scope=openid,profile,email"
				)
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasSingleBean(OAuth2GoogleConfig.class);
					assertThat(context).hasSingleBean(OAuth2GoogleAuthFunction.class);
					assertThat(context).hasSingleBean(OAuth2GoogleAccessTokenFunction.class);

					OAuth2GoogleConfig config = context.getBean(OAuth2GoogleConfig.class);
					assertThat(config.getClientId()).isEqualTo("test-google-client-id");
					assertThat(config.getClientSecret()).isEqualTo("test-google-secret");
					assertThat(config.getRedirectUri()).isEqualTo("http://localhost:8080/callback/google");
					assertThat(config.getScope()).isEqualTo("openid,profile,email");
				});
	}

	@Test
	void shouldFailWhenClientSecretNotProvided() {
		this.contextRunner
				.withPropertyValues(
						"oauth2.providers.google.client-id=test-google-client-id",
						"oauth2.providers.google.redirect-uri=http://localhost:8080/callback/google"
				)
				.run(context -> {
					assertThat(context).hasFailed();
					assertThat(context.getStartupFailure())
							.hasMessageContaining("client-secret is required for Google");
				});
	}

	@Test
	void shouldUseDefaultScopeWhenNotProvided() {
		this.contextRunner
				.withPropertyValues(
						"oauth2.providers.google.client-id=test-google-client-id",
						"oauth2.providers.google.client-secret=test-google-secret",
						"oauth2.providers.google.redirect-uri=http://localhost:8080/callback/google"
				)
				.run(context -> {
					assertThat(context).hasNotFailed();
					OAuth2GoogleConfig config = context.getBean(OAuth2GoogleConfig.class);
					assertThat(config.getScope()).isEqualTo("openid profile email");
				});
	}

	@Test
	void shouldNotConfigureWhenEnabledIsFalse() {
		this.contextRunner
				.withPropertyValues(
						"oauth2.providers.google.client-id=test-google-client-id",
						"oauth2.providers.google.client-secret=test-google-secret",
						"oauth2.providers.google.redirect-uri=http://localhost:8080/callback/google",
						"oauth2.providers.google.enabled=false"
				)
				.run(context -> {
					assertThat(context).doesNotHaveBean(OAuth2GoogleConfig.class);
					assertThat(context).doesNotHaveBean(OAuth2GoogleAuthFunction.class);
					assertThat(context).doesNotHaveBean(OAuth2GoogleAccessTokenFunction.class);
				});
	}

	@Test
	void shouldConfigureAuthFunctionBean() {
		this.contextRunner
				.withPropertyValues(
						"oauth2.providers.google.client-id=test-google-client-id",
						"oauth2.providers.google.client-secret=test-google-secret",
						"oauth2.providers.google.redirect-uri=http://localhost:8080/callback/google"
				)
				.run(context -> {
					assertThat(context).hasSingleBean(OAuth2GoogleAuthFunction.class);
					OAuth2GoogleAuthFunction authFunction = context.getBean(OAuth2GoogleAuthFunction.class);
					assertThat(authFunction).isNotNull();

					String authUrl = authFunction.getAuthUrl("test-state");
					assertThat(authUrl).contains("https://accounts.google.com/o/oauth2/v2/auth");
					assertThat(authUrl).contains("client_id=test-google-client-id");
					assertThat(authUrl).contains("state=test-state");
				});
	}
}
