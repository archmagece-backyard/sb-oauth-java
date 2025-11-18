package org.scriptonbasestar.oauth.integration.springboot.properties;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for OAuth2Properties configuration binding
 */
@SpringBootTest(classes = OAuth2PropertiesTest.TestConfig.class)
@TestPropertySource(properties = {
		"oauth2.providers.naver.client-id=test-naver-client-id",
		"oauth2.providers.naver.client-secret=test-naver-client-secret",
		"oauth2.providers.naver.redirect-uri=http://localhost:8080/callback/naver",
		"oauth2.providers.naver.scope=profile,email",
		"oauth2.providers.naver.enabled=true",
		"oauth2.providers.kakao.client-id=test-kakao-client-id",
		"oauth2.providers.kakao.redirect-uri=http://localhost:8080/callback/kakao",
		"oauth2.providers.kakao.scope=profile_nickname",
		"oauth2.providers.kakao.enabled=false",
		"oauth2.storage.type=redis",
		"oauth2.storage.redis.host=redis.example.com",
		"oauth2.storage.redis.port=6380",
		"oauth2.storage.redis.password=secret",
		"oauth2.storage.redis.database=1",
		"oauth2.storage.redis.key-prefix=oauth:tokens:",
		"oauth2.storage.ehcache.cache-name=my-tokens",
		"oauth2.storage.ehcache.max-entries=2000",
		"oauth2.storage.ehcache.ttl-seconds=3600"
})
class OAuth2PropertiesTest {

	@Autowired
	private OAuth2Properties properties;

	@Test
	void shouldBindNaverProviderProperties() {
		OAuth2Properties.ProviderProperties naver = properties.getProviders().get("naver");

		assertThat(naver).isNotNull();
		assertThat(naver.getClientId()).isEqualTo("test-naver-client-id");
		assertThat(naver.getClientSecret()).isEqualTo("test-naver-client-secret");
		assertThat(naver.getRedirectUri()).isEqualTo("http://localhost:8080/callback/naver");
		assertThat(naver.getScope()).isEqualTo("profile,email");
		assertThat(naver.isEnabled()).isTrue();
	}

	@Test
	void shouldBindKakaoProviderProperties() {
		OAuth2Properties.ProviderProperties kakao = properties.getProviders().get("kakao");

		assertThat(kakao).isNotNull();
		assertThat(kakao.getClientId()).isEqualTo("test-kakao-client-id");
		assertThat(kakao.getClientSecret()).isNull(); // Not provided
		assertThat(kakao.getRedirectUri()).isEqualTo("http://localhost:8080/callback/kakao");
		assertThat(kakao.getScope()).isEqualTo("profile_nickname");
		assertThat(kakao.isEnabled()).isFalse();
	}

	@Test
	void shouldBindStorageProperties() {
		OAuth2Properties.StorageProperties storage = properties.getStorage();

		assertThat(storage).isNotNull();
		assertThat(storage.getType()).isEqualTo(OAuth2Properties.StorageType.REDIS);
	}

	@Test
	void shouldBindRedisStorageProperties() {
		OAuth2Properties.RedisProperties redis = properties.getStorage().getRedis();

		assertThat(redis).isNotNull();
		assertThat(redis.getHost()).isEqualTo("redis.example.com");
		assertThat(redis.getPort()).isEqualTo(6380);
		assertThat(redis.getPassword()).isEqualTo("secret");
		assertThat(redis.getDatabase()).isEqualTo(1);
		assertThat(redis.getKeyPrefix()).isEqualTo("oauth:tokens:");
	}

	@Test
	void shouldBindEhcacheStorageProperties() {
		OAuth2Properties.EhcacheProperties ehcache = properties.getStorage().getEhcache();

		assertThat(ehcache).isNotNull();
		assertThat(ehcache.getCacheName()).isEqualTo("my-tokens");
		assertThat(ehcache.getMaxEntries()).isEqualTo(2000);
		assertThat(ehcache.getTtlSeconds()).isEqualTo(3600);
	}

	@Test
	void shouldHaveDefaultValues() {
		// Create a new properties instance to test defaults
		OAuth2Properties defaultProps = new OAuth2Properties();

		assertThat(defaultProps.getProviders()).isEmpty();
		assertThat(defaultProps.getStorage().getType()).isEqualTo(OAuth2Properties.StorageType.LOCAL);
		assertThat(defaultProps.getStorage().getRedis().getHost()).isEqualTo("localhost");
		assertThat(defaultProps.getStorage().getRedis().getPort()).isEqualTo(6379);
		assertThat(defaultProps.getStorage().getRedis().getKeyPrefix()).isEqualTo("oauth2:tokens:");
		assertThat(defaultProps.getStorage().getEhcache().getCacheName()).isEqualTo("oauth2-tokens");
		assertThat(defaultProps.getStorage().getEhcache().getMaxEntries()).isEqualTo(1000);
		assertThat(defaultProps.getStorage().getEhcache().getTtlSeconds()).isEqualTo(7200);
	}

	@EnableConfigurationProperties(OAuth2Properties.class)
	static class TestConfig {
	}
}
