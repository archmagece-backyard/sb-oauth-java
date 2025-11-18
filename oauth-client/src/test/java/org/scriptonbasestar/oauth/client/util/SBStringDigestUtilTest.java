package org.scriptonbasestar.oauth.client.util;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for SBStringDigestUtil
 */
class SBStringDigestUtilTest {

	@Test
	void md5ShouldGenerateCorrectHash() {
		String input = "hello world";
		byte[] result = SBStringDigestUtil.md5(input);

		assertThat(result).isNotNull();
		assertThat(result).hasSize(16); // MD5 produces 128-bit (16-byte) hash

		// Verify hash is consistent
		byte[] result2 = SBStringDigestUtil.md5(input);
		assertThat(result).isEqualTo(result2);
	}

	@Test
	void md5ShouldProduceDifferentHashesForDifferentInputs() {
		byte[] hash1 = SBStringDigestUtil.md5("hello");
		byte[] hash2 = SBStringDigestUtil.md5("world");

		assertThat(hash1).isNotEqualTo(hash2);
	}

	@Test
	void sha1ShouldGenerateCorrectHash() {
		String input = "hello world";
		byte[] result = SBStringDigestUtil.sha1(input);

		assertThat(result).isNotNull();
		assertThat(result).hasSize(20); // SHA-1 produces 160-bit (20-byte) hash

		// Verify hash is consistent
		byte[] result2 = SBStringDigestUtil.sha1(input);
		assertThat(result).isEqualTo(result2);
	}

	@Test
	void sha1ShouldProduceDifferentHashesForDifferentInputs() {
		byte[] hash1 = SBStringDigestUtil.sha1("hello");
		byte[] hash2 = SBStringDigestUtil.sha1("world");

		assertThat(hash1).isNotEqualTo(hash2);
	}

	@Test
	void sha256ShouldGenerateCorrectHash() {
		String input = "hello world";
		byte[] result = SBStringDigestUtil.sha256(input);

		assertThat(result).isNotNull();
		assertThat(result).hasSize(32); // SHA-256 produces 256-bit (32-byte) hash

		// Verify hash is consistent
		byte[] result2 = SBStringDigestUtil.sha256(input);
		assertThat(result).isEqualTo(result2);
	}

	@Test
	void sha256ShouldProduceDifferentHashesForDifferentInputs() {
		byte[] hash1 = SBStringDigestUtil.sha256("hello");
		byte[] hash2 = SBStringDigestUtil.sha256("world");

		assertThat(hash1).isNotEqualTo(hash2);
	}

	@Test
	void shouldHandleEmptyString() {
		byte[] md5Result = SBStringDigestUtil.md5("");
		byte[] sha1Result = SBStringDigestUtil.sha1("");
		byte[] sha256Result = SBStringDigestUtil.sha256("");

		assertThat(md5Result).isNotNull().hasSize(16);
		assertThat(sha1Result).isNotNull().hasSize(20);
		assertThat(sha256Result).isNotNull().hasSize(32);
	}

	@Test
	void shouldHandleUnicodeCharacters() {
		String unicode = "你好世界 🌍";

		byte[] md5Result = SBStringDigestUtil.md5(unicode);
		byte[] sha1Result = SBStringDigestUtil.sha1(unicode);
		byte[] sha256Result = SBStringDigestUtil.sha256(unicode);

		assertThat(md5Result).isNotNull().hasSize(16);
		assertThat(sha1Result).isNotNull().hasSize(20);
		assertThat(sha256Result).isNotNull().hasSize(32);

		// Verify consistency
		assertThat(SBStringDigestUtil.md5(unicode)).isEqualTo(md5Result);
		assertThat(SBStringDigestUtil.sha1(unicode)).isEqualTo(sha1Result);
		assertThat(SBStringDigestUtil.sha256(unicode)).isEqualTo(sha256Result);
	}

	@Test
	void shouldHandleLongStrings() {
		String longString = "a".repeat(10000);

		byte[] md5Result = SBStringDigestUtil.md5(longString);
		byte[] sha1Result = SBStringDigestUtil.sha1(longString);
		byte[] sha256Result = SBStringDigestUtil.sha256(longString);

		assertThat(md5Result).isNotNull().hasSize(16);
		assertThat(sha1Result).isNotNull().hasSize(20);
		assertThat(sha256Result).isNotNull().hasSize(32);
	}
}
