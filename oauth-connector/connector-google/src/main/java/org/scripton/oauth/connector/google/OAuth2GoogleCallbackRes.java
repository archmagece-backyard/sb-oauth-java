package org.scripton.oauth.connector.google;

/**
 * Google OAuth 2.0 Callback Response
 *
 * @param code          Authorization code
 * @param state         CSRF protection state parameter
 * @param error         Error code if authorization failed (nullable)
 * @param errorDescription Human-readable error description (nullable)
 */
public record OAuth2GoogleCallbackRes(
  String code,
  String state,
  String error,
  String errorDescription
) {}
