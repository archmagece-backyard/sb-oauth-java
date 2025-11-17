package org.scriptonbasestar.oauth.client.nobi.state;

import org.scriptonbasestar.oauth.client.model.State;

import java.util.stream.IntStream;

public class JsonStateGenerator implements StateGenerator {

	private final String[] keys;

	public JsonStateGenerator(String... keys) {
		this.keys = keys;
	}

	@Override
	public State generate(String... values) {
		if (keys.length != values.length) {
			throw new IllegalArgumentException("values must same length of keys");
		}

		String jsonContent = IntStream.range(0, keys.length)
				.mapToObj(i -> "\"%s\":\"%s\"".formatted(keys[i], values[i]))
				.reduce((a, b) -> a + "," + b)
				.orElse("");

		return new State("{" + jsonContent + "}");
	}
}
