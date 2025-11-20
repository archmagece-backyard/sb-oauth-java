package org.scriptonbasestar.oauth.client.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test class for Preconditions utility
 */
class PreconditionsTest {

	@Test
	void notNull_withValidValue_shouldReturnValue() {
		String value = "test";
		String result = Preconditions.notNull(value, "value must not be null");
		assertThat(result).isEqualTo("test");
	}

	@Test
	void notNull_withNullValue_shouldThrowException() {
		assertThatThrownBy(() -> Preconditions.notNull(null, "value must not be null"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("value must not be null");
	}

	@Test
	void notEmptyString_withValidString_shouldReturnString() {
		String value = "test";
		String result = Preconditions.notEmptyString(value, "string must not be empty");
		assertThat(result).isEqualTo("test");
	}

	@Test
	void notEmptyString_withNullString_shouldThrowException() {
		assertThatThrownBy(() -> Preconditions.notEmptyString(null, "string must not be empty"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("string must not be empty");
	}

	@Test
	void notEmptyString_withEmptyString_shouldThrowException() {
		assertThatThrownBy(() -> Preconditions.notEmptyString("", "string must not be empty"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("string must not be empty");
	}

	@Test
	void notBlank_withValidString_shouldReturnString() {
		String value = "test";
		String result = Preconditions.notBlank(value, "string must not be blank");
		assertThat(result).isEqualTo("test");
	}

	@Test
	void notBlank_withNullString_shouldThrowException() {
		assertThatThrownBy(() -> Preconditions.notBlank(null, "string must not be blank"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("string must not be blank");
	}

	@Test
	void notBlank_withBlankString_shouldThrowException() {
		assertThatThrownBy(() -> Preconditions.notBlank("   ", "string must not be blank"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("string must not be blank");
	}

	@Test
	void notEmpty_withValidArray_shouldReturnArray() {
		String[] array = {"test1", "test2"};
		String[] result = Preconditions.notEmpty(array, "array must not be empty");
		assertThat(result).containsExactly("test1", "test2");
	}

	@Test
	void notEmpty_withNullArray_shouldThrowException() {
		assertThatThrownBy(() -> Preconditions.notEmpty(null, "array must not be empty"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("array must not be empty");
	}

	@Test
	void notEmpty_withEmptyArray_shouldThrowException() {
		String[] emptyArray = new String[0];
		assertThatThrownBy(() -> Preconditions.notEmpty(emptyArray, "array must not be empty"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("array must not be empty");
	}

	@Test
	void customPattern_withMatchingString_shouldReturnString() {
		String email = "test@example.com";
		String pattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
		String result = Preconditions.customPattern(email, pattern, "invalid email");
		assertThat(result).isEqualTo("test@example.com");
	}

	@Test
	void customPattern_withNonMatchingString_shouldThrowException() {
		String invalid = "not-an-email";
		String pattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
		assertThatThrownBy(() -> Preconditions.customPattern(invalid, pattern, "invalid email"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("invalid email");
	}

	@Test
	void customPattern_withNullString_shouldThrowException() {
		String pattern = "^test$";
		assertThatThrownBy(() -> Preconditions.customPattern(null, pattern, "string must not be null"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("string must not be null");
	}

	@Test
	void validUrl_withValidHttpUrl_shouldReturnUrl() {
		String url = "http://example.com/path";
		String result = Preconditions.validUrl(url, "invalid URL");
		assertThat(result).isEqualTo(url);
	}

	@Test
	void validUrl_withValidHttpsUrl_shouldReturnUrl() {
		String url = "https://example.com:8080/path";
		String result = Preconditions.validUrl(url, "invalid URL");
		assertThat(result).isEqualTo(url);
	}

	@Test
	void validUrl_withInvalidUrl_shouldThrowException() {
		String invalid = "not-a-url";
		assertThatThrownBy(() -> Preconditions.validUrl(invalid, "invalid URL"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("invalid URL");
	}

	@Test
	void validUrl_withFtpUrl_shouldThrowException() {
		String ftpUrl = "ftp://example.com/path";
		assertThatThrownBy(() -> Preconditions.validUrl(ftpUrl, "invalid URL"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("invalid URL");
	}
}
