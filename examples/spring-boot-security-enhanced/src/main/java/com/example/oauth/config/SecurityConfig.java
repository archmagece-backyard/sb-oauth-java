package com.example.oauth.config;

import org.scriptonbasestar.oauth.client.nobi.state.StateGenerator;
import org.scriptonbasestar.oauth.client.security.RedirectUriValidator;
import org.scriptonbasestar.oauth.client.security.SecureStateGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Security Configuration
 *
 * <p>Configures security components for OAuth 2.0 with production-ready settings.</p>
 *
 * <h3>Security Features:</h3>
 * <ul>
 *   <li>Cryptographically secure CSRF state generation (256-bit)</li>
 *   <li>Redirect URI validation with whitelist</li>
 *   <li>State expiration with configurable timeout</li>
 *   <li>HTTPS enforcement in production</li>
 * </ul>
 */
@Configuration
public class SecurityConfig {

	@Value("${oauth.state.max-age-seconds:600}")
	private int stateMaxAgeSeconds;

	@Value("${oauth.redirect-uri.require-https:false}")
	private boolean requireHttps;

	@Value("${oauth.redirect-uri.allow-localhost:true}")
	private boolean allowLocalhost;

	@Value("${oauth.naver.redirect-uri}")
	private String naverRedirectUri;

	/**
	 * Secure State Generator Bean
	 *
	 * <p>Generates cryptographically secure state values for CSRF protection.</p>
	 *
	 * <p><strong>Features:</strong></p>
	 * <ul>
	 *   <li>Uses {@link java.security.SecureRandom} for unpredictable values</li>
	 *   <li>32 bytes (256 bits) of random data</li>
	 *   <li>Base64 URL-safe encoding</li>
	 *   <li>Includes timestamp for expiration validation</li>
	 * </ul>
	 *
	 * @return SecureStateGenerator instance
	 */
	@Bean
	public StateGenerator stateGenerator() {
		// Use production-grade secure state generator
		// - 32 bytes (256 bits) random data
		// - Timestamp included for expiration checking
		// - URL-safe Base64 encoding
		return SecureStateGenerator.forProduction();
	}

	/**
	 * Redirect URI Validator Bean
	 *
	 * <p>Validates redirect URIs to prevent open redirect vulnerabilities.</p>
	 *
	 * <p><strong>Security Checks:</strong></p>
	 * <ul>
	 *   <li>URI format validation (scheme, host, path)</li>
	 *   <li>Whitelist verification</li>
	 *   <li>HTTPS enforcement (production mode)</li>
	 *   <li>localhost handling (development mode)</li>
	 * </ul>
	 *
	 * @return RedirectUriValidator instance
	 */
	@Bean
	public RedirectUriValidator redirectUriValidator() {
		RedirectUriValidator validator = new RedirectUriValidator(
			java.util.Set.of(naverRedirectUri),
			allowLocalhost,
			requireHttps
		);

		// Log security settings
		org.slf4j.LoggerFactory.getLogger(SecurityConfig.class).info(
			"RedirectUriValidator configured - requireHttps: {}, allowLocalhost: {}, whitelisted: {}",
			requireHttps, allowLocalhost, naverRedirectUri
		);

		return validator;
	}

	/**
	 * Gets the maximum age for state values.
	 *
	 * @return state max age duration
	 */
	@Bean
	public Duration stateMaxAge() {
		return Duration.ofSeconds(stateMaxAgeSeconds);
	}
}
