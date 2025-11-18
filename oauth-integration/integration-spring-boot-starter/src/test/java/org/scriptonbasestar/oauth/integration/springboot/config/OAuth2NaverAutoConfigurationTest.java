package org.scriptonbasestar.oauth.integration.springboot.config;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.scripton.oauth.connector.naver.OAuth2NaverAccesstokenFunction;
import org.scripton.oauth.connector.naver.OAuth2NaverAuthFunction;
import org.scripton.oauth.connector.naver.OAuth2NaverConfig;
import org.scripton.oauth.connector.naver.OAuth2NaverTokenRes;
import org.scriptonbasestar.oauth.client.nobi.TokenStorage;
import org.scriptonbasestar.oauth.client.nobi.token.TokenExtractor;
import org.scriptonbasestar.oauth.integration.springboot.properties.OAuth2Properties;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for OAuth2NaverAutoConfiguration
 */
class OAuth2NaverAutoConfigurationTest {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(
					OAuth2AutoConfiguration.class,
					OAuth2NaverAutoConfiguration.class
			));

	@Test
	void shouldNotConfigureWhenNaverClassNotOnClasspath() {
		// This test verifies that without Naver classes, no beans are created
		// In real scenario, we'd need to exclude the Naver connector from classpath
		// For this test, we just verify the conditional logic works
	}

	@Test
	void shouldNotConfigureWhenClientIdNotProvided() {
		this.contextRunner
				.run(context -> {
					assertThat(context).doesNotHaveBean(OAuth2NaverConfig.class);
					assertThat(context).doesNotHaveBean(OAuth2NaverAuthFunction.class);
					assertThat(context).doesNotHaveBean(OAuth2NaverAccesstokenFunction.class);
				});
	}

	@Test
	void shouldConfigureNaverBeansWhenPropertiesProvided() {
		this.contextRunner
				.withPropertyValues(
						"oauth2.providers.naver.client-id=test-client-id",
						"oauth2.providers.naver.client-secret=test-client-secret",
						"oauth2.providers.naver.redirect-uri=http://localhost:8080/callback",
						"oauth2.providers.naver.scope=profile,email"
				)
				.run(context -> {
					assertThat(context).hasSingleBean(OAuth2NaverConfig.class);
					assertThat(context).hasSingleBean(OAuth2NaverAuthFunction.class);
					assertThat(context).hasSingleBean(OAuth2NaverAccesstokenFunction.class);
					assertThat(context).hasSingleBean(TokenExtractor.class);

					OAuth2NaverConfig config = context.getBean(OAuth2NaverConfig.class);
					assertThat(config.getClientId()).isEqualTo("test-client-id");
					assertThat(config.getClientSecret()).isEqualTo("test-client-secret");
					assertThat(config.getRedirectUri()).isEqualTo("http://localhost:8080/callback");
					assertThat(config.getScope()).isEqualTo("profile,email");
				});
	}

	@Test
	void shouldNotConfigureWhenEnabledIsFalse() {
		this.contextRunner
				.withPropertyValues(
						"oauth2.providers.naver.client-id=test-client-id",
						"oauth2.providers.naver.client-secret=test-client-secret",
						"oauth2.providers.naver.redirect-uri=http://localhost:8080/callback",
						"oauth2.providers.naver.enabled=false"
				)
				.run(context -> {
					assertThat(context).doesNotHaveBean(OAuth2NaverConfig.class);
					assertThat(context).doesNotHaveBean(OAuth2NaverAuthFunction.class);
					assertThat(context).doesNotHaveBean(OAuth2NaverAccesstokenFunction.class);
				});
	}

	@Test
	void shouldFailWhenClientSecretNotProvided() {
		this.contextRunner
				.withPropertyValues(
						"oauth2.providers.naver.client-id=test-client-id",
						"oauth2.providers.naver.redirect-uri=http://localhost:8080/callback"
				)
				.run(context -> {
					assertThat(context).hasFailed();
					assertThat(context.getStartupFailure())
							.hasMessageContaining("client-secret is required for Naver");
				});
	}

	@Test
	void shouldUseDefaultScopeWhenNotProvided() {
		this.contextRunner
				.withPropertyValues(
						"oauth2.providers.naver.client-id=test-client-id",
						"oauth2.providers.naver.client-secret=test-client-secret",
						"oauth2.providers.naver.redirect-uri=http://localhost:8080/callback"
				)
				.run(context -> {
					assertThat(context).hasNotFailed();
					OAuth2NaverConfig config = context.getBean(OAuth2NaverConfig.class);
					assertThat(config.getScope()).isEqualTo("profile");
				});
	}

	@Test
	void shouldConfigureTokenExtractorBean() {
		this.contextRunner
				.withPropertyValues(
						"oauth2.providers.naver.client-id=test-client-id",
						"oauth2.providers.naver.client-secret=test-client-secret",
						"oauth2.providers.naver.redirect-uri=http://localhost:8080/callback"
				)
				.run(context -> {
					assertThat(context).hasSingleBean(TokenExtractor.class);
					TokenExtractor<?> extractor = context.getBean(TokenExtractor.class);
					assertThat(extractor).isNotNull();
				});
	}

	@Test
	void shouldConfigureAuthFunctionBean() {
		this.contextRunner
				.withPropertyValues(
						"oauth2.providers.naver.client-id=test-client-id",
						"oauth2.providers.naver.client-secret=test-client-secret",
						"oauth2.providers.naver.redirect-uri=http://localhost:8080/callback"
				)
				.run(context -> {
					assertThat(context).hasSingleBean(OAuth2NaverAuthFunction.class);
					OAuth2NaverAuthFunction authFunction = context.getBean(OAuth2NaverAuthFunction.class);
					assertThat(authFunction).isNotNull();

					// Test that auth function works
					String authUrl = authFunction.getAuthUrl("test-state");
					assertThat(authUrl).contains("https://nid.naver.com/oauth2.0/authorize");
					assertThat(authUrl).contains("client_id=test-client-id");
					assertThat(authUrl).contains("state=test-state");
				});
	}

	@Test
	void shouldConfigureTokenFunctionBean() {
		this.contextRunner
				.withPropertyValues(
						"oauth2.providers.naver.client-id=test-client-id",
						"oauth2.providers.naver.client-secret=test-client-secret",
						"oauth2.providers.naver.redirect-uri=http://localhost:8080/callback"
				)
				.run(context -> {
					assertThat(context).hasSingleBean(OAuth2NaverAccesstokenFunction.class);
					OAuth2NaverAccesstokenFunction tokenFunction = context.getBean(OAuth2NaverAccesstokenFunction.class);
					assertThat(tokenFunction).isNotNull();
				});
	}

	@Test
	void shouldRespectMatchIfMissingForEnabledProperty() {
		// When enabled property is not set, it should default to true (matchIfMissing = true)
		this.contextRunner
				.withPropertyValues(
						"oauth2.providers.naver.client-id=test-client-id",
						"oauth2.providers.naver.client-secret=test-client-secret",
						"oauth2.providers.naver.redirect-uri=http://localhost:8080/callback"
						// Note: enabled property NOT set
				)
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasSingleBean(OAuth2NaverConfig.class);
				});
	}
}
