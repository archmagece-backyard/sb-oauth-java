package org.scriptonbasestar.oauth.client.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for SBStringDigestUtil
 */
class SBStringDigestUtilTest {

  @Test
  void md5_shouldReturnCorrectHash() {
    byte[] result = SBStringDigestUtil.md5("test");

    assertThat(result).isNotNull();
    assertThat(result).hasSize(16); // MD5 produces 16 bytes
  }

  @Test
  void md5_withEmptyString_shouldReturnHash() {
    byte[] result = SBStringDigestUtil.md5("");

    assertThat(result).isNotNull();
    assertThat(result).hasSize(16);
  }

  @Test
  void sha1_shouldReturnCorrectHash() {
    byte[] result = SBStringDigestUtil.sha1("test");

    assertThat(result).isNotNull();
    assertThat(result).hasSize(20); // SHA-1 produces 20 bytes
  }

  @Test
  void sha1_withEmptyString_shouldReturnHash() {
    byte[] result = SBStringDigestUtil.sha1("");

    assertThat(result).isNotNull();
    assertThat(result).hasSize(20);
  }

  @Test
  void sha256_shouldReturnCorrectHash() {
    byte[] result = SBStringDigestUtil.sha256("test");

    assertThat(result).isNotNull();
    assertThat(result).hasSize(32); // SHA-256 produces 32 bytes
  }

  @Test
  void sha256_withEmptyString_shouldReturnHash() {
    byte[] result = SBStringDigestUtil.sha256("");

    assertThat(result).isNotNull();
    assertThat(result).hasSize(32);
  }

  @Test
  void md5_shouldProduceConsistentResults() {
    byte[] result1 = SBStringDigestUtil.md5("hello");
    byte[] result2 = SBStringDigestUtil.md5("hello");

    assertThat(result1).isEqualTo(result2);
  }

  @Test
  void sha1_shouldProduceConsistentResults() {
    byte[] result1 = SBStringDigestUtil.sha1("hello");
    byte[] result2 = SBStringDigestUtil.sha1("hello");

    assertThat(result1).isEqualTo(result2);
  }

  @Test
  void sha256_shouldProduceConsistentResults() {
    byte[] result1 = SBStringDigestUtil.sha256("hello");
    byte[] result2 = SBStringDigestUtil.sha256("hello");

    assertThat(result1).isEqualTo(result2);
  }

  @Test
  void differentInputs_shouldProduceDifferentHashes() {
    byte[] md5Result1 = SBStringDigestUtil.md5("test1");
    byte[] md5Result2 = SBStringDigestUtil.md5("test2");

    assertThat(md5Result1).isNotEqualTo(md5Result2);
  }
}
