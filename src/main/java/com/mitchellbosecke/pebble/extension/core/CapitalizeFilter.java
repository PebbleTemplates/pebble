package com.mitchellbosecke.pebble.extension.core;

import java.util.List;
import java.util.Map;

import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.utils.StringUtils;

public class CapitalizeFilter implements Filter {

	@Override
	public List<String> getArgumentNames() {
		return null;
	}

	@Override
	public Object apply(Object input, Map<String, Object> args) {
		if (input == null) {
			return null;
		}
		String str = (String) input;
		return StringUtils.capitalize(str);
	}

}
