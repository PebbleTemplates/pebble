package com.mitchellbosecke.pebble.extension.core;

import java.util.Map;

import com.mitchellbosecke.pebble.extension.Filter;

public class TrimFilter implements Filter {

	@Override
	public Object apply(Object input, Map<String, Object> args) {
		if (input == null) {
			return null;
		}
		String str = (String) input;
		return str.trim();
	}

}
