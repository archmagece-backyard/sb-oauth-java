package org.scriptonbasestar.oauth.integration.springboot.config;

import com.fasterxml.jackson.core.type.TypeReference;
import org.scripton.oauth.connector.kakao.OAuth2KakaoAccessTokenFunction;
import org.scripton.oauth.connector.kakao.OAuth2KakaoAuthFunction;
import org.scripton.oauth.connector.kakao.OAuth2KakaoConfig;
import org.scripton.oauth.connector.kakao.OAuth2KakaoTokenRes;
import org.scriptonbasestar.oauth.client.nobi.TokenStorage;
import org.scriptonbasestar.oauth.client.nobi.token.JsonTokenExtractor;
import org.scriptonbasestar.oauth.client.nobi.token.TokenExtractor;
import org.scriptonbasestar.oauth.integration.springboot.properties.OAuth2Properties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(OAuth2KakaoConfig.class)
@ConditionalOnProperty(prefix = "oauth2.providers.kakao", name = "client-id")
public class OAuth2KakaoAutoConfiguration {

	private final OAuth2Properties properties;

	public OAuth2KakaoAutoConfiguration(OAuth2Properties properties) {
		this.properties = properties;
	}

	@Bean
	@ConditionalOnProperty(prefix = "oauth2.providers.kakao", name = "enabled", havingValue = "true", matchIfMissing = true)
	public OAuth2KakaoConfig kakaoConfig() {
		OAuth2Properties.ProviderProperties kakao = properties.getProviders().get("kakao");

		return OAuth2KakaoConfig.builder()
				.clientId(kakao.getClientId())
				.clientSecret(kakao.getClientSecret()) // Optional for Kakao
				.redirectUri(kakao.getRedirectUri())
				.scope(kakao.getScope())
				.build();
	}

	@Bean
	@ConditionalOnProperty(prefix = "oauth2.providers.kakao", name = "enabled", havingValue = "true", matchIfMissing = true)
	public TokenExtractor<OAuth2KakaoTokenRes> kakaoTokenExtractor() {
		return new JsonTokenExtractor<>(new TypeReference<OAuth2KakaoTokenRes>() {});
	}

	@Bean
	@ConditionalOnProperty(prefix = "oauth2.providers.kakao", name = "enabled", havingValue = "true", matchIfMissing = true)
	public OAuth2KakaoAuthFunction kakaoAuthFunction(OAuth2KakaoConfig config) {
		return new OAuth2KakaoAuthFunction(config);
	}

	@Bean
	@ConditionalOnProperty(prefix = "oauth2.providers.kakao", name = "enabled", havingValue = "true", matchIfMissing = true)
	public OAuth2KakaoAccessTokenFunction kakaoTokenFunction(
			OAuth2KakaoConfig config,
			TokenExtractor<OAuth2KakaoTokenRes> extractor,
			TokenStorage storage) {
		return new OAuth2KakaoAccessTokenFunction(config, extractor, storage);
	}
}
