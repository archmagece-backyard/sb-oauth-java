package org.scripton.oauth.connector.facebook;

import org.scriptonbasestar.oauth.client.TokenPack;
import org.scriptonbasestar.oauth.client.o20.type.AccessTokenType;

/**
 * Facebook OAuth 2.0 Token Response
 *
 * @param accessToken Access token for API calls
 * @param tokenType Token type (usually BEARER)
 * @param refreshToken Refresh token for renewing access (nullable)
 * @param expiresIn Token expiration time in seconds
 * @param result Result status for revocation (nullable, "success" on successful deletion)
 * @param error Error code if request failed (nullable)
 * @param errorDescription Human-readable error description (nullable)
 */
public record OAuth2FacebookTokenRes(
  String accessToken,
  AccessTokenType tokenType,
  String refreshToken,
  Long expiresIn,
  String result,
  String error,
  String errorDescription
) implements TokenPack {

  /**
   * Compact constructor for validation
   */
  public OAuth2FacebookTokenRes {
    // Validation can be added here if needed
    // For OAuth responses, we allow null values as they may not always be present
  }

  // Explicit implementations of TokenPack interface methods
  // Records generate accessToken() but interface requires getAccessToken()
  @Override
  public String getAccessToken() {
    return accessToken;
  }

  @Override
  public AccessTokenType getTokenType() {
    return tokenType;
  }

  @Override
  public String getRefreshToken() {
    return refreshToken;
  }

  @Override
  public Long getExpiresIn() {
    return expiresIn;
  }
}
