package com.mitchellbosecke.pebble.extension.core;

import java.util.List;
import java.util.Map;

import com.mitchellbosecke.pebble.extension.Test;

public class IterableTest implements Test {

	@Override
	public List<String> getArgumentNames() {
		return null;
	}
	
	@Override
	public boolean apply(Object input, Map<String, Object> args) {

		return input instanceof Iterable;
	}
}
