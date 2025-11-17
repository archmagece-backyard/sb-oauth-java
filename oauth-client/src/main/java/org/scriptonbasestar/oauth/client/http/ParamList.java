package org.scriptonbasestar.oauth.client.http;

import org.scriptonbasestar.oauth.client.OAuth20Constants;
import org.scriptonbasestar.oauth.client.model.ValueModel;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author archmagece
 * @since 2016-10-26 16
 */
public final class ParamList {
	private final Set<Param> paramSet = new LinkedHashSet<>();

	public ParamList(Param... params) {
		paramSet.addAll(Arrays.asList(params));
	}

	public ParamList(Collection<Param> params) {
		paramSet.addAll(params);
	}

	public static ParamList create(Param... params) {
		return new ParamList(params);
	}

	public ParamList add(String key, String... values) {
		paramSet.add(new Param(key, values));
		return this;
	}

	public ParamList add(OAuth20Constants key, String... values) {
		paramSet.add(new Param(key, values));
		return this;
	}

	public ParamList add(String key, ValueModel... values) {
		paramSet.add(new Param(key, values));
		return this;
	}

	public ParamList add(OAuth20Constants key, ValueModel... values) {
		paramSet.add(new Param(key, values));
		return this;
	}

	public ParamList add(Collection<Param> params) {
		paramSet.addAll(params);
		return this;
	}

	public ParamList add(Param... params) {
		paramSet.addAll(Arrays.asList(params));
		return this;
	}

	public Set<Param> paramSet() {
		return paramSet;
	}

}
