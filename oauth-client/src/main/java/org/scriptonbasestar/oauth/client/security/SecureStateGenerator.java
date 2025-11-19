package org.scriptonbasestar.oauth.client.security;

import org.scriptonbasestar.oauth.client.model.State;
import org.scriptonbasestar.oauth.client.nobi.state.StateGenerator;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Cryptographically secure state generator for OAuth CSRF protection.
 *
 * <p>This generator uses {@link SecureRandom} to generate unpredictable state values,
 * providing strong protection against CSRF attacks. The state includes a random component
 * and an optional timestamp for expiration validation.</p>
 *
 * <p>Generated state format: {base64_random}[-{timestamp}]</p>
 *
 * @since 1.0.0
 */
public class SecureStateGenerator implements StateGenerator {

	private static final int DEFAULT_RANDOM_BYTES = 32; // 256 bits
	private final SecureRandom secureRandom;
	private final int randomBytes;
	private final boolean includeTimestamp;
	private final char separator;

	/**
	 * Creates a secure state generator with default settings.
	 * - 32 bytes (256 bits) of random data
	 * - Timestamp included
	 * - Hyphen separator
	 */
	public SecureStateGenerator() {
		this(DEFAULT_RANDOM_BYTES, true, '-');
	}

	/**
	 * Creates a secure state generator with custom settings.
	 *
	 * @param randomBytes number of random bytes to generate (recommended: 32+)
	 * @param includeTimestamp whether to include timestamp for expiration checking
	 * @param separator character to separate random value and timestamp
	 */
	public SecureStateGenerator(int randomBytes, boolean includeTimestamp, char separator) {
		if (randomBytes < 16) {
			throw new IllegalArgumentException("Random bytes must be at least 16 for security");
		}
		this.randomBytes = randomBytes;
		this.includeTimestamp = includeTimestamp;
		this.separator = separator;
		this.secureRandom = new SecureRandom();
	}

	/**
	 * Generates a cryptographically secure state value.
	 *
	 * <p>The values parameter is ignored in this implementation as the state
	 * is generated entirely from secure random data.</p>
	 *
	 * @param values ignored parameter (for compatibility with StateGenerator interface)
	 * @return cryptographically secure State
	 */
	@Override
	public State generate(String... values) {
		byte[] randomData = new byte[randomBytes];
		secureRandom.nextBytes(randomData);

		String randomPart = Base64.getUrlEncoder().withoutPadding().encodeToString(randomData);

		if (includeTimestamp) {
			return new State(randomPart + separator + System.currentTimeMillis());
		} else {
			return new State(randomPart);
		}
	}

	/**
	 * Extracts the timestamp from a state value if present.
	 *
	 * @param state the state to extract timestamp from
	 * @return timestamp in milliseconds, or -1 if no timestamp present
	 */
	public long extractTimestamp(State state) {
		if (state == null || state.getValue() == null || !includeTimestamp) {
			return -1;
		}

		String value = state.getValue();
		int separatorIndex = value.lastIndexOf(separator);

		if (separatorIndex == -1) {
			return -1;
		}

		try {
			return Long.parseLong(value.substring(separatorIndex + 1));
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * Checks if a state has expired based on the maximum age in milliseconds.
	 *
	 * @param state the state to check
	 * @param maxAgeMillis maximum age in milliseconds
	 * @return true if the state has expired
	 */
	public boolean isExpired(State state, long maxAgeMillis) {
		long timestamp = extractTimestamp(state);
		if (timestamp == -1) {
			// If no timestamp, consider it expired for safety
			return true;
		}

		long currentTime = System.currentTimeMillis();
		long age = currentTime - timestamp;

		return age > maxAgeMillis;
	}

	/**
	 * Validates that a state is recent (not expired).
	 *
	 * @param state the state to validate
	 * @param maxAgeMillis maximum age in milliseconds
	 * @throws IllegalStateException if the state has expired
	 */
	public void validateNotExpired(State state, long maxAgeMillis) {
		if (isExpired(state, maxAgeMillis)) {
			throw new IllegalStateException("State has expired");
		}
	}

	/**
	 * Creates a secure state generator for production use.
	 * - 32 bytes of random data
	 * - Timestamp included for expiration checking
	 *
	 * @return production-ready secure state generator
	 */
	public static SecureStateGenerator forProduction() {
		return new SecureStateGenerator(32, true, '-');
	}

	/**
	 * Creates a secure state generator for development/testing.
	 * - 16 bytes of random data (faster)
	 * - Timestamp included
	 *
	 * @return development secure state generator
	 */
	public static SecureStateGenerator forDevelopment() {
		return new SecureStateGenerator(16, true, '-');
	}

	/**
	 * Gets the number of random bytes used.
	 *
	 * @return number of random bytes
	 */
	public int getRandomBytes() {
		return randomBytes;
	}

	/**
	 * Checks if timestamp is included in generated states.
	 *
	 * @return true if timestamp is included
	 */
	public boolean isIncludeTimestamp() {
		return includeTimestamp;
	}

	/**
	 * Gets the separator character used.
	 *
	 * @return separator character
	 */
	public char getSeparator() {
		return separator;
	}
}
