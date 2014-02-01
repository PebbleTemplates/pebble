package com.mitchellbosecke.pebble.extension.core;

import java.util.Map;

import com.mitchellbosecke.pebble.extension.Test;

public class EvenTest implements Test {

	@Override
	public boolean apply(Object input, Map<String,Object> args) {
		if (input == null) {
			throw new IllegalArgumentException("Can not pass null value to \"even\" test.");
		}

		Integer obj = (Integer) input;
		return (obj % 2 == 0);
	}
}
