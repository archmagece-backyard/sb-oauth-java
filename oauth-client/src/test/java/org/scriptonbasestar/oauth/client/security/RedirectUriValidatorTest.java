package org.scriptonbasestar.oauth.client.security;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for RedirectUriValidator
 */
class RedirectUriValidatorTest {

	@Test
	void shouldValidateExactMatch() {
		Set<String> allowed = Set.of("https://example.com/callback");
		RedirectUriValidator validator = new RedirectUriValidator(allowed);

		assertThat(validator.isValid("https://example.com/callback")).isTrue();
	}

	@Test
	void shouldRejectNonMatchingUri() {
		Set<String> allowed = Set.of("https://example.com/callback");
		RedirectUriValidator validator = new RedirectUriValidator(allowed);

		assertThat(validator.isValid("https://malicious.com/callback")).isFalse();
	}

	@Test
	void shouldRejectNullUri() {
		Set<String> allowed = Set.of("https://example.com/callback");
		RedirectUriValidator validator = new RedirectUriValidator(allowed);

		assertThat(validator.isValid(null)).isFalse();
	}

	@Test
	void shouldRejectEmptyUri() {
		Set<String> allowed = Set.of("https://example.com/callback");
		RedirectUriValidator validator = new RedirectUriValidator(allowed);

		assertThat(validator.isValid("")).isFalse();
		assertThat(validator.isValid("   ")).isFalse();
	}

	@Test
	void shouldRejectUriWithoutScheme() {
		Set<String> allowed = Set.of("https://example.com/callback");
		RedirectUriValidator validator = new RedirectUriValidator(allowed);

		assertThat(validator.isValid("example.com/callback")).isFalse();
	}

	@Test
	void shouldRejectUriWithoutHost() {
		Set<String> allowed = Set.of("https://example.com/callback");
		RedirectUriValidator validator = new RedirectUriValidator(allowed);

		assertThat(validator.isValid("https:///callback")).isFalse();
	}

	@Test
	void shouldAllowLocalhostWhenEnabled() {
		Set<String> allowed = Set.of("https://example.com/callback");
		RedirectUriValidator validator = new RedirectUriValidator(allowed, true, false);

		assertThat(validator.isValid("http://localhost:8080/callback")).isTrue();
		assertThat(validator.isValid("http://127.0.0.1:8080/callback")).isTrue();
		assertThat(validator.isValid("http://127.0.0.1/callback")).isTrue();
	}

	@Test
	void shouldRejectLocalhostWhenDisabled() {
		Set<String> allowed = Set.of("https://example.com/callback");
		RedirectUriValidator validator = new RedirectUriValidator(allowed, false, false);

		assertThat(validator.isValid("http://localhost:8080/callback")).isFalse();
		assertThat(validator.isValid("http://127.0.0.1:8080/callback")).isFalse();
	}

	@Test
	void shouldRequireHttpsWhenEnabled() {
		Set<String> allowed = Set.of("https://example.com/callback");
		RedirectUriValidator validator = new RedirectUriValidator(allowed, false, true);

		assertThat(validator.isValid("https://example.com/callback")).isTrue();
		assertThat(validator.isValid("http://example.com/callback")).isFalse();
	}

	@Test
	void shouldAllowHttpForLocalhostEvenWithHttpsRequired() {
		Set<String> allowed = Set.of("https://example.com/callback");
		RedirectUriValidator validator = new RedirectUriValidator(allowed, true, true);

		assertThat(validator.isValid("http://localhost:8080/callback")).isTrue();
		assertThat(validator.isValid("http://127.0.0.1:8080/callback")).isTrue();
		assertThat(validator.isValid("http://example.com/callback")).isFalse();
	}

	@Test
	void shouldThrowExceptionForInvalidUri() {
		Set<String> allowed = Set.of("https://example.com/callback");
		RedirectUriValidator validator = new RedirectUriValidator(allowed);

		assertThatThrownBy(() -> validator.validate("https://malicious.com/callback"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("Invalid redirect URI");
	}

	@Test
	void shouldNotThrowExceptionForValidUri() {
		Set<String> allowed = Set.of("https://example.com/callback");
		RedirectUriValidator validator = new RedirectUriValidator(allowed);

		// Should not throw
		validator.validate("https://example.com/callback");
	}

	@Test
	void shouldMatchWhitelist() {
		Set<String> allowed = Set.of(
			"https://example.com/callback",
			"https://example.com/callback2"
		);
		RedirectUriValidator validator = new RedirectUriValidator(allowed);

		assertThat(validator.matchesWhitelist("https://example.com/callback")).isTrue();
		assertThat(validator.matchesWhitelist("https://example.com/callback2")).isTrue();
		assertThat(validator.matchesWhitelist("https://malicious.com/callback")).isFalse();
	}

	@Test
	void shouldCreateDevelopmentValidator() {
		RedirectUriValidator validator = RedirectUriValidator.forDevelopment(
			"https://example.com/callback"
		);

		assertThat(validator.isAllowLocalhost()).isTrue();
		assertThat(validator.isRequireHttps()).isFalse();
		assertThat(validator.isValid("http://localhost:8080/callback")).isTrue();
	}

	@Test
	void shouldCreateProductionValidator() {
		RedirectUriValidator validator = RedirectUriValidator.forProduction(
			"https://example.com/callback"
		);

		assertThat(validator.isAllowLocalhost()).isFalse();
		assertThat(validator.isRequireHttps()).isTrue();
		assertThat(validator.isValid("http://localhost:8080/callback")).isFalse();
		assertThat(validator.isValid("https://example.com/callback")).isTrue();
	}

	@Test
	void shouldGetAllowedRedirectUris() {
		Set<String> allowed = Set.of(
			"https://example.com/callback",
			"https://example.com/callback2"
		);
		RedirectUriValidator validator = new RedirectUriValidator(allowed);

		Set<String> result = validator.getAllowedRedirectUris();

		assertThat(result).containsExactlyInAnyOrder(
			"https://example.com/callback",
			"https://example.com/callback2"
		);
	}

	@Test
	void shouldHandleMultipleAllowedUris() {
		Set<String> allowed = Set.of(
			"https://example.com/callback",
			"https://example.com/oauth/callback",
			"https://app.example.com/callback"
		);
		RedirectUriValidator validator = new RedirectUriValidator(allowed);

		assertThat(validator.isValid("https://example.com/callback")).isTrue();
		assertThat(validator.isValid("https://example.com/oauth/callback")).isTrue();
		assertThat(validator.isValid("https://app.example.com/callback")).isTrue();
		assertThat(validator.isValid("https://malicious.com/callback")).isFalse();
	}

	@Test
	void shouldRejectPathTraversal() {
		Set<String> allowed = Set.of("https://example.com/callback");
		RedirectUriValidator validator = new RedirectUriValidator(allowed);

		assertThat(validator.isValid("https://example.com/callback/../admin")).isFalse();
	}

	@Test
	void shouldRejectDifferentPort() {
		Set<String> allowed = Set.of("https://example.com:443/callback");
		RedirectUriValidator validator = new RedirectUriValidator(allowed);

		assertThat(validator.isValid("https://example.com:8443/callback")).isFalse();
	}

	@Test
	void shouldRejectDifferentPath() {
		Set<String> allowed = Set.of("https://example.com/callback");
		RedirectUriValidator validator = new RedirectUriValidator(allowed);

		assertThat(validator.isValid("https://example.com/callback2")).isFalse();
		assertThat(validator.isValid("https://example.com/callback/extra")).isFalse();
	}

	@Test
	void shouldRejectDifferentQueryString() {
		Set<String> allowed = Set.of("https://example.com/callback");
		RedirectUriValidator validator = new RedirectUriValidator(allowed);

		assertThat(validator.isValid("https://example.com/callback?evil=true")).isFalse();
	}

	@Test
	void shouldAllowQueryStringIfInWhitelist() {
		Set<String> allowed = Set.of("https://example.com/callback?param=value");
		RedirectUriValidator validator = new RedirectUriValidator(allowed);

		assertThat(validator.isValid("https://example.com/callback?param=value")).isTrue();
		assertThat(validator.isValid("https://example.com/callback?param=other")).isFalse();
	}

	@Test
	void shouldHandleIPv6Localhost() {
		Set<String> allowed = new HashSet<>();
		RedirectUriValidator validator = new RedirectUriValidator(allowed, true, false);

		assertThat(validator.isValid("http://[::1]:8080/callback")).isTrue();
	}

	@Test
	void shouldRejectMalformedUri() {
		Set<String> allowed = Set.of("https://example.com/callback");
		RedirectUriValidator validator = new RedirectUriValidator(allowed);

		assertThat(validator.isValid("https://example.com:invalid/callback")).isFalse();
		assertThat(validator.isValid("ht!tp://example.com/callback")).isFalse();
	}

	@Test
	void shouldHandleTrailingSlash() {
		Set<String> allowed = Set.of("https://example.com/callback/");
		RedirectUriValidator validator = new RedirectUriValidator(allowed);

		assertThat(validator.isValid("https://example.com/callback/")).isTrue();
		assertThat(validator.isValid("https://example.com/callback")).isFalse();
	}

	@Test
	void shouldBeCaseSensitiveForPath() {
		Set<String> allowed = Set.of("https://example.com/callback");
		RedirectUriValidator validator = new RedirectUriValidator(allowed);

		assertThat(validator.isValid("https://example.com/Callback")).isFalse();
		assertThat(validator.isValid("https://example.com/CALLBACK")).isFalse();
	}

	@Test
	void shouldDetectLocalhostVariants() {
		Set<String> allowed = new HashSet<>();
		RedirectUriValidator validator = new RedirectUriValidator(allowed, true, false);

		assertThat(validator.isValid("http://localhost/callback")).isTrue();
		assertThat(validator.isValid("http://LOCALHOST/callback")).isTrue();
		assertThat(validator.isValid("http://127.0.0.1/callback")).isTrue();
		assertThat(validator.isValid("http://127.0.0.2/callback")).isTrue();
		assertThat(validator.isValid("http://0.0.0.0/callback")).isTrue();
	}
}
