package org.scriptonbasestar.oauth.client.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for Token and related value models
 */
class TokenTest {

	@Test
	void shouldCreateTokenWithValue() {
		Token token = new Token("access_token_value");

		assertThat(token.getValue()).isEqualTo("access_token_value");
	}

	@Test
	void shouldThrowExceptionForNullValue() {
		assertThatThrownBy(() -> new Token(null))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void shouldThrowExceptionForEmptyValue() {
		assertThatThrownBy(() -> new Token(""))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void shouldSupportEqualityComparison() {
		Token token1 = new Token("same_value");
		Token token2 = new Token("same_value");
		Token token3 = new Token("different_value");

		assertThat(token1).isEqualTo(token2);
		assertThat(token1).isNotEqualTo(token3);
		assertThat(token1.hashCode()).isEqualTo(token2.hashCode());
	}

	@Test
	void shouldHandleLongTokenValues() {
		String longValue = "a".repeat(1000);
		Token token = new Token(longValue);

		assertThat(token.getValue()).isEqualTo(longValue);
	}

	@Test
	void shouldHandleSpecialCharacters() {
		String specialValue = "token@#$%^&*()_+-=[]{}|;:',.<>?/~`";
		Token token = new Token(specialValue);

		assertThat(token.getValue()).isEqualTo(specialValue);
	}

	@Test
	void shouldHandleBase64EncodedValues() {
		String base64Value = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0.dozjgNryP4J3jVmNHl0w5N_XgL0n3I9PlFUP0THsR8U";
		Token token = new Token(base64Value);

		assertThat(token.getValue()).isEqualTo(base64Value);
	}
}
