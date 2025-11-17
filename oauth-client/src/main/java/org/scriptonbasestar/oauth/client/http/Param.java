package org.scriptonbasestar.oauth.client.http;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.scriptonbasestar.oauth.client.OAuth20Constants;
import org.scriptonbasestar.oauth.client.model.ValueModel;
import org.scriptonbasestar.oauth.client.util.Preconditions;

import java.util.Arrays;

/**
 * @author archmagece
 * @since 2016-10-25
 * <p>
 * TODO generic.
 * TODO ParamUtil - ParamList 안으로
 * TODO
 */
@Getter
@EqualsAndHashCode
public final class Param {
	protected String key;
	protected String[] values;

	public Param(String key, String... values) {
		Preconditions.notEmptyString(key, "Param.key will not empty");
		Preconditions.notEmpty(values, "Param.value will not empty");
		this.key = key;
		this.values = values;
	}

	public Param(OAuth20Constants key, String... values) {
		this(key.value, values);
	}

	public Param(String key, ValueModel... values) {
		this(key, modelToStringArray(values));
	}

	public Param(OAuth20Constants key, ValueModel... values) {
		this(key.value, modelToStringArray(values));
	}

	private static String[] modelToStringArray(ValueModel... value) {
		return Arrays.stream(value)
				.map(ValueModel::getValue)
				.toArray(String[]::new);
	}

}
