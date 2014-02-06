package com.mitchellbosecke.pebble.extension.core;

import java.util.List;
import java.util.Map;

import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.utils.OperatorUtils;

public class MaxFunction implements Function {

	@Override
	public List<String> getArgumentNames() {
		return null;
	}

	@Override
	public Object execute(Map<String, Object> args) {
		Object min = null;
		for (Object candidate : args.values()) {
			if (min == null) {
				min = candidate;
				continue;
			}
			if (OperatorUtils.gt(candidate, min)) {
				min = candidate;
			}
		}
		return min;

	}

}
