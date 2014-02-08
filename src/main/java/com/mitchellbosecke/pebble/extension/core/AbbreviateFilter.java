package com.mitchellbosecke.pebble.extension.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mitchellbosecke.pebble.extension.Filter;

public class AbbreviateFilter implements Filter{

	@Override
	public List<String> getArgumentNames() {
		List<String> names = new ArrayList<>();
		names.add("length");
		return names;
	}

	@Override
	public Object apply(Object input, Map<String, Object> args) {
		if (input == null) {
			return null;
		}
		String value = (String) input;
		int maxWidth = (Integer) args.get("length");

		String ellipsis = "...";
		int length = value.length();

		if (length < maxWidth) {
			return value;
		}
		if (length <= 3) {
			return value;
		}
		return value.substring(0, maxWidth - 3) + ellipsis;
	}

}
