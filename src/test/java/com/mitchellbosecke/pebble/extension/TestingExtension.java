package com.mitchellbosecke.pebble.extension;

import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;

@Ignore
public class TestingExtension extends AbstractExtension {

	private InvocationCountingFunction invocationCountingFunction = new InvocationCountingFunction();

	@Override
	public Map<String, Function> getFunctions() {
		Map<String, Function> functions = new HashMap<>();
		functions.put("invocationCountingFunction", invocationCountingFunction);
		return functions;
	}

	public InvocationCountingFunction getInvocationCountingFunction() {
		return invocationCountingFunction;
	}

}
