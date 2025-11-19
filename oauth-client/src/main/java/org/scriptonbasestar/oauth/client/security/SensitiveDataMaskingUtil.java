package org.scriptonbasestar.oauth.client.security;

import lombok.experimental.UtilityClass;

/**
 * Utility class for masking sensitive data in logs and toString() methods.
 *
 * <p>This class helps prevent accidental exposure of sensitive information
 * like client secrets, access tokens, and refresh tokens in logs, error messages,
 * and toString() output.</p>
 *
 * @since 1.0.0
 */
@UtilityClass
public class SensitiveDataMaskingUtil {

	private static final String MASK_PATTERN = "***";
	private static final int DEFAULT_VISIBLE_PREFIX_LENGTH = 4;
	private static final int DEFAULT_VISIBLE_SUFFIX_LENGTH = 4;

	/**
	 * Masks a sensitive string, showing only a portion of the original value.
	 *
	 * @param value the sensitive value to mask
	 * @return masked string, or null if input is null
	 */
	public static String mask(String value) {
		return mask(value, DEFAULT_VISIBLE_PREFIX_LENGTH, DEFAULT_VISIBLE_SUFFIX_LENGTH);
	}

	/**
	 * Masks a sensitive string, showing specified prefix and suffix lengths.
	 *
	 * <p>Examples:</p>
	 * <ul>
	 *   <li>mask("secret123456", 4, 4) → "secr***3456"</li>
	 *   <li>mask("short", 4, 4) → "***"</li>
	 *   <li>mask(null, 4, 4) → null</li>
	 * </ul>
	 *
	 * @param value the sensitive value to mask
	 * @param prefixLength number of characters to show at the beginning
	 * @param suffixLength number of characters to show at the end
	 * @return masked string
	 */
	public static String mask(String value, int prefixLength, int suffixLength) {
		if (value == null) {
			return null;
		}

		if (value.isEmpty()) {
			return MASK_PATTERN;
		}

		int length = value.length();
		int minLengthToShow = prefixLength + suffixLength;

		// If the value is too short, just mask it completely
		if (length <= minLengthToShow) {
			return MASK_PATTERN;
		}

		String prefix = value.substring(0, prefixLength);
		String suffix = value.substring(length - suffixLength);
		return prefix + MASK_PATTERN + suffix;
	}

	/**
	 * Masks a client secret for logging purposes.
	 * Shows first 4 and last 4 characters.
	 *
	 * @param clientSecret the client secret to mask
	 * @return masked client secret
	 */
	public static String maskClientSecret(String clientSecret) {
		return mask(clientSecret, 4, 4);
	}

	/**
	 * Masks an access token for logging purposes.
	 * Shows first 8 and last 4 characters.
	 *
	 * @param accessToken the access token to mask
	 * @return masked access token
	 */
	public static String maskAccessToken(String accessToken) {
		return mask(accessToken, 8, 4);
	}

	/**
	 * Masks a refresh token for logging purposes.
	 * Shows first 8 and last 4 characters.
	 *
	 * @param refreshToken the refresh token to mask
	 * @return masked refresh token
	 */
	public static String maskRefreshToken(String refreshToken) {
		return mask(refreshToken, 8, 4);
	}

	/**
	 * Masks an authorization code for logging purposes.
	 * Shows first 6 and last 4 characters.
	 *
	 * @param authorizationCode the authorization code to mask
	 * @return masked authorization code
	 */
	public static String maskAuthorizationCode(String authorizationCode) {
		return mask(authorizationCode, 6, 4);
	}

	/**
	 * Completely masks a value without showing any characters.
	 * Always returns the mask pattern.
	 *
	 * @param value the value to mask
	 * @return mask pattern or null if input is null
	 */
	public static String maskCompletely(String value) {
		return value == null ? null : MASK_PATTERN;
	}

	/**
	 * Checks if a string appears to be a sensitive value based on common patterns.
	 *
	 * @param value the value to check
	 * @return true if the value appears to be sensitive
	 */
	public static boolean isSensitive(String value) {
		if (value == null || value.isEmpty()) {
			return false;
		}

		String lowerValue = value.toLowerCase();
		return lowerValue.contains("secret")
			|| lowerValue.contains("token")
			|| lowerValue.contains("password")
			|| lowerValue.contains("credential")
			|| lowerValue.contains("key");
	}

	/**
	 * Masks a value only if it appears to be sensitive based on its name or content.
	 *
	 * @param fieldName the name of the field
	 * @param value the value to potentially mask
	 * @return original value if not sensitive, masked value otherwise
	 */
	public static String maskIfSensitive(String fieldName, String value) {
		if (value == null) {
			return null;
		}

		if (isSensitive(fieldName) || isSensitive(value)) {
			return mask(value);
		}

		return value;
	}
}
