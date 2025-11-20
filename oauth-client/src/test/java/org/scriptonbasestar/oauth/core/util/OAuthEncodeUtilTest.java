package org.scriptonbasestar.oauth.core.util;

import org.junit.jupiter.api.Test;
import org.scriptonbasestar.oauth.client.util.OAuthEncodeUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author archmagece
 * @since 2016-10-26 14
 */
class OAuthEncodeUtilTest {

	@Test
	void encode_withSimpleString_shouldReturnSameString() {
		String result = OAuthEncodeUtil.encode("test");
		assertThat(result).isEqualTo("test");
	}

	@Test
	void encode_withSpaces_shouldEncodeSpaces() {
		String result = OAuthEncodeUtil.encode("hello world");
		assertThat(result).isEqualTo("hello+world");
	}

	@Test
	void encode_withSpecialCharacters_shouldEncodeSpecialChars() {
		String result = OAuthEncodeUtil.encode("test@example.com");
		assertThat(result).isEqualTo("test%40example.com");
	}

	@Test
	void encode_withKoreanCharacters_shouldEncodeKorean() {
		String result = OAuthEncodeUtil.encode("테스트");
		assertThat(result).isNotEmpty();
		assertThat(result).contains("%");
	}

	@Test
	void encode_withMultipleSpecialChars_shouldEncodeAll() {
		String result = OAuthEncodeUtil.encode("a=b&c=d");
		assertThat(result).isEqualTo("a%3Db%26c%3Dd");
	}

	@Test
	void encodeArray_withSimpleStrings_shouldReturnSameArray() {
		String[] input = {"test1", "test2", "test3"};
		String[] result = OAuthEncodeUtil.encodeArray(input);
		assertThat(result).containsExactly("test1", "test2", "test3");
	}

	@Test
	void encodeArray_withSpecialCharacters_shouldEncodeAll() {
		String[] input = {"hello world", "test@example.com", "a=b"};
		String[] result = OAuthEncodeUtil.encodeArray(input);
		assertThat(result).containsExactly("hello+world", "test%40example.com", "a%3Db");
	}

	@Test
	void encodeArray_withEmptyArray_shouldReturnEmptyArray() {
		String[] input = new String[0];
		String[] result = OAuthEncodeUtil.encodeArray(input);
		assertThat(result).isEmpty();
	}
}
