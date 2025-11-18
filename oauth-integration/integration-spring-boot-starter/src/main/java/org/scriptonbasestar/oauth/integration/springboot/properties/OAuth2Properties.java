package org.scriptonbasestar.oauth.integration.springboot.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * OAuth 2.0 Configuration Properties
 *
 * Binds configuration from application.yml:
 * <pre>
 * oauth2:
 *   providers:
 *     naver:
 *       client-id: xxx
 *       client-secret: xxx
 *       redirect-uri: http://localhost:8080/oauth/callback/naver
 *       scope: profile,email
 *     kakao:
 *       client-id: xxx
 *       redirect-uri: http://localhost:8080/oauth/callback/kakao
 * </pre>
 */
@Data
@ConfigurationProperties(prefix = "oauth2")
public class OAuth2Properties {

	/**
	 * OAuth providers configuration
	 */
	private Map<String, ProviderProperties> providers = new HashMap<>();

	/**
	 * Storage configuration
	 */
	private StorageProperties storage = new StorageProperties();

	/**
	 * Provider-specific configuration
	 */
	@Data
	public static class ProviderProperties {
		/**
		 * OAuth provider type (naver, kakao, google, facebook)
		 */
		private String type;

		/**
		 * Client ID
		 */
		private String clientId;

		/**
		 * Client Secret (optional for some providers like Kakao)
		 */
		private String clientSecret;

		/**
		 * Redirect URI
		 */
		private String redirectUri;

		/**
		 * OAuth scopes (comma or space separated)
		 */
		private String scope;

		/**
		 * Authorization endpoint URL (optional, uses default if not specified)
		 */
		private String authorizeEndpoint;

		/**
		 * Token endpoint URL (optional, uses default if not specified)
		 */
		private String tokenEndpoint;

		/**
		 * Profile/Resource endpoint URL (optional)
		 */
		private String profileEndpoint;

		/**
		 * Revoke endpoint URL (optional, for providers that support it)
		 */
		private String revokeEndpoint;

		/**
		 * Enable this provider (default: true)
		 */
		private boolean enabled = true;
	}

	/**
	 * Storage configuration
	 */
	@Data
	public static class StorageProperties {
		/**
		 * Storage type: local, redis, ehcache
		 */
		private StorageType type = StorageType.LOCAL;

		/**
		 * Redis configuration (when type=redis)
		 */
		private RedisProperties redis = new RedisProperties();

		/**
		 * Ehcache configuration (when type=ehcache)
		 */
		private EhcacheProperties ehcache = new EhcacheProperties();
	}

	/**
	 * Redis storage configuration
	 */
	@Data
	public static class RedisProperties {
		/**
		 * Redis host
		 */
		private String host = "localhost";

		/**
		 * Redis port
		 */
		private int port = 6379;

		/**
		 * Redis password (optional)
		 */
		private String password;

		/**
		 * Redis database index
		 */
		private int database = 0;

		/**
		 * Connection timeout (milliseconds)
		 */
		private int timeout = 2000;

		/**
		 * Max total connections
		 */
		private int maxTotal = 50;

		/**
		 * Max idle connections
		 */
		private int maxIdle = 10;

		/**
		 * Min idle connections
		 */
		private int minIdle = 5;
	}

	/**
	 * Ehcache storage configuration
	 */
	@Data
	public static class EhcacheProperties {
		/**
		 * Cache name for tokens
		 */
		private String cacheName = "oauthTokenCache";

		/**
		 * Max entries in heap
		 */
		private int maxEntriesLocalHeap = 10000;

		/**
		 * Time to live (seconds)
		 */
		private int timeToLiveSeconds = 3600;

		/**
		 * Time to idle (seconds)
		 */
		private int timeToIdleSeconds = 1800;
	}

	/**
	 * Storage type enum
	 */
	public enum StorageType {
		/**
		 * Local in-memory storage (ConcurrentHashMap)
		 * Suitable for: Development, single-server environments
		 */
		LOCAL,

		/**
		 * Redis storage
		 * Suitable for: Production, distributed environments
		 */
		REDIS,

		/**
		 * Ehcache storage
		 * Suitable for: Single-server production
		 */
		EHCACHE
	}
}
