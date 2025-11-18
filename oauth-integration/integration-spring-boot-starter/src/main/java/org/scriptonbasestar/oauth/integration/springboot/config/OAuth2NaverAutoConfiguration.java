package org.scriptonbasestar.oauth.integration.springboot.config;

import com.fasterxml.jackson.core.type.TypeReference;
import org.scripton.oauth.connector.naver.OAuth2NaverAccesstokenFunction;
import org.scripton.oauth.connector.naver.OAuth2NaverAuthFunction;
import org.scripton.oauth.connector.naver.OAuth2NaverConfig;
import org.scripton.oauth.connector.naver.OAuth2NaverTokenRes;
import org.scriptonbasestar.oauth.client.nobi.TokenStorage;
import org.scriptonbasestar.oauth.client.nobi.token.JsonTokenExtractor;
import org.scriptonbasestar.oauth.client.nobi.token.TokenExtractor;
import org.scriptonbasestar.oauth.integration.springboot.properties.OAuth2Properties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * Naver OAuth 2.0 Auto Configuration
 *
 * <p>Activated when:
 * <ul>
 *   <li>OAuth2NaverConfig class is on classpath</li>
 *   <li>oauth2.providers.naver.client-id is configured</li>
 *   <li>oauth2.providers.naver.enabled is true (default)</li>
 * </ul>
 *
 * <p>Example configuration:
 * <pre>
 * oauth2:
 *   providers:
 *     naver:
 *       client-id: YOUR_CLIENT_ID
 *       client-secret: YOUR_CLIENT_SECRET
 *       redirect-uri: http://localhost:8080/oauth/callback/naver
 *       scope: profile,email
 * </pre>
 *
 * @author ScriptonBasestar Team
 * @since 2025-01-18
 */
@AutoConfiguration
@ConditionalOnClass(OAuth2NaverConfig.class)
@ConditionalOnProperty(prefix = "oauth2.providers.naver", name = "client-id")
public class OAuth2NaverAutoConfiguration {

	private final OAuth2Properties properties;

	public OAuth2NaverAutoConfiguration(OAuth2Properties properties) {
		this.properties = properties;
	}

	/**
	 * Naver OAuth configuration
	 */
	@Bean
	@ConditionalOnProperty(prefix = "oauth2.providers.naver", name = "enabled", havingValue = "true", matchIfMissing = true)
	public OAuth2NaverConfig naverConfig() {
		OAuth2Properties.ProviderProperties naver = properties.getProviders().get("naver");

		if (naver.getClientSecret() == null) {
			throw new IllegalStateException("oauth2.providers.naver.client-secret is required for Naver OAuth");
		}

		return OAuth2NaverConfig.builder()
				.clientId(naver.getClientId())
				.clientSecret(naver.getClientSecret())
				.redirectUri(naver.getRedirectUri())
				.scope(naver.getScope() != null ? naver.getScope() : "profile")
				.build();
	}

	/**
	 * Naver token extractor
	 */
	@Bean
	@ConditionalOnProperty(prefix = "oauth2.providers.naver", name = "enabled", havingValue = "true", matchIfMissing = true)
	public TokenExtractor<OAuth2NaverTokenRes> naverTokenExtractor() {
		return new JsonTokenExtractor<>(new TypeReference<OAuth2NaverTokenRes>() {});
	}

	/**
	 * Naver authorization URL generator
	 */
	@Bean
	@ConditionalOnProperty(prefix = "oauth2.providers.naver", name = "enabled", havingValue = "true", matchIfMissing = true)
	public OAuth2NaverAuthFunction naverAuthFunction(OAuth2NaverConfig config) {
		return new OAuth2NaverAuthFunction(config);
	}

	/**
	 * Naver token endpoint function (issue, refresh, revoke)
	 */
	@Bean
	@ConditionalOnProperty(prefix = "oauth2.providers.naver", name = "enabled", havingValue = "true", matchIfMissing = true)
	public OAuth2NaverAccesstokenFunction naverTokenFunction(
			OAuth2NaverConfig config,
			TokenExtractor<OAuth2NaverTokenRes> extractor,
			TokenStorage storage) {
		return new OAuth2NaverAccesstokenFunction(config, extractor, storage);
	}
}
