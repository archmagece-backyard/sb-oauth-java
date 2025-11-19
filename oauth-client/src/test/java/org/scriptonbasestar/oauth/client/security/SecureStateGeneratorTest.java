package org.scriptonbasestar.oauth.client.security;

import org.junit.jupiter.api.Test;
import org.scriptonbasestar.oauth.client.model.State;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for SecureStateGenerator
 */
class SecureStateGeneratorTest {

	@Test
	void shouldGenerateState() {
		SecureStateGenerator generator = new SecureStateGenerator();

		State state = generator.generate();

		assertThat(state).isNotNull();
		assertThat(state.getValue()).isNotNull();
		assertThat(state.getValue()).isNotEmpty();
	}

	@Test
	void shouldGenerateUniqueStates() {
		SecureStateGenerator generator = new SecureStateGenerator();
		Set<String> states = new HashSet<>();

		// Generate 100 states and ensure they're all unique
		for (int i = 0; i < 100; i++) {
			State state = generator.generate();
			states.add(state.getValue());
		}

		assertThat(states).hasSize(100);
	}

	@Test
	void shouldIncludeTimestampWhenEnabled() {
		SecureStateGenerator generator = new SecureStateGenerator(32, true, '-');

		State state = generator.generate();

		assertThat(state.getValue()).contains("-");
		long timestamp = generator.extractTimestamp(state);
		assertThat(timestamp).isGreaterThan(0);
	}

	@Test
	void shouldNotIncludeTimestampWhenDisabled() {
		SecureStateGenerator generator = new SecureStateGenerator(32, false, '-');

		State state = generator.generate();

		long timestamp = generator.extractTimestamp(state);
		assertThat(timestamp).isEqualTo(-1);
	}

	@Test
	void shouldExtractTimestamp() throws InterruptedException {
		SecureStateGenerator generator = new SecureStateGenerator();
		long before = System.currentTimeMillis();

		State state = generator.generate();
		Thread.sleep(10); // Small delay to ensure timestamp difference

		long after = System.currentTimeMillis();
		long timestamp = generator.extractTimestamp(state);

		assertThat(timestamp).isBetween(before, after);
	}

	@Test
	void shouldReturnNegativeOneForNullState() {
		SecureStateGenerator generator = new SecureStateGenerator();

		long timestamp = generator.extractTimestamp(null);

		assertThat(timestamp).isEqualTo(-1);
	}

	@Test
	void shouldDetectExpiredState() throws InterruptedException {
		SecureStateGenerator generator = new SecureStateGenerator();

		State state = generator.generate();
		Thread.sleep(100);

		boolean expired = generator.isExpired(state, 50); // 50ms max age

		assertThat(expired).isTrue();
	}

	@Test
	void shouldNotExpireRecentState() {
		SecureStateGenerator generator = new SecureStateGenerator();

		State state = generator.generate();

		boolean expired = generator.isExpired(state, 10000); // 10 seconds max age

		assertThat(expired).isFalse();
	}

	@Test
	void shouldThrowExceptionForExpiredState() throws InterruptedException {
		SecureStateGenerator generator = new SecureStateGenerator();

		State state = generator.generate();
		Thread.sleep(100);

		assertThatThrownBy(() -> generator.validateNotExpired(state, 50))
			.isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("expired");
	}

	@Test
	void shouldNotThrowExceptionForValidState() {
		SecureStateGenerator generator = new SecureStateGenerator();

		State state = generator.generate();

		// Should not throw
		generator.validateNotExpired(state, 10000);
	}

	@Test
	void shouldThrowExceptionForTooFewRandomBytes() {
		assertThatThrownBy(() -> new SecureStateGenerator(8, true, '-'))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("at least 16");
	}

	@Test
	void shouldGenerateWithMinimumBytes() {
		SecureStateGenerator generator = new SecureStateGenerator(16, true, '-');

		State state = generator.generate();

		assertThat(state).isNotNull();
		assertThat(state.getValue()).isNotEmpty();
	}

	@Test
	void shouldGenerateWithLargeByteCount() {
		SecureStateGenerator generator = new SecureStateGenerator(64, true, '-');

		State state = generator.generate();

		assertThat(state).isNotNull();
		assertThat(state.getValue()).isNotEmpty();
		// Larger byte count should produce longer base64 string
		assertThat(state.getValue().length()).isGreaterThan(50);
	}

	@Test
	void shouldUseCustomSeparator() {
		SecureStateGenerator generator = new SecureStateGenerator(32, true, '_');

		State state = generator.generate();

		assertThat(state.getValue()).contains("_");
		assertThat(state.getValue()).doesNotContain("-");
	}

	@Test
	void shouldCreateProductionGenerator() {
		SecureStateGenerator generator = SecureStateGenerator.forProduction();

		assertThat(generator.getRandomBytes()).isEqualTo(32);
		assertThat(generator.isIncludeTimestamp()).isTrue();
		assertThat(generator.getSeparator()).isEqualTo('-');

		State state = generator.generate();
		assertThat(state).isNotNull();
	}

	@Test
	void shouldCreateDevelopmentGenerator() {
		SecureStateGenerator generator = SecureStateGenerator.forDevelopment();

		assertThat(generator.getRandomBytes()).isEqualTo(16);
		assertThat(generator.isIncludeTimestamp()).isTrue();

		State state = generator.generate();
		assertThat(state).isNotNull();
	}

	@Test
	void shouldIgnoreValuesParameter() {
		SecureStateGenerator generator = new SecureStateGenerator();

		State state1 = generator.generate("ignored");
		State state2 = generator.generate("also", "ignored");
		State state3 = generator.generate();

		// All should be unique despite same/different parameters
		assertThat(state1.getValue()).isNotEqualTo(state2.getValue());
		assertThat(state2.getValue()).isNotEqualTo(state3.getValue());
	}

	@Test
	void shouldGenerateUrlSafeBase64() {
		SecureStateGenerator generator = new SecureStateGenerator(32, false, '-');

		for (int i = 0; i < 10; i++) {
			State state = generator.generate();
			String value = state.getValue();

			// URL-safe base64 should not contain +, /, or =
			assertThat(value).doesNotContain("+");
			assertThat(value).doesNotContain("/");
			assertThat(value).doesNotContain("=");
		}
	}

	@Test
	void shouldHandleMalformedStateInExtractTimestamp() {
		SecureStateGenerator generator = new SecureStateGenerator();

		State malformedState = new State("no-timestamp-here");

		long timestamp = generator.extractTimestamp(malformedState);

		assertThat(timestamp).isEqualTo(-1);
	}

	@Test
	void shouldHandleStateWithMultipleSeparators() {
		SecureStateGenerator generator = new SecureStateGenerator();

		// Create a state with multiple separators (extract should get last one)
		State state = new State("random-value-1234567890");

		long timestamp = generator.extractTimestamp(state);

		assertThat(timestamp).isEqualTo(1234567890);
	}

	@Test
	void shouldTreatStateWithoutTimestampAsExpired() {
		SecureStateGenerator generator = new SecureStateGenerator(32, false, '-');

		State state = generator.generate();

		// State without timestamp should be considered expired for safety
		boolean expired = generator.isExpired(state, 10000);

		assertThat(expired).isTrue();
	}

	@Test
	void shouldHandleVeryOldState() {
		SecureStateGenerator generator = new SecureStateGenerator();

		// Create a state with very old timestamp
		State oldState = new State("randomvalue-1000000000");

		boolean expired = generator.isExpired(oldState, 60000); // 1 minute max age

		assertThat(expired).isTrue();
	}

	@Test
	void shouldGenerateConsistentLength() {
		SecureStateGenerator generator = new SecureStateGenerator(32, false, '-');

		Set<Integer> lengths = new HashSet<>();
		for (int i = 0; i < 10; i++) {
			State state = generator.generate();
			lengths.add(state.getValue().length());
		}

		// All states without timestamp should have same length
		assertThat(lengths).hasSize(1);
	}

	@Test
	void shouldHandleZeroMaxAge() {
		SecureStateGenerator generator = new SecureStateGenerator();

		State state = generator.generate();

		// Any state should be expired with 0 max age
		boolean expired = generator.isExpired(state, 0);

		assertThat(expired).isTrue();
	}

	@Test
	void shouldHandleNegativeMaxAge() {
		SecureStateGenerator generator = new SecureStateGenerator();

		State state = generator.generate();

		// Any state should be expired with negative max age
		boolean expired = generator.isExpired(state, -1000);

		assertThat(expired).isTrue();
	}
}
