package org.scriptonbasestar.oauth.client.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for SensitiveDataMaskingUtil
 */
class SensitiveDataMaskingUtilTest {

	@Test
	void shouldMaskStringWithDefaultLengths() {
		String value = "secret123456789";
		String masked = SensitiveDataMaskingUtil.mask(value);

		assertThat(masked).isEqualTo("secr***6789");
		assertThat(masked).contains("***");
		assertThat(masked).startsWith("secr");
		assertThat(masked).endsWith("6789");
	}

	@Test
	void shouldMaskStringWithCustomLengths() {
		String value = "secret123456789";
		String masked = SensitiveDataMaskingUtil.mask(value, 6, 3);

		assertThat(masked).isEqualTo("secret***789");
		assertThat(masked).contains("***");
		assertThat(masked).startsWith("secret");
		assertThat(masked).endsWith("789");
	}

	@Test
	void shouldMaskShortStringCompletely() {
		String value = "short";
		String masked = SensitiveDataMaskingUtil.mask(value, 4, 4);

		assertThat(masked).isEqualTo("***");
	}

	@Test
	void shouldHandleNullValue() {
		String masked = SensitiveDataMaskingUtil.mask(null);

		assertThat(masked).isNull();
	}

	@Test
	void shouldHandleEmptyString() {
		String value = "";
		String masked = SensitiveDataMaskingUtil.mask(value);

		assertThat(masked).isEqualTo("***");
	}

	@Test
	void shouldMaskClientSecret() {
		String clientSecret = "client_secret_1234567890abcdef";
		String masked = SensitiveDataMaskingUtil.maskClientSecret(clientSecret);

		assertThat(masked).isEqualTo("clie***cdef");
		assertThat(masked).contains("***");
	}

	@Test
	void shouldMaskAccessToken() {
		String accessToken = "access_token_1234567890abcdefghijklmnop";
		String masked = SensitiveDataMaskingUtil.maskAccessToken(accessToken);

		assertThat(masked).isEqualTo("access_t***mnop");
		assertThat(masked).startsWith("access_t");
		assertThat(masked).endsWith("mnop");
	}

	@Test
	void shouldMaskRefreshToken() {
		String refreshToken = "refresh_token_1234567890abcdefghijklmnop";
		String masked = SensitiveDataMaskingUtil.maskRefreshToken(refreshToken);

		assertThat(masked).isEqualTo("refresh_***mnop");
		assertThat(masked).startsWith("refresh_");
		assertThat(masked).endsWith("mnop");
	}

	@Test
	void shouldMaskAuthorizationCode() {
		String authCode = "authorization_code_1234567890abcdef";
		String masked = SensitiveDataMaskingUtil.maskAuthorizationCode(authCode);

		assertThat(masked).isEqualTo("author***cdef");
		assertThat(masked).startsWith("author");
		assertThat(masked).endsWith("cdef");
	}

	@Test
	void shouldMaskCompletely() {
		String value = "very_secret_value";
		String masked = SensitiveDataMaskingUtil.maskCompletely(value);

		assertThat(masked).isEqualTo("***");
		assertThat(masked).doesNotContain("secret");
	}

	@Test
	void shouldMaskCompletelyNullValue() {
		String masked = SensitiveDataMaskingUtil.maskCompletely(null);

		assertThat(masked).isNull();
	}

	@Test
	void shouldDetectSensitiveByKeyword() {
		assertThat(SensitiveDataMaskingUtil.isSensitive("client_secret")).isTrue();
		assertThat(SensitiveDataMaskingUtil.isSensitive("access_token")).isTrue();
		assertThat(SensitiveDataMaskingUtil.isSensitive("refresh_token")).isTrue();
		assertThat(SensitiveDataMaskingUtil.isSensitive("password")).isTrue();
		assertThat(SensitiveDataMaskingUtil.isSensitive("credential")).isTrue();
		assertThat(SensitiveDataMaskingUtil.isSensitive("api_key")).isTrue();
	}

	@Test
	void shouldNotDetectNonSensitiveAsText() {
		assertThat(SensitiveDataMaskingUtil.isSensitive("username")).isFalse();
		assertThat(SensitiveDataMaskingUtil.isSensitive("email")).isFalse();
		assertThat(SensitiveDataMaskingUtil.isSensitive("client_id")).isFalse();
		assertThat(SensitiveDataMaskingUtil.isSensitive("redirect_uri")).isFalse();
	}

	@Test
	void shouldHandleNullInIsSensitive() {
		assertThat(SensitiveDataMaskingUtil.isSensitive(null)).isFalse();
	}

	@Test
	void shouldHandleEmptyInIsSensitive() {
		assertThat(SensitiveDataMaskingUtil.isSensitive("")).isFalse();
	}

	@Test
	void shouldMaskIfSensitiveFieldName() {
		String masked = SensitiveDataMaskingUtil.maskIfSensitive("client_secret", "secret_value_12345");

		assertThat(masked).isEqualTo("secr***2345");
	}

	@Test
	void shouldNotMaskIfNotSensitiveFieldName() {
		String value = "public_value";
		String masked = SensitiveDataMaskingUtil.maskIfSensitive("client_id", value);

		assertThat(masked).isEqualTo(value);
	}

	@Test
	void shouldMaskIfSensitiveValue() {
		String masked = SensitiveDataMaskingUtil.maskIfSensitive("some_field", "my_secret_token_12345");

		assertThat(masked).isEqualTo("my_s***2345");
	}

	@Test
	void shouldHandleNullInMaskIfSensitive() {
		String masked = SensitiveDataMaskingUtil.maskIfSensitive("field", null);

		assertThat(masked).isNull();
	}

	@Test
	void shouldHandleUnicodeInMasking() {
		String value = "비밀번호_한글_secret_🔒";
		String masked = SensitiveDataMaskingUtil.mask(value);

		assertThat(masked).contains("***");
		assertThat(masked.length()).isLessThan(value.length());
	}

	@Test
	void shouldHandleLongTokens() {
		String longToken = "a".repeat(200);
		String masked = SensitiveDataMaskingUtil.maskAccessToken(longToken);

		assertThat(masked).isEqualTo("aaaaaaaa***aaaa");
		assertThat(masked.length()).isEqualTo(15);
	}

	@Test
	void shouldMaskVeryShortValue() {
		String value = "abc";
		String masked = SensitiveDataMaskingUtil.mask(value);

		assertThat(masked).isEqualTo("***");
	}

	@Test
	void shouldMaskSingleCharacter() {
		String value = "a";
		String masked = SensitiveDataMaskingUtil.mask(value);

		assertThat(masked).isEqualTo("***");
	}

	@Test
	void shouldDetectSensitiveWithUpperCase() {
		assertThat(SensitiveDataMaskingUtil.isSensitive("CLIENT_SECRET")).isTrue();
		assertThat(SensitiveDataMaskingUtil.isSensitive("ACCESS_TOKEN")).isTrue();
		assertThat(SensitiveDataMaskingUtil.isSensitive("PASSWORD")).isTrue();
	}

	@Test
	void shouldDetectSensitiveWithMixedCase() {
		assertThat(SensitiveDataMaskingUtil.isSensitive("ClientSecret")).isTrue();
		assertThat(SensitiveDataMaskingUtil.isSensitive("AccessToken")).isTrue();
		assertThat(SensitiveDataMaskingUtil.isSensitive("PassWord")).isTrue();
	}
}
