package com.mitchellbosecke.pebble.macro;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.mitchellbosecke.pebble.extension.Filter;

public class TestFilter implements Filter {

	static int counter = 0;

	public static final String FILTER_NAME = "testfilter";

	@Override
	public List<String> getArgumentNames() {
		return Arrays.asList("content");
	}

	@Override
	public Object apply(Object input, Map<String, Object> args) {
		String content = (String) input;
		counter++;
		content = content + "?" + "Hello";
		return content;
	}

	public static int getCounter() {
		return counter;
	}

}
