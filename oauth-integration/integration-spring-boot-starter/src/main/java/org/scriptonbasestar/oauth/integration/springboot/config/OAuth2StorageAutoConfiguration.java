package org.scriptonbasestar.oauth.integration.springboot.config;

import org.scriptonbasestar.oauth.client.nobi.TokenStorage;
import org.scriptonbasestar.oauth.client.nobi.storage.LocalTokenStorage;
import org.scriptonbasestar.oauth.integration.springboot.properties.OAuth2Properties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;
import java.util.concurrent.TimeUnit;

/**
 * OAuth 2.0 Storage Auto Configuration
 *
 * <p>Provides automatic configuration for different storage backends:
 * <ul>
 *   <li>LOCAL: In-memory storage (default, development only)</li>
 *   <li>REDIS: Redis-based distributed storage (production)</li>
 *   <li>EHCACHE: Ehcache-based storage (single-server production)</li>
 * </ul>
 *
 * <p>Example configurations:
 * <pre>
 * # Local storage (default)
 * oauth2:
 *   storage:
 *     type: local
 *
 * # Redis storage
 * oauth2:
 *   storage:
 *     type: redis
 *     redis:
 *       host: localhost
 *       port: 6379
 *       password: secret
 *       database: 0
 *       key-prefix: oauth2:tokens:
 *
 * # Ehcache storage
 * oauth2:
 *   storage:
 *     type: ehcache
 *     ehcache:
 *       cache-name: oauth2-tokens
 *       max-entries: 1000
 *       ttl-seconds: 7200
 * </pre>
 *
 * @author ScriptonBasestar Team
 * @since 2025-01-18
 */
@AutoConfiguration
public class OAuth2StorageAutoConfiguration {

	private final OAuth2Properties properties;

	public OAuth2StorageAutoConfiguration(OAuth2Properties properties) {
		this.properties = properties;
	}

	/**
	 * Local token storage (default)
	 * <p>WARNING: Only for development. Tokens are lost on server restart.
	 */
	@Bean
	@ConditionalOnMissingBean(TokenStorage.class)
	@ConditionalOnProperty(prefix = "oauth2.storage", name = "type", havingValue = "local", matchIfMissing = true)
	public TokenStorage localTokenStorage() {
		return new LocalTokenStorage();
	}

	/**
	 * Redis-based token storage
	 * <p>Recommended for production with multiple servers
	 */
	@Bean
	@ConditionalOnClass(RedisConnectionFactory.class)
	@ConditionalOnProperty(prefix = "oauth2.storage", name = "type", havingValue = "redis")
	public TokenStorage redisTokenStorage(RedisTemplate<String, Object> redisTemplate) {
		String keyPrefix = properties.getStorage().getRedis().getKeyPrefix();
		return new RedisTokenStorage(redisTemplate, keyPrefix);
	}

	/**
	 * Redis template for OAuth tokens
	 */
	@Bean
	@ConditionalOnClass(RedisConnectionFactory.class)
	@ConditionalOnProperty(prefix = "oauth2.storage", name = "type", havingValue = "redis")
	public RedisTemplate<String, Object> oauthRedisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
		template.setHashKeySerializer(new StringRedisSerializer());
		template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
		return template;
	}

	/**
	 * Ehcache-based token storage
	 * <p>Suitable for single-server production deployments
	 */
	@Bean
	@ConditionalOnClass(CacheManager.class)
	@ConditionalOnProperty(prefix = "oauth2.storage", name = "type", havingValue = "ehcache")
	public TokenStorage ehcacheTokenStorage() {
		OAuth2Properties.EhcacheProperties ehcacheProps = properties.getStorage().getEhcache();

		CachingProvider cachingProvider = Caching.getCachingProvider();
		CacheManager cacheManager = cachingProvider.getCacheManager();

		MutableConfiguration<String, Object> config = new MutableConfiguration<>();
		config.setTypes(String.class, Object.class);
		config.setStoreByValue(false);
		config.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(
				new Duration(TimeUnit.SECONDS, ehcacheProps.getTtlSeconds())
		));

		String cacheName = ehcacheProps.getCacheName();
		if (cacheManager.getCache(cacheName) == null) {
			cacheManager.createCache(cacheName, config);
		}

		return new EhcacheTokenStorage(cacheManager.getCache(cacheName));
	}

	/**
	 * Redis-based TokenStorage implementation
	 */
	private static class RedisTokenStorage implements TokenStorage {
		private final RedisTemplate<String, Object> redisTemplate;
		private final String keyPrefix;

		public RedisTokenStorage(RedisTemplate<String, Object> redisTemplate, String keyPrefix) {
			this.redisTemplate = redisTemplate;
			this.keyPrefix = keyPrefix;
		}

		@Override
		public void store(String id, Object token) {
			redisTemplate.opsForValue().set(keyPrefix + id, token);
		}

		@Override
		public <T> T retrieve(String id, Class<T> type) {
			Object value = redisTemplate.opsForValue().get(keyPrefix + id);
			return type.cast(value);
		}

		@Override
		public void remove(String id) {
			redisTemplate.delete(keyPrefix + id);
		}
	}

	/**
	 * Ehcache-based TokenStorage implementation
	 */
	private static class EhcacheTokenStorage implements TokenStorage {
		private final javax.cache.Cache<String, Object> cache;

		public EhcacheTokenStorage(javax.cache.Cache<String, Object> cache) {
			this.cache = cache;
		}

		@Override
		public void store(String id, Object token) {
			cache.put(id, token);
		}

		@Override
		public <T> T retrieve(String id, Class<T> type) {
			Object value = cache.get(id);
			return type.cast(value);
		}

		@Override
		public void remove(String id) {
			cache.remove(id);
		}
	}
}
