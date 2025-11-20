package org.scriptonbasestar.oauth.client.o20.type;

import lombok.Getter;
import org.scriptonbasestar.oauth.client.model.ValueModel;

/**
 * @author archmagece
 * @since 2016-10-24
 */
//@AllArgsConstructor
public enum AccessTokenType
		implements ValueModel {

	BEARER("bearer"),
	MAC("mac");

	private final String value;

	AccessTokenType(String value) {
		this.value = value.toUpperCase();
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return value;
	}

}
