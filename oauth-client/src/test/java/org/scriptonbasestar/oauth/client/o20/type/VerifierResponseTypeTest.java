package org.scriptonbasestar.oauth.client.o20.type;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for VerifierResponseType enum
 */
class VerifierResponseTypeTest {

	@Test
	void code_shouldHaveCorrectValue() {
		assertThat(VerifierResponseType.CODE.getValue()).isEqualTo("code");
	}

	@Test
	void token_shouldHaveCorrectValue() {
		assertThat(VerifierResponseType.TOKEN.getValue()).isEqualTo("token");
	}

	@Test
	void allValues_shouldReturnConsistentValues() {
		for (VerifierResponseType type : VerifierResponseType.values()) {
			assertThat(type.getValue()).isNotNull();
			assertThat(type.getValue()).isNotEmpty();
		}
	}
}
