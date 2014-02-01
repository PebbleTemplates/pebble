package com.mitchellbosecke.pebble.extension.core;

import java.util.Map;

import com.mitchellbosecke.pebble.extension.Test;

public class OddTest implements Test {

	@Override
	public boolean apply(Object input, Map<String, Object> args) {
		if (input == null) {
			throw new IllegalArgumentException("Can not pass null value to \"odd\" test.");
		}
		EvenTest evenTest = new EvenTest();
		return evenTest.apply(input, args) == false;
	}
}
