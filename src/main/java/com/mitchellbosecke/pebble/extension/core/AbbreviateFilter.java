package com.mitchellbosecke.pebble.extension.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.utils.StringUtils;

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
		String str = (String) input;
		int maxWidth = (Integer) args.get("length");

		return StringUtils.abbreviate(str, maxWidth);
	}

}
