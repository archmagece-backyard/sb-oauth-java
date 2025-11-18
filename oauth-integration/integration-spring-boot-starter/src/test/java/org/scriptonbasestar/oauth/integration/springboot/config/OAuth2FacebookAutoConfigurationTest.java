package org.scriptonbasestar.oauth.integration.springboot.config;

import org.junit.jupiter.api.Test;
import org.scripton.oauth.connector.facebook.OAuth2FacebookAccessTokenFunction;
import org.scripton.oauth.connector.facebook.OAuth2FacebookAuthFunction;
import org.scripton.oauth.connector.facebook.OAuth2FacebookConfig;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for OAuth2FacebookAutoConfiguration
 */
class OAuth2FacebookAutoConfigurationTest {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(
					OAuth2AutoConfiguration.class,
					OAuth2FacebookAutoConfiguration.class
			));

	@Test
	void shouldNotConfigureWhenClientIdNotProvided() {
		this.contextRunner
				.run(context -> {
					assertThat(context).doesNotHaveBean(OAuth2FacebookConfig.class);
					assertThat(context).doesNotHaveBean(OAuth2FacebookAuthFunction.class);
					assertThat(context).doesNotHaveBean(OAuth2FacebookAccessTokenFunction.class);
				});
	}

	@Test
	void shouldConfigureFacebookBeansWhenPropertiesProvided() {
		this.contextRunner
				.withPropertyValues(
						"oauth2.providers.facebook.client-id=test-facebook-app-id",
						"oauth2.providers.facebook.client-secret=test-facebook-secret",
						"oauth2.providers.facebook.redirect-uri=http://localhost:8080/callback/facebook",
						"oauth2.providers.facebook.scope=email,public_profile"
				)
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasSingleBean(OAuth2FacebookConfig.class);
					assertThat(context).hasSingleBean(OAuth2FacebookAuthFunction.class);
					assertThat(context).hasSingleBean(OAuth2FacebookAccessTokenFunction.class);

					OAuth2FacebookConfig config = context.getBean(OAuth2FacebookConfig.class);
					assertThat(config.getClientId()).isEqualTo("test-facebook-app-id");
					assertThat(config.getClientSecret()).isEqualTo("test-facebook-secret");
					assertThat(config.getRedirectUri()).isEqualTo("http://localhost:8080/callback/facebook");
					assertThat(config.getScope()).isEqualTo("email,public_profile");
				});
	}

	@Test
	void shouldFailWhenClientSecretNotProvided() {
		this.contextRunner
				.withPropertyValues(
						"oauth2.providers.facebook.client-id=test-facebook-app-id",
						"oauth2.providers.facebook.redirect-uri=http://localhost:8080/callback/facebook"
				)
				.run(context -> {
					assertThat(context).hasFailed();
					assertThat(context.getStartupFailure())
							.hasMessageContaining("client-secret is required for Facebook");
				});
	}

	@Test
	void shouldUseDefaultScopeWhenNotProvided() {
		this.contextRunner
				.withPropertyValues(
						"oauth2.providers.facebook.client-id=test-facebook-app-id",
						"oauth2.providers.facebook.client-secret=test-facebook-secret",
						"oauth2.providers.facebook.redirect-uri=http://localhost:8080/callback/facebook"
				)
				.run(context -> {
					assertThat(context).hasNotFailed();
					OAuth2FacebookConfig config = context.getBean(OAuth2FacebookConfig.class);
					assertThat(config.getScope()).isEqualTo("email,public_profile");
				});
	}

	@Test
	void shouldNotConfigureWhenEnabledIsFalse() {
		this.contextRunner
				.withPropertyValues(
						"oauth2.providers.facebook.client-id=test-facebook-app-id",
						"oauth2.providers.facebook.client-secret=test-facebook-secret",
						"oauth2.providers.facebook.redirect-uri=http://localhost:8080/callback/facebook",
						"oauth2.providers.facebook.enabled=false"
				)
				.run(context -> {
					assertThat(context).doesNotHaveBean(OAuth2FacebookConfig.class);
					assertThat(context).doesNotHaveBean(OAuth2FacebookAuthFunction.class);
					assertThat(context).doesNotHaveBean(OAuth2FacebookAccessTokenFunction.class);
				});
	}

	@Test
	void shouldConfigureAuthFunctionBean() {
		this.contextRunner
				.withPropertyValues(
						"oauth2.providers.facebook.client-id=test-facebook-app-id",
						"oauth2.providers.facebook.client-secret=test-facebook-secret",
						"oauth2.providers.facebook.redirect-uri=http://localhost:8080/callback/facebook"
				)
				.run(context -> {
					assertThat(context).hasSingleBean(OAuth2FacebookAuthFunction.class);
					OAuth2FacebookAuthFunction authFunction = context.getBean(OAuth2FacebookAuthFunction.class);
					assertThat(authFunction).isNotNull();

					String authUrl = authFunction.getAuthUrl("test-state");
					assertThat(authUrl).contains("https://www.facebook.com/v");
					assertThat(authUrl).contains("dialog/oauth");
					assertThat(authUrl).contains("client_id=test-facebook-app-id");
					assertThat(authUrl).contains("state=test-state");
				});
	}

	@Test
	void shouldDefaultToEnabledTrue() {
		this.contextRunner
				.withPropertyValues(
						"oauth2.providers.facebook.client-id=test-facebook-app-id",
						"oauth2.providers.facebook.client-secret=test-facebook-secret",
						"oauth2.providers.facebook.redirect-uri=http://localhost:8080/callback/facebook"
						// enabled not set - should default to true
				)
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasSingleBean(OAuth2FacebookConfig.class);
				});
	}
}
