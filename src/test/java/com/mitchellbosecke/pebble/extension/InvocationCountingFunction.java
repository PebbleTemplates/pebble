package com.mitchellbosecke.pebble.extension;

import java.util.List;
import java.util.Map;

import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

/**
 * This function will count how many times it's been invoked (just testing
 * purposes). Not thread safe.
 *
 * @author mbosecke
 *
 */
public class InvocationCountingFunction implements Function {

	private int invocationCount = 0;

	@Override
	public List<String> getArgumentNames() {
		return null;
	}

	@Override
	public Object execute(Map<String, Object> args, PebbleTemplateImpl self, int lineNumber) {
		return ++invocationCount;
	}

	public int getInvocationCount() {
		return invocationCount;
	}

}
