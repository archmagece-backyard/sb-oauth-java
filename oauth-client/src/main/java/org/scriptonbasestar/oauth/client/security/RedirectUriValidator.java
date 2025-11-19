package org.scriptonbasestar.oauth.client.security;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Validator for OAuth redirect URIs to prevent open redirect vulnerabilities.
 *
 * <p>This validator ensures that redirect URIs are valid and match configured
 * whitelist patterns, preventing attackers from redirecting users to malicious sites.</p>
 *
 * @since 1.0.0
 */
public class RedirectUriValidator {

	private final Set<String> allowedRedirectUris;
	private final boolean allowLocalhost;
	private final boolean requireHttps;

	/**
	 * Creates a validator with exact URI matching only.
	 *
	 * @param allowedRedirectUris set of allowed redirect URIs
	 */
	public RedirectUriValidator(Set<String> allowedRedirectUris) {
		this(allowedRedirectUris, false, false);
	}

	/**
	 * Creates a validator with configurable options.
	 *
	 * @param allowedRedirectUris set of allowed redirect URIs
	 * @param allowLocalhost whether to allow localhost/127.0.0.1 URIs for development
	 * @param requireHttps whether to require HTTPS (except for localhost if allowed)
	 */
	public RedirectUriValidator(Set<String> allowedRedirectUris, boolean allowLocalhost, boolean requireHttps) {
		this.allowedRedirectUris = new HashSet<>(allowedRedirectUris);
		this.allowLocalhost = allowLocalhost;
		this.requireHttps = requireHttps;
	}

	/**
	 * Validates a redirect URI against the configured rules.
	 *
	 * @param redirectUri the redirect URI to validate
	 * @return true if the URI is valid and allowed
	 */
	public boolean isValid(String redirectUri) {
		if (redirectUri == null || redirectUri.trim().isEmpty()) {
			return false;
		}

		try {
			URI uri = new URI(redirectUri);

			// Check if scheme exists
			if (uri.getScheme() == null) {
				return false;
			}

			// Check if host exists
			if (uri.getHost() == null) {
				return false;
			}

			// Check for localhost if allowed
			boolean isLocalhost = isLocalhost(uri.getHost());

			// Check HTTPS requirement
			if (requireHttps && !isLocalhost && !"https".equalsIgnoreCase(uri.getScheme())) {
				return false;
			}

			// Allow localhost in development mode
			if (allowLocalhost && isLocalhost) {
				return true;
			}

			// Check against whitelist
			return allowedRedirectUris.contains(redirectUri);

		} catch (URISyntaxException e) {
			return false;
		}
	}

	/**
	 * Validates a redirect URI and throws exception if invalid.
	 *
	 * @param redirectUri the redirect URI to validate
	 * @throws IllegalArgumentException if the URI is invalid
	 */
	public void validate(String redirectUri) {
		if (!isValid(redirectUri)) {
			throw new IllegalArgumentException("Invalid redirect URI: " + redirectUri);
		}
	}

	/**
	 * Checks if the URI matches any of the allowed patterns.
	 *
	 * @param redirectUri the redirect URI to check
	 * @return true if the URI matches the whitelist
	 */
	public boolean matchesWhitelist(String redirectUri) {
		return allowedRedirectUris.contains(redirectUri);
	}

	/**
	 * Checks if a host is localhost or loopback address.
	 *
	 * @param host the host to check
	 * @return true if the host is localhost
	 */
	private boolean isLocalhost(String host) {
		return "localhost".equalsIgnoreCase(host)
			|| "127.0.0.1".equals(host)
			|| "::1".equals(host)
			|| host.startsWith("127.")
			|| host.startsWith("0.0.0.0");
	}

	/**
	 * Creates a validator that allows localhost for development.
	 *
	 * @param allowedRedirectUris set of allowed redirect URIs
	 * @return validator with localhost allowed
	 */
	public static RedirectUriValidator forDevelopment(String... allowedRedirectUris) {
		return new RedirectUriValidator(
			new HashSet<>(Arrays.asList(allowedRedirectUris)),
			true,
			false
		);
	}

	/**
	 * Creates a validator for production with HTTPS required.
	 *
	 * @param allowedRedirectUris set of allowed redirect URIs
	 * @return validator with HTTPS required
	 */
	public static RedirectUriValidator forProduction(String... allowedRedirectUris) {
		return new RedirectUriValidator(
			new HashSet<>(Arrays.asList(allowedRedirectUris)),
			false,
			true
		);
	}

	/**
	 * Gets the set of allowed redirect URIs.
	 *
	 * @return unmodifiable set of allowed URIs
	 */
	public Set<String> getAllowedRedirectUris() {
		return new HashSet<>(allowedRedirectUris);
	}

	/**
	 * Checks if localhost is allowed.
	 *
	 * @return true if localhost is allowed
	 */
	public boolean isAllowLocalhost() {
		return allowLocalhost;
	}

	/**
	 * Checks if HTTPS is required.
	 *
	 * @return true if HTTPS is required
	 */
	public boolean isRequireHttps() {
		return requireHttps;
	}
}
