package org.scripton.oauth.storage.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.scriptonbasestar.oauth.client.model.Token;
import redis.clients.jedis.Jedis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for RedisTokenStorage
 */
@ExtendWith(MockitoExtension.class)
class RedisTokenStorageTest {

	@Mock
	private Jedis jedis;

	private RedisTokenStorage storage;

	@BeforeEach
	void setUp() {
		storage = new RedisTokenStorage(jedis);
	}

	@Test
	void shouldStoreToken() {
		String tokenId = "user123";
		Token token = new Token("access_token_value");

		storage.store(tokenId, token);

		verify(jedis).set(tokenId, "access_token_value");
	}

	@Test
	void shouldLoadToken() {
		String tokenId = "user123";
		String tokenValue = "access_token_value";
		when(jedis.get(tokenId)).thenReturn(tokenValue);

		Token result = storage.load(tokenId);

		assertThat(result).isNotNull();
		assertThat(result.getValue()).isEqualTo(tokenValue);
		verify(jedis).get(tokenId);
	}

	@Test
	void shouldDropToken() {
		String tokenId = "user123";

		storage.drop(tokenId);

		verify(jedis).del(tokenId);
	}

	@Test
	void shouldHandleStoreWithLongTokenValue() {
		String tokenId = "user123";
		String longTokenValue = "a".repeat(1000);
		Token token = new Token(longTokenValue);

		storage.store(tokenId, token);

		verify(jedis).set(tokenId, longTokenValue);
	}

	@Test
	void shouldHandleLoadWhenTokenNotFound() {
		String tokenId = "nonexistent";
		when(jedis.get(tokenId)).thenReturn(null);

		Token result = storage.load(tokenId);

		assertThat(result).isNotNull();
		assertThat(result.getValue()).isNull();
	}

	@Test
	void shouldHandleStoreWithSpecialCharacters() {
		String tokenId = "user@example.com";
		Token token = new Token("token:with:special:chars");

		storage.store(tokenId, token);

		verify(jedis).set("user@example.com", "token:with:special:chars");
	}

	@Test
	void shouldHandleMultipleOperations() {
		String tokenId1 = "user1";
		String tokenId2 = "user2";
		Token token1 = new Token("token1");
		Token token2 = new Token("token2");

		storage.store(tokenId1, token1);
		storage.store(tokenId2, token2);
		storage.drop(tokenId1);

		verify(jedis).set(tokenId1, "token1");
		verify(jedis).set(tokenId2, "token2");
		verify(jedis).del(tokenId1);
	}

	@Test
	void shouldHandleUnicodeTokenValues() {
		String tokenId = "user123";
		String unicodeToken = "토큰값_한글_🔒";
		Token token = new Token(unicodeToken);

		storage.store(tokenId, token);

		verify(jedis).set(tokenId, unicodeToken);
	}

	@Test
	void shouldHandleEmptyTokenId() {
		String emptyId = "";
		Token token = new Token("token_value");

		storage.store(emptyId, token);

		verify(jedis).set("", "token_value");
	}

	@Test
	void shouldHandleLoadWithEmptyResult() {
		String tokenId = "user123";
		when(jedis.get(tokenId)).thenReturn("");

		Token result = storage.load(tokenId);

		assertThat(result).isNotNull();
		assertThat(result.getValue()).isEmpty();
	}
}
