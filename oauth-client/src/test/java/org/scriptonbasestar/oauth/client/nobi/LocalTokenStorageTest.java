package org.scriptonbasestar.oauth.client.nobi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.scriptonbasestar.oauth.client.model.Token;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for LocalTokenStorage
 */
class LocalTokenStorageTest {

	private LocalTokenStorage storage;

	@BeforeEach
	void setUp() {
		storage = new LocalTokenStorage();
	}

	@Test
	void shouldStoreAndLoadToken() {
		String tokenId = "user123";
		Token token = new Token("access_token_value");

		storage.store(tokenId, token);
		Token loaded = storage.load(tokenId);

		assertThat(loaded).isNotNull();
		assertThat(loaded).isEqualTo(token);
	}

	@Test
	void shouldReturnNullForNonExistentToken() {
		Token loaded = storage.load("non_existent_id");

		assertThat(loaded).isNull();
	}

	@Test
	void shouldDropToken() {
		String tokenId = "user123";
		Token token = new Token("access_token_value");

		storage.store(tokenId, token);
		assertThat(storage.load(tokenId)).isNotNull();

		storage.drop(tokenId);
		assertThat(storage.load(tokenId)).isNull();
	}

	@Test
	void shouldOverwriteExistingToken() {
		String tokenId = "user123";
		Token token1 = new Token("access_token_1");
		Token token2 = new Token("access_token_2");

		storage.store(tokenId, token1);
		storage.store(tokenId, token2);

		Token loaded = storage.load(tokenId);
		assertThat(loaded).isEqualTo(token2);
		assertThat(loaded).isNotEqualTo(token1);
	}

	@Test
	void shouldHandleMultipleTokens() {
		Token token1 = new Token("access_token_1");
		Token token2 = new Token("access_token_2");
		Token token3 = new Token("access_token_3");

		storage.store("user1", token1);
		storage.store("user2", token2);
		storage.store("user3", token3);

		assertThat(storage.load("user1")).isEqualTo(token1);
		assertThat(storage.load("user2")).isEqualTo(token2);
		assertThat(storage.load("user3")).isEqualTo(token3);
	}

	@Test
	void shouldNotAffectOtherTokensWhenDropping() {
		Token token1 = new Token("access_token_1");
		Token token2 = new Token("access_token_2");

		storage.store("user1", token1);
		storage.store("user2", token2);

		storage.drop("user1");

		assertThat(storage.load("user1")).isNull();
		assertThat(storage.load("user2")).isNotNull();
		assertThat(storage.load("user2")).isEqualTo(token2);
	}

	@Test
	void dropShouldBeIdempotent() {
		String tokenId = "user123";
		Token token = new Token("access_token_value");

		storage.store(tokenId, token);
		storage.drop(tokenId);
		storage.drop(tokenId); // Drop again

		assertThat(storage.load(tokenId)).isNull();
	}

	@Test
	void shouldHandleNullTokenId() {
		Token token = new Token("access_token_value");

		// Store with null id - should not throw exception
		storage.store(null, token);
		Token loaded = storage.load(null);

		assertThat(loaded).isEqualTo(token);
	}

	@Test
	void shouldBeThreadSafe() throws InterruptedException {
		// Test concurrent access
		String tokenId = "user123";

		Thread writer = new Thread(() -> {
			for (int i = 0; i < 1000; i++) {
				storage.store(tokenId, new Token("token_" + i));
			}
		});

		Thread reader = new Thread(() -> {
			for (int i = 0; i < 1000; i++) {
				storage.load(tokenId);
			}
		});

		writer.start();
		reader.start();

		writer.join();
		reader.join();

		// Should not throw ConcurrentModificationException
		assertThat(storage.load(tokenId)).isNotNull();
	}
}
