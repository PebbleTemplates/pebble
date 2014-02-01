package com.mitchellbosecke.pebble.extension.core;

import java.util.Map;

import com.mitchellbosecke.pebble.extension.Test;

public class NullTest implements Test {

	@Override
	public boolean apply(Object input, Map<String, Object> args) {
		return input == null;
	}
}
