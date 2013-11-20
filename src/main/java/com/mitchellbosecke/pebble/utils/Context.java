package com.mitchellbosecke.pebble.utils;

import java.util.HashMap;

public class Context extends HashMap<String, Object> {

	private static final long serialVersionUID = -7044995933897288856L;

	public final boolean strictVariables;

	public Context(boolean strictVariables) {
		this.strictVariables = strictVariables;
	}

	@Override
	public Object get(Object key) {
		if (strictVariables && !super.containsKey(key)) {
			throw new RuntimeException(
					String.format(
							"Variable [%s] does not exist and strict variables is set to true.",
							String.valueOf(key)));
		}
		return super.get(key);
	}

}
