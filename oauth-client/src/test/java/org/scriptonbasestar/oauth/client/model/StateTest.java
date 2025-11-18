package org.scriptonbasestar.oauth.client.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for State
 */
class StateTest {

	@Test
	void shouldCreateStateWithValue() {
		State state = new State("state_value");

		assertThat(state.getValue()).isEqualTo("state_value");
	}

	@Test
	void shouldThrowExceptionForNullValue() {
		assertThatThrownBy(() -> new State(null))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void shouldThrowExceptionForEmptyValue() {
		assertThatThrownBy(() -> new State(""))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void shouldSupportEqualityComparison() {
		State state1 = new State("same_value");
		State state2 = new State("same_value");
		State state3 = new State("different_value");

		assertThat(state1).isEqualTo(state2);
		assertThat(state1).isNotEqualTo(state3);
		assertThat(state1.hashCode()).isEqualTo(state2.hashCode());
	}

	@Test
	void shouldHandleStateWithTimestamp() {
		State state = new State("user123-1234567890");

		assertThat(state.getValue()).isEqualTo("user123-1234567890");
	}

	@Test
	void shouldHandleSpecialCharacters() {
		State state = new State("state_@#$%");

		assertThat(state.getValue()).isEqualTo("state_@#$%");
	}

	@Test
	void shouldHandleUnicodeCharacters() {
		State state = new State("상태-123");

		assertThat(state.getValue()).isEqualTo("상태-123");
	}

	@Test
	void shouldHandleLongValues() {
		String longValue = "state-" + "a".repeat(500);
		State state = new State(longValue);

		assertThat(state.getValue()).isEqualTo(longValue);
	}
}
