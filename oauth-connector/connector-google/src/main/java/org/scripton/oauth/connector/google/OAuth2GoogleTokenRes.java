package org.scripton.oauth.connector.google;

import org.scriptonbasestar.oauth.client.TokenPack;
import org.scriptonbasestar.oauth.client.o20.type.AccessTokenType;

/**
 * Google OAuth 2.0 Token Response
 *
 * @param accessToken Access token for API calls
 * @param tokenType Token type (usually BEARER)
 * @param refreshToken Refresh token for renewing access (nullable)
 * @param expiresIn Token expiration time in seconds
 * @param idToken JWT ID token containing user identity (nullable)
 * @param scope Granted scopes
 * @param result Result status for revocation (nullable, "success" on successful deletion)
 * @param error Error code if request failed (nullable)
 * @param errorDescription Human-readable error description (nullable)
 */
public record OAuth2GoogleTokenRes(
	String accessToken,
	AccessTokenType tokenType,
	String refreshToken,
	Long expiresIn,
	String idToken,
	String scope,
	String result,
	String error,
	String errorDescription
) implements TokenPack {

	/**
	 * Compact constructor for validation
	 */
	public OAuth2GoogleTokenRes {
		// Validation can be added here if needed
		// For OAuth responses, we allow null values as they may not always be present
	}
}
