package com.example.oauth.config;

import com.fasterxml.jackson.core.type.TypeReference;
import org.scripton.oauth.connector.naver.OAuth2NaverAccesstokenFunction;
import org.scripton.oauth.connector.naver.OAuth2NaverAuthFunction;
import org.scripton.oauth.connector.naver.OAuth2NaverConfig;
import org.scripton.oauth.connector.naver.OAuth2NaverTokenRes;
import org.scriptonbasestar.oauth.client.nobi.LocalTokenStorage;
import org.scriptonbasestar.oauth.client.nobi.TokenStorage;
import org.scriptonbasestar.oauth.client.nobi.state.RandomStringStateGenerator;
import org.scriptonbasestar.oauth.client.nobi.state.StateGenerator;
import org.scriptonbasestar.oauth.client.nobi.token.JsonTokenExtractor;
import org.scriptonbasestar.oauth.client.nobi.token.TokenExtractor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OAuth Configuration
 *
 * Configures Naver OAuth beans for the application.
 */
@Configuration
public class OAuthConfig {

	@Value("${oauth.naver.client-id}")
	private String clientId;

	@Value("${oauth.naver.client-secret}")
	private String clientSecret;

	@Value("${oauth.naver.redirect-uri}")
	private String redirectUri;

	@Value("${oauth.naver.scope}")
	private String scope;

	/**
	 * Naver OAuth configuration
	 */
	@Bean
	public OAuth2NaverConfig naverConfig() {
		return OAuth2NaverConfig.builder()
				.clientId(clientId)
				.clientSecret(clientSecret)
				.redirectUri(redirectUri)
				.scope(scope)
				.build();
	}

	/**
	 * State generator for CSRF protection
	 */
	@Bean
	public StateGenerator stateGenerator() {
		return new RandomStringStateGenerator();
	}

	/**
	 * Token storage (in-memory for this example)
	 * For production, use RedisTokenStorage or database
	 */
	@Bean
	public TokenStorage tokenStorage() {
		return new LocalTokenStorage();
	}

	/**
	 * Token extractor for parsing Naver token response
	 */
	@Bean
	public TokenExtractor<OAuth2NaverTokenRes> tokenExtractor() {
		return new JsonTokenExtractor<>(new TypeReference<OAuth2NaverTokenRes>() {});
	}

	/**
	 * Authorization URL generator
	 */
	@Bean
	public OAuth2NaverAuthFunction naverAuthFunction(OAuth2NaverConfig config) {
		return new OAuth2NaverAuthFunction(config);
	}

	/**
	 * Token endpoint function (issue, refresh, revoke)
	 */
	@Bean
	public OAuth2NaverAccesstokenFunction naverTokenFunction(
			OAuth2NaverConfig config,
			TokenExtractor<OAuth2NaverTokenRes> extractor,
			TokenStorage storage) {
		return new OAuth2NaverAccesstokenFunction(config, extractor, storage);
	}
}
