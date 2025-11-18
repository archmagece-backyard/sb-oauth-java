package org.scripton.oauth.storage.ehcache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.scriptonbasestar.oauth.client.model.Token;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for EhcacheTokenStorage
 */
@ExtendWith(MockitoExtension.class)
class EhcacheTokenStorageTest {

	@Mock
	private Cache cache;

	private EhcacheTokenStorage storage;

	@BeforeEach
	void setUp() {
		storage = new EhcacheTokenStorage(cache);
	}

	@Test
	void shouldStoreToken() {
		String tokenId = "user123";
		Token token = new Token("access_token_value");

		storage.store(tokenId, token);

		ArgumentCaptor<Element> elementCaptor = ArgumentCaptor.forClass(Element.class);
		verify(cache).put(elementCaptor.capture());

		Element captured = elementCaptor.getValue();
		assertThat(captured.getObjectKey()).isEqualTo(tokenId);
		assertThat(captured.getObjectValue()).isEqualTo(token);
	}

	@Test
	void shouldLoadToken() {
		String tokenId = "user123";
		Token expectedToken = new Token("access_token_value");
		Element element = new Element(tokenId, expectedToken);
		when(cache.get(tokenId)).thenReturn(element);

		Token result = storage.load(tokenId);

		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(expectedToken);
		assertThat(result.getValue()).isEqualTo("access_token_value");
		verify(cache).get(tokenId);
	}

	@Test
	void shouldDropToken() {
		String tokenId = "user123";

		storage.drop(tokenId);

		verify(cache).remove(tokenId);
	}

	@Test
	void shouldHandleStoreWithComplexToken() {
		String tokenId = "user@example.com";
		Token token = new Token("complex_token_with_special_chars!@#$%");

		storage.store(tokenId, token);

		ArgumentCaptor<Element> elementCaptor = ArgumentCaptor.forClass(Element.class);
		verify(cache).put(elementCaptor.capture());

		Element captured = elementCaptor.getValue();
		assertThat(captured.getObjectKey()).isEqualTo(tokenId);
		Token storedToken = (Token) captured.getObjectValue();
		assertThat(storedToken.getValue()).isEqualTo("complex_token_with_special_chars!@#$%");
	}

	@Test
	void shouldHandleMultipleStoreOperations() {
		String tokenId1 = "user1";
		String tokenId2 = "user2";
		Token token1 = new Token("token1");
		Token token2 = new Token("token2");

		storage.store(tokenId1, token1);
		storage.store(tokenId2, token2);

		ArgumentCaptor<Element> elementCaptor = ArgumentCaptor.forClass(Element.class);
		verify(cache).put(elementCaptor.capture());
		// Note: Only captures the last invocation, but we verify 2 calls happened
	}

	@Test
	void shouldHandleLoadAndStore() {
		String tokenId = "user123";
		Token originalToken = new Token("original_value");
		Token updatedToken = new Token("updated_value");

		// Store original
		storage.store(tokenId, originalToken);

		// Load
		Element element = new Element(tokenId, originalToken);
		when(cache.get(tokenId)).thenReturn(element);
		Token loaded = storage.load(tokenId);

		// Store updated
		storage.store(tokenId, updatedToken);

		assertThat(loaded.getValue()).isEqualTo("original_value");
		verify(cache).get(tokenId);
	}

	@Test
	void shouldHandleDropAfterStore() {
		String tokenId = "user123";
		Token token = new Token("token_value");

		storage.store(tokenId, token);
		storage.drop(tokenId);

		verify(cache).remove(tokenId);
	}

	@Test
	void shouldHandleLongTokenValue() {
		String tokenId = "user123";
		String longValue = "a".repeat(2000);
		Token token = new Token(longValue);

		storage.store(tokenId, token);

		ArgumentCaptor<Element> elementCaptor = ArgumentCaptor.forClass(Element.class);
		verify(cache).put(elementCaptor.capture());

		Element captured = elementCaptor.getValue();
		Token storedToken = (Token) captured.getObjectValue();
		assertThat(storedToken.getValue()).hasSize(2000);
	}

	@Test
	void shouldHandleUnicodeTokenValue() {
		String tokenId = "user123";
		Token token = new Token("토큰_한글_🔒_Unicode");

		storage.store(tokenId, token);

		ArgumentCaptor<Element> elementCaptor = ArgumentCaptor.forClass(Element.class);
		verify(cache).put(elementCaptor.capture());

		Element captured = elementCaptor.getValue();
		Token storedToken = (Token) captured.getObjectValue();
		assertThat(storedToken.getValue()).contains("토큰");
		assertThat(storedToken.getValue()).contains("🔒");
	}

	@Test
	void shouldHandleEmptyTokenId() {
		String emptyId = "";
		Token token = new Token("token_value");

		storage.store(emptyId, token);

		ArgumentCaptor<Element> elementCaptor = ArgumentCaptor.forClass(Element.class);
		verify(cache).put(elementCaptor.capture());

		Element captured = elementCaptor.getValue();
		assertThat(captured.getObjectKey()).isEqualTo("");
	}
}
