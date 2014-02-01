package com.mitchellbosecke.pebble.extension.core;

import java.util.Map;

import com.mitchellbosecke.pebble.extension.SimpleFunction;
import com.mitchellbosecke.pebble.utils.OperatorUtils;

public class MinFunction implements SimpleFunction {

	@Override
	public Object execute(Map<String,Object> args) {
		Object min = null;
		for (Object candidate : args.values()) {
			if (min == null) {
				min = candidate;
				continue;
			}
			if (OperatorUtils.lt(candidate, min)) {
				min = candidate;
			}
		}
		return min;
	}

}
