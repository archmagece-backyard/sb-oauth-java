package org.scriptonbasestar.oauth.client.config;

import lombok.Getter;
import org.scriptonbasestar.oauth.client.util.Preconditions;

/**
 * @author archmagece
 * @since 2016-10-24
 */
@Getter
public abstract class OAuthBaseConfig {
	private final String clientId;
	//카카오만 api-secret null
	private final String clientSecret;

	/**
	 * @param clientId
	 * @param clientSecret kakao는 null
	 */
	public OAuthBaseConfig(String clientId, String clientSecret) {
		Preconditions.notEmptyString(clientId, "clientId should not null or empty");
//		Preconditions.notEmptyString(clientSecret, "clientSecret should not empty");
		this.clientId = clientId;
		this.clientSecret = clientSecret;
	}
}
