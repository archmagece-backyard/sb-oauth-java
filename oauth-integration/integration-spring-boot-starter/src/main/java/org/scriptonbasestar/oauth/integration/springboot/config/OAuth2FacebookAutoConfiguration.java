package org.scriptonbasestar.oauth.integration.springboot.config;

import com.fasterxml.jackson.core.type.TypeReference;
import org.scripton.oauth.connector.facebook.OAuth2FacebookAccessTokenFunction;
import org.scripton.oauth.connector.facebook.OAuth2FacebookAuthFunction;
import org.scripton.oauth.connector.facebook.OAuth2FacebookConfig;
import org.scripton.oauth.connector.facebook.OAuth2FacebookTokenRes;
import org.scriptonbasestar.oauth.client.nobi.TokenStorage;
import org.scriptonbasestar.oauth.client.nobi.token.JsonTokenExtractor;
import org.scriptonbasestar.oauth.client.nobi.token.TokenExtractor;
import org.scriptonbasestar.oauth.integration.springboot.properties.OAuth2Properties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * Facebook OAuth 2.0 Auto Configuration
 *
 * <p>Activated when:
 * <ul>
 *   <li>OAuth2FacebookConfig class is on classpath</li>
 *   <li>oauth2.providers.facebook.client-id is configured</li>
 *   <li>oauth2.providers.facebook.enabled is true (default)</li>
 * </ul>
 *
 * <p>Example configuration:
 * <pre>
 * oauth2:
 *   providers:
 *     facebook:
 *       client-id: YOUR_APP_ID
 *       client-secret: YOUR_APP_SECRET
 *       redirect-uri: http://localhost:8080/oauth/callback/facebook
 *       scope: email,public_profile
 * </pre>
 *
 * @author ScriptonBasestar Team
 * @since 2025-01-18
 */
@AutoConfiguration
@ConditionalOnClass(OAuth2FacebookConfig.class)
@ConditionalOnProperty(prefix = "oauth2.providers.facebook", name = "client-id")
public class OAuth2FacebookAutoConfiguration {

	private final OAuth2Properties properties;

	public OAuth2FacebookAutoConfiguration(OAuth2Properties properties) {
		this.properties = properties;
	}

	/**
	 * Facebook OAuth configuration
	 */
	@Bean
	@ConditionalOnProperty(prefix = "oauth2.providers.facebook", name = "enabled", havingValue = "true", matchIfMissing = true)
	public OAuth2FacebookConfig facebookConfig() {
		OAuth2Properties.ProviderProperties facebook = properties.getProviders().get("facebook");

		if (facebook.getClientSecret() == null) {
			throw new IllegalStateException("oauth2.providers.facebook.client-secret is required for Facebook OAuth");
		}

		return OAuth2FacebookConfig.builder()
				.clientId(facebook.getClientId())
				.clientSecret(facebook.getClientSecret())
				.redirectUri(facebook.getRedirectUri())
				.scope(facebook.getScope() != null ? facebook.getScope() : "email,public_profile")
				.build();
	}

	/**
	 * Facebook token extractor
	 */
	@Bean
	@ConditionalOnProperty(prefix = "oauth2.providers.facebook", name = "enabled", havingValue = "true", matchIfMissing = true)
	public TokenExtractor<OAuth2FacebookTokenRes> facebookTokenExtractor() {
		return new JsonTokenExtractor<>(new TypeReference<OAuth2FacebookTokenRes>() {});
	}

	/**
	 * Facebook authorization URL generator
	 */
	@Bean
	@ConditionalOnProperty(prefix = "oauth2.providers.facebook", name = "enabled", havingValue = "true", matchIfMissing = true)
	public OAuth2FacebookAuthFunction facebookAuthFunction(OAuth2FacebookConfig config) {
		return new OAuth2FacebookAuthFunction(config);
	}

	/**
	 * Facebook token endpoint function (issue, refresh, revoke)
	 */
	@Bean
	@ConditionalOnProperty(prefix = "oauth2.providers.facebook", name = "enabled", havingValue = "true", matchIfMissing = true)
	public OAuth2FacebookAccessTokenFunction facebookTokenFunction(
			OAuth2FacebookConfig config,
			TokenExtractor<OAuth2FacebookTokenRes> extractor,
			TokenStorage storage) {
		return new OAuth2FacebookAccessTokenFunction(config, extractor, storage);
	}
}
