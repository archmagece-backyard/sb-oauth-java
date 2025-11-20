package org.scriptonbasestar.oauth.client.o20.type;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for AccessTokenType enum
 */
class AccessTokenTypeTest {

	@Test
	void bearer_shouldHaveCorrectValue() {
		assertThat(AccessTokenType.BEARER.getValue()).isEqualToIgnoringCase("bearer");
	}

	@Test
	void mac_shouldHaveCorrectValue() {
		assertThat(AccessTokenType.MAC.getValue()).isEqualToIgnoringCase("mac");
	}

	@Test
	void allValues_shouldReturnConsistentUpperCaseValues() {
		for (AccessTokenType type : AccessTokenType.values()) {
			assertThat(type.getValue()).isNotNull();
			assertThat(type.getValue()).isNotEmpty();
			assertThat(type.getValue()).isEqualTo(type.getValue().toUpperCase());
		}
	}
}
