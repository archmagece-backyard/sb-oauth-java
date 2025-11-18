package org.scriptonbasestar.oauth.client.http;

import org.junit.jupiter.api.Test;
import org.scriptonbasestar.oauth.client.OAuth20Constants;
import org.scriptonbasestar.oauth.client.model.State;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for Param
 */
class ParamTest {

	@Test
	void shouldCreateParamWithKeyAndValues() {
		Param param = new Param("key", "value1", "value2");

		assertThat(param.getKey()).isEqualTo("key");
		assertThat(param.getValues()).containsExactly("value1", "value2");
	}

	@Test
	void shouldCreateParamWithSingleValue() {
		Param param = new Param("key", "value");

		assertThat(param.getKey()).isEqualTo("key");
		assertThat(param.getValues()).containsExactly("value");
	}

	@Test
	void shouldCreateParamWithOAuth20Constant() {
		Param param = new Param(OAuth20Constants.CLIENT_ID, "client123");

		assertThat(param.getKey()).isEqualTo(OAuth20Constants.CLIENT_ID.value);
		assertThat(param.getValues()).containsExactly("client123");
	}

	@Test
	void shouldThrowExceptionForEmptyKey() {
		assertThatThrownBy(() -> new Param("", "value"))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void shouldThrowExceptionForNullKey() {
		assertThatThrownBy(() -> new Param(null, "value"))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void shouldThrowExceptionForEmptyValues() {
		assertThatThrownBy(() -> new Param("key"))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void shouldCreateParamWithValueModel() {
		State state = new State("test-state");
		Param param = new Param("state", state);

		assertThat(param.getKey()).isEqualTo("state");
		assertThat(param.getValues()).containsExactly("test-state");
	}

	@Test
	void shouldCreateParamWithMultipleValueModels() {
		State state1 = new State("state1");
		State state2 = new State("state2");
		Param param = new Param("states", state1, state2);

		assertThat(param.getKey()).isEqualTo("states");
		assertThat(param.getValues()).containsExactly("state1", "state2");
	}

	@Test
	void shouldSupportEqualityComparison() {
		Param param1 = new Param("key", "value");
		Param param2 = new Param("key", "value");
		Param param3 = new Param("key", "different");

		assertThat(param1).isEqualTo(param2);
		assertThat(param1).isNotEqualTo(param3);
		assertThat(param1.hashCode()).isEqualTo(param2.hashCode());
	}

	@Test
	void shouldHandleSpecialCharactersInKey() {
		Param param = new Param("key@#$%", "value");

		assertThat(param.getKey()).isEqualTo("key@#$%");
	}

	@Test
	void shouldHandleSpecialCharactersInValue() {
		Param param = new Param("key", "value@#$%&*()");

		assertThat(param.getValues()).containsExactly("value@#$%&*()");
	}

	@Test
	void shouldHandleUnicodeCharacters() {
		Param param = new Param("키", "값");

		assertThat(param.getKey()).isEqualTo("키");
		assertThat(param.getValues()).containsExactly("값");
	}
}
