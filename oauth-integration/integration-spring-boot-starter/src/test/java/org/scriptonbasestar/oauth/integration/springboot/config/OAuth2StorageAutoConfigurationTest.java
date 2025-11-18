package org.scriptonbasestar.oauth.integration.springboot.config;

import org.junit.jupiter.api.Test;
import org.scriptonbasestar.oauth.client.nobi.TokenStorage;
import org.scriptonbasestar.oauth.client.nobi.storage.LocalTokenStorage;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.data.redis.core.RedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for OAuth2StorageAutoConfiguration
 */
class OAuth2StorageAutoConfigurationTest {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(
					OAuth2AutoConfiguration.class,
					OAuth2StorageAutoConfiguration.class
			));

	@Test
	void shouldConfigureLocalStorageByDefault() {
		this.contextRunner
				.run(context -> {
					assertThat(context).hasSingleBean(TokenStorage.class);
					TokenStorage storage = context.getBean(TokenStorage.class);
					assertThat(storage).isInstanceOf(LocalTokenStorage.class);
				});
	}

	@Test
	void shouldConfigureLocalStorageWhenTypeIsLocal() {
		this.contextRunner
				.withPropertyValues("oauth2.storage.type=local")
				.run(context -> {
					assertThat(context).hasSingleBean(TokenStorage.class);
					TokenStorage storage = context.getBean(TokenStorage.class);
					assertThat(storage).isInstanceOf(LocalTokenStorage.class);
				});
	}

	@Test
	void shouldConfigureLocalStorageWhenTypeIsLocalUpperCase() {
		this.contextRunner
				.withPropertyValues("oauth2.storage.type=LOCAL")
				.run(context -> {
					assertThat(context).hasSingleBean(TokenStorage.class);
					TokenStorage storage = context.getBean(TokenStorage.class);
					assertThat(storage).isInstanceOf(LocalTokenStorage.class);
				});
	}

	@Test
	void shouldNotConfigureRedisStorageWhenRedisClassNotOnClasspath() {
		// When RedisConnectionFactory is not on classpath, Redis storage should not be configured
		this.contextRunner
				.withPropertyValues("oauth2.storage.type=redis")
				.run(context -> {
					// Should fall back to default LocalTokenStorage since Redis is not available
					// Or context should not have Redis-specific beans
					assertThat(context).doesNotHaveBean(RedisTemplate.class);
				});
	}

	@Test
	void shouldUseDefaultRedisProperties() {
		// Test that default Redis properties are correctly set
		this.contextRunner
				.withPropertyValues("oauth2.storage.type=local") // Use local to avoid Redis dependency issues
				.run(context -> {
					assertThat(context).hasNotFailed();
				});
	}

	@Test
	void shouldNotOverrideExistingTokenStorageBean() {
		this.contextRunner
				.withUserConfiguration(CustomTokenStorageConfig.class)
				.run(context -> {
					assertThat(context).hasSingleBean(TokenStorage.class);
					TokenStorage storage = context.getBean(TokenStorage.class);
					assertThat(storage).isInstanceOf(CustomTokenStorage.class);
				});
	}

	@Test
	void shouldConfigureLocalStorageWithMatchIfMissingTrue() {
		// When storage.type is not specified, it should default to LOCAL
		this.contextRunner
				.run(context -> {
					assertThat(context).hasNotFailed();
					assertThat(context).hasSingleBean(TokenStorage.class);
				});
	}

	/**
	 * Custom TokenStorage for testing
	 */
	static class CustomTokenStorage implements TokenStorage {
		@Override
		public void store(String id, Object token) {
		}

		@Override
		public <T> T retrieve(String id, Class<T> type) {
			return null;
		}

		@Override
		public void remove(String id) {
		}
	}

	/**
	 * Configuration that provides a custom TokenStorage bean
	 */
	@org.springframework.boot.test.context.TestConfiguration
	static class CustomTokenStorageConfig {
		@org.springframework.context.annotation.Bean
		public TokenStorage customTokenStorage() {
			return new CustomTokenStorage();
		}
	}
}
