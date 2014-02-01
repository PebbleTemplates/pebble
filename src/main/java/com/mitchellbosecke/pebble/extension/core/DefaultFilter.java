package com.mitchellbosecke.pebble.extension.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.extension.NamedArguments;
import com.mitchellbosecke.pebble.extension.Test;

public class DefaultFilter implements Filter, NamedArguments {

	@Override
	public List<String> getArgumentNames() {
		List<String> names = new ArrayList<>();
		names.add("default");
		return names;
	}

	@Override
	public Object apply(Object input, Map<String, Object> args) {

		Object defaultObj = args.get("default");

		Test emptyTest = new EmptyTest();
		if (emptyTest.apply(input, new HashMap<String, Object>())) {
			return defaultObj;
		}
		return input;
	}

}
