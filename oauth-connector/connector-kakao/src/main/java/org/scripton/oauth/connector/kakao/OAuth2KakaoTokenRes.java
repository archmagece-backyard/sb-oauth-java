package org.scripton.oauth.connector.kakao;

import org.scriptonbasestar.oauth.client.TokenPack;
import org.scriptonbasestar.oauth.client.o20.type.AccessTokenType;

/**
 * Kakao OAuth 2.0 Token Response
 *
 * @param accessToken Access token for API calls
 * @param expiresIn Token expiration time in seconds
 * @param tokenType Token type (usually BEARER)
 * @param refreshToken Refresh token for renewing access (nullable)
 * @param refreshTokenExpiresIn Refresh token expiration time in seconds (Kakao-specific)
 * @param scope Granted scopes
 */
public record OAuth2KakaoTokenRes(
	String accessToken,
	Long expiresIn,
	AccessTokenType tokenType,
	String refreshToken,
	Long refreshTokenExpiresIn,
	String scope
) implements TokenPack {

	/**
	 * Compact constructor for validation
	 */
	public OAuth2KakaoTokenRes {
		// Validation can be added here if needed
		// For OAuth responses, we allow null values as they may not always be present
	}
}
