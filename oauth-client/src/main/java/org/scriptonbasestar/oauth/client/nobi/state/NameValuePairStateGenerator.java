package org.scriptonbasestar.oauth.client.nobi.state;

import org.scriptonbasestar.oauth.client.model.State;

import java.util.stream.IntStream;

public class NameValuePairStateGenerator implements StateGenerator {

	private final String[] keys;

	public NameValuePairStateGenerator(String... keys) {
		this.keys = keys;
	}

	@Override
	public State generate(String... values) {
		if (keys.length != values.length) {
			throw new IllegalArgumentException("values must same length of keys");
		}

		String content = IntStream.range(0, keys.length)
				.mapToObj(i -> keys[i] + "=" + values[i])
				.reduce((a, b) -> a + "&" + b)
				.orElse("");

		return new State(content);
	}
}
