package org.scriptonbasestar.oauth.client.util;

import java.util.regex.Pattern;

/**
 * Utility class for validating preconditions and arguments.
 * Replaces the legacy sb-tools-java dependency.
 *
 * @since 2.0.0
 */
public final class Preconditions {

	private Preconditions() {
		throw new AssertionError("Utility class should not be instantiated");
	}

	/**
	 * Validates that the specified argument is not null.
	 *
	 * @param reference the object reference to check for nullity
	 * @param message   the exception message to use if the check fails
	 * @param <T>       the type of the reference
	 * @return {@code reference} if not null
	 * @throws IllegalArgumentException if {@code reference} is null
	 */
	public static <T> T notNull(T reference, String message) {
		if (reference == null) {
			throw new IllegalArgumentException(message);
		}
		return reference;
	}

	/**
	 * Validates that the specified string is not null and not empty.
	 *
	 * @param str     the string to check
	 * @param message the exception message to use if the check fails
	 * @return {@code str} if not null and not empty
	 * @throws IllegalArgumentException if {@code str} is null or empty
	 */
	public static String notEmptyString(String str, String message) {
		if (str == null || str.isEmpty()) {
			throw new IllegalArgumentException(message);
		}
		return str;
	}

	/**
	 * Validates that the specified string is not null and not blank.
	 *
	 * @param str     the string to check
	 * @param message the exception message to use if the check fails
	 * @return {@code str} if not null and not blank
	 * @throws IllegalArgumentException if {@code str} is null or blank
	 */
	public static String notBlank(String str, String message) {
		if (str == null || str.isBlank()) {
			throw new IllegalArgumentException(message);
		}
		return str;
	}

	/**
	 * Validates that the specified string matches the given regex pattern.
	 *
	 * @param str     the string to check
	 * @param pattern the regex pattern to match against
	 * @param message the exception message to use if the check fails
	 * @return {@code str} if it matches the pattern
	 * @throws IllegalArgumentException if {@code str} does not match the pattern
	 */
	public static String customPattern(String str, String pattern, String message) {
		if (str == null || !Pattern.matches(pattern, str)) {
			throw new IllegalArgumentException(message);
		}
		return str;
	}

	/**
	 * Validates that the specified string is a valid URL.
	 *
	 * @param url     the URL string to validate
	 * @param message the exception message to use if the check fails
	 * @return {@code url} if valid
	 * @throws IllegalArgumentException if {@code url} is not a valid URL
	 */
	public static String validUrl(String url, String message) {
		// Simple URL validation pattern (http/https)
		String urlPattern = "^https?://[a-zA-Z0-9.-]+(:[0-9]+)?(/.*)?$";
		return customPattern(url, urlPattern, message);
	}
}
