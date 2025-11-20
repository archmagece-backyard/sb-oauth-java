package org.scriptonbasestar.oauth.client.model;

import org.scriptonbasestar.oauth.client.util.Preconditions;

/**
 * @author archmagece
 * @since 2016-10-25 22
 */
public abstract class StringValueModel
		implements ValueModel {
	protected final String value;

	protected StringValueModel(String value) {
		Preconditions.notEmptyString(value, "value must not null or empty");
		this.value = value;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		StringValueModel that = (StringValueModel) o;
		return value.equals(that.value);
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public String toString() {
		return value;
	}
}
