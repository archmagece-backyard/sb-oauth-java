package org.scriptonbasestar.oauth.integration.springboot.config;

import org.scriptonbasestar.oauth.client.nobi.LocalTokenStorage;
import org.scriptonbasestar.oauth.client.nobi.TokenStorage;
import org.scriptonbasestar.oauth.client.nobi.state.RandomStringStateGenerator;
import org.scriptonbasestar.oauth.client.nobi.state.StateGenerator;
import org.scriptonbasestar.oauth.integration.springboot.properties.OAuth2Properties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * OAuth 2.0 Auto Configuration
 *
 * <p>Automatically configures OAuth 2.0 beans based on application.yml properties.
 *
 * <p>Example configuration:
 * <pre>
 * oauth2:
 *   providers:
 *     naver:
 *       client-id: xxx
 *       client-secret: xxx
 *       redirect-uri: http://localhost:8080/oauth/callback/naver
 *       scope: profile,email
 *   storage:
 *     type: redis
 *     redis:
 *       host: localhost
 *       port: 6379
 * </pre>
 *
 * @author ScriptonBasestar Team
 * @since 2025-01-18
 */
@AutoConfiguration
@EnableConfigurationProperties(OAuth2Properties.class)
@Import({
		OAuth2NaverAutoConfiguration.class,
		OAuth2KakaoAutoConfiguration.class,
		OAuth2GoogleAutoConfiguration.class,
		OAuth2FacebookAutoConfiguration.class,
		OAuth2StorageAutoConfiguration.class
})
public class OAuth2AutoConfiguration {

	/**
	 * Default state generator
	 *
	 * Creates random string state for CSRF protection.
	 * Override by providing your own StateGenerator bean.
	 */
	@Bean
	@ConditionalOnMissingBean
	public StateGenerator stateGenerator() {
		return new RandomStringStateGenerator();
	}

	/**
	 * Fallback token storage
	 *
	 * Uses local in-memory storage if no other storage is configured.
	 * Not recommended for production use.
	 */
	@Bean
	@ConditionalOnMissingBean
	public TokenStorage tokenStorage() {
		return new LocalTokenStorage();
	}
}
