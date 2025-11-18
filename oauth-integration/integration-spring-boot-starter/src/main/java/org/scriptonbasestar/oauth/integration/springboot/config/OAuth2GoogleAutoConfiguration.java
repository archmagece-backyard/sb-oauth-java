package org.scriptonbasestar.oauth.integration.springboot.config;

import com.fasterxml.jackson.core.type.TypeReference;
import org.scripton.oauth.connector.google.OAuth2GoogleAccessTokenFunction;
import org.scripton.oauth.connector.google.OAuth2GoogleAuthFunction;
import org.scripton.oauth.connector.google.OAuth2GoogleConfig;
import org.scripton.oauth.connector.google.OAuth2GoogleTokenRes;
import org.scriptonbasestar.oauth.client.nobi.TokenStorage;
import org.scriptonbasestar.oauth.client.nobi.token.JsonTokenExtractor;
import org.scriptonbasestar.oauth.client.nobi.token.TokenExtractor;
import org.scriptonbasestar.oauth.integration.springboot.properties.OAuth2Properties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * Google OAuth 2.0 Auto Configuration
 *
 * <p>Activated when:
 * <ul>
 *   <li>OAuth2GoogleConfig class is on classpath</li>
 *   <li>oauth2.providers.google.client-id is configured</li>
 *   <li>oauth2.providers.google.enabled is true (default)</li>
 * </ul>
 *
 * <p>Example configuration:
 * <pre>
 * oauth2:
 *   providers:
 *     google:
 *       client-id: YOUR_CLIENT_ID
 *       client-secret: YOUR_CLIENT_SECRET
 *       redirect-uri: http://localhost:8080/oauth/callback/google
 *       scope: openid,profile,email
 * </pre>
 *
 * @author ScriptonBasestar Team
 * @since 2025-01-18
 */
@AutoConfiguration
@ConditionalOnClass(OAuth2GoogleConfig.class)
@ConditionalOnProperty(prefix = "oauth2.providers.google", name = "client-id")
public class OAuth2GoogleAutoConfiguration {

	private final OAuth2Properties properties;

	public OAuth2GoogleAutoConfiguration(OAuth2Properties properties) {
		this.properties = properties;
	}

	/**
	 * Google OAuth configuration
	 */
	@Bean
	@ConditionalOnProperty(prefix = "oauth2.providers.google", name = "enabled", havingValue = "true", matchIfMissing = true)
	public OAuth2GoogleConfig googleConfig() {
		OAuth2Properties.ProviderProperties google = properties.getProviders().get("google");

		if (google.getClientSecret() == null) {
			throw new IllegalStateException("oauth2.providers.google.client-secret is required for Google OAuth");
		}

		return OAuth2GoogleConfig.builder()
				.clientId(google.getClientId())
				.clientSecret(google.getClientSecret())
				.redirectUri(google.getRedirectUri())
				.scope(google.getScope() != null ? google.getScope() : "openid profile email")
				.build();
	}

	/**
	 * Google token extractor
	 */
	@Bean
	@ConditionalOnProperty(prefix = "oauth2.providers.google", name = "enabled", havingValue = "true", matchIfMissing = true)
	public TokenExtractor<OAuth2GoogleTokenRes> googleTokenExtractor() {
		return new JsonTokenExtractor<>(new TypeReference<OAuth2GoogleTokenRes>() {});
	}

	/**
	 * Google authorization URL generator
	 */
	@Bean
	@ConditionalOnProperty(prefix = "oauth2.providers.google", name = "enabled", havingValue = "true", matchIfMissing = true)
	public OAuth2GoogleAuthFunction googleAuthFunction(OAuth2GoogleConfig config) {
		return new OAuth2GoogleAuthFunction(config);
	}

	/**
	 * Google token endpoint function (issue, refresh, revoke)
	 */
	@Bean
	@ConditionalOnProperty(prefix = "oauth2.providers.google", name = "enabled", havingValue = "true", matchIfMissing = true)
	public OAuth2GoogleAccessTokenFunction googleTokenFunction(
			OAuth2GoogleConfig config,
			TokenExtractor<OAuth2GoogleTokenRes> extractor,
			TokenStorage storage) {
		return new OAuth2GoogleAccessTokenFunction(config, extractor, storage);
	}
}
