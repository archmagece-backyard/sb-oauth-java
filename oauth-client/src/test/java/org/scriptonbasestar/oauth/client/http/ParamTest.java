package org.scriptonbasestar.oauth.client.http;

import org.junit.jupiter.api.Test;
import org.scriptonbasestar.oauth.client.OAuth20Constants;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test class for Param
 */
class ParamTest {

	@Test
	void constructor_withValidKeyAndValue_shouldCreateParam() {
		Param param = new Param("key", "value");
		assertThat(param.getKey()).isEqualTo("key");
		assertThat(param.getValues()).containsExactly("value");
	}

	@Test
	void constructor_withMultipleValues_shouldStoreAllValues() {
		Param param = new Param("key", "value1", "value2", "value3");
		assertThat(param.getKey()).isEqualTo("key");
		assertThat(param.getValues()).containsExactly("value1", "value2", "value3");
	}

	@Test
	void constructor_withOAuth20Constants_shouldCreateParam() {
		Param param = new Param(OAuth20Constants.CLIENT_ID, "my-client-id");
		assertThat(param.getKey()).isEqualTo("client_id");
		assertThat(param.getValues()).containsExactly("my-client-id");
	}

	@Test
	void constructor_withEmptyKey_shouldThrowException() {
		assertThatThrownBy(() -> new Param("", new String[]{"value"}))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Param.key will not empty");
	}

	@Test
	void constructor_withNullKey_shouldThrowException() {
		assertThatThrownBy(() -> new Param((String) null, "value"))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void constructor_withEmptyValues_shouldThrowException() {
		assertThatThrownBy(() -> new Param("key", new String[0]))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Param.value will not empty");
	}

	@Test
	void equals_withSameKeyAndValues_shouldReturnTrue() {
		Param param1 = new Param("key", "value1", "value2");
		Param param2 = new Param("key", "value1", "value2");
		assertThat(param1).isEqualTo(param2);
	}

	@Test
	void equals_withDifferentKey_shouldReturnFalse() {
		Param param1 = new Param("key1", "value");
		Param param2 = new Param("key2", "value");
		assertThat(param1).isNotEqualTo(param2);
	}

	@Test
	void equals_withDifferentValues_shouldReturnFalse() {
		Param param1 = new Param("key", "value1");
		Param param2 = new Param("key", "value2");
		assertThat(param1).isNotEqualTo(param2);
	}

	@Test
	void equals_withSameInstance_shouldReturnTrue() {
		Param param = new Param("key", "value");
		assertThat(param).isEqualTo(param);
	}

	@Test
	void equals_withNull_shouldReturnFalse() {
		Param param = new Param("key", "value");
		assertThat(param).isNotEqualTo(null);
	}

	@Test
	void hashCode_withSameKeyAndValues_shouldReturnSameHash() {
		Param param1 = new Param("key", "value1", "value2");
		Param param2 = new Param("key", "value1", "value2");
		assertThat(param1.hashCode()).isEqualTo(param2.hashCode());
	}

	@Test
	void hashCode_withDifferentParams_shouldReturnDifferentHash() {
		Param param1 = new Param("key1", "value");
		Param param2 = new Param("key2", "value");
		assertThat(param1.hashCode()).isNotEqualTo(param2.hashCode());
	}
}
