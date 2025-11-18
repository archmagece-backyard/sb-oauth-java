package org.scriptonbasestar.oauth.client.nobi.state;

import org.junit.jupiter.api.Test;
import org.scriptonbasestar.oauth.client.model.State;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for RandomStringStateGenerator
 */
class RandomStringStateGeneratorTest {

	@Test
	void shouldGenerateStateWithDefaultSeparator() {
		RandomStringStateGenerator generator = new RandomStringStateGenerator();
		State state = generator.generate("test");

		assertThat(state).isNotNull();
		assertThat(state.getValue()).contains("test");
		assertThat(state.getValue()).contains("-");
	}

	@Test
	void shouldGenerateStateWithCustomSeparator() {
		RandomStringStateGenerator generator = new RandomStringStateGenerator('_');
		State state = generator.generate("test");

		assertThat(state).isNotNull();
		assertThat(state.getValue()).contains("test");
		assertThat(state.getValue()).contains("_");
		assertThat(state.getValue()).doesNotContain("-");
	}

	@Test
	void shouldIncludeTimestampInState() throws InterruptedException {
		RandomStringStateGenerator generator = new RandomStringStateGenerator();

		long before = System.currentTimeMillis();
		State state = generator.generate("test");
		long after = System.currentTimeMillis();

		// Extract timestamp from state
		String stateValue = state.getValue();
		String[] parts = stateValue.split("-");

		assertThat(parts).hasSize(2);
		assertThat(parts[0]).isEqualTo("test");

		long timestamp = Long.parseLong(parts[1]);
		assertThat(timestamp).isBetween(before, after);
	}

	@Test
	void shouldGenerateUniqueStatesForSameInput() throws InterruptedException {
		RandomStringStateGenerator generator = new RandomStringStateGenerator();

		State state1 = generator.generate("test");
		Thread.sleep(2); // Small delay to ensure different timestamps
		State state2 = generator.generate("test");

		assertThat(state1.getValue()).isNotEqualTo(state2.getValue());
		assertThat(state1.getValue()).startsWith("test-");
		assertThat(state2.getValue()).startsWith("test-");
	}

	@Test
	void shouldHandleEmptyString() {
		RandomStringStateGenerator generator = new RandomStringStateGenerator();
		State state = generator.generate("");

		assertThat(state).isNotNull();
		assertThat(state.getValue()).startsWith("-");
		assertThat(state.getValue()).matches("-\\d+");
	}

	@Test
	void shouldHandleSpecialCharactersInPrefix() {
		RandomStringStateGenerator generator = new RandomStringStateGenerator();
		State state = generator.generate("test@#$%");

		assertThat(state).isNotNull();
		assertThat(state.getValue()).startsWith("test@#$%-");
	}

	@Test
	void shouldHandleUnicodeCharactersInPrefix() {
		RandomStringStateGenerator generator = new RandomStringStateGenerator();
		State state = generator.generate("테스트");

		assertThat(state).isNotNull();
		assertThat(state.getValue()).startsWith("테스트-");
	}

	@Test
	void shouldHandleLongPrefix() {
		RandomStringStateGenerator generator = new RandomStringStateGenerator();
		String longPrefix = "a".repeat(1000);
		State state = generator.generate(longPrefix);

		assertThat(state).isNotNull();
		assertThat(state.getValue()).startsWith(longPrefix + "-");
	}

	@Test
	void shouldWorkWithDifferentSeparators() {
		char[] separators = {'_', ':', '|', '.', '~'};

		for (char separator : separators) {
			RandomStringStateGenerator generator = new RandomStringStateGenerator(separator);
			State state = generator.generate("test");

			assertThat(state).isNotNull();
			assertThat(state.getValue()).contains("test");
			assertThat(state.getValue()).contains(String.valueOf(separator));
		}
	}
}
