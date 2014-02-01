package com.mitchellbosecke.pebble.extension.core;

import java.util.Collection;
import java.util.Map;

import com.mitchellbosecke.pebble.extension.Test;
import com.mitchellbosecke.pebble.utils.StringUtils;

public class EmptyTest implements Test{

	@Override
	public boolean apply(Object input, Map<String, Object> args) {
		boolean isEmpty = input == null;

		if (!isEmpty && input instanceof String) {
			isEmpty = StringUtils.isBlank(((String) input));
		}

		if (!isEmpty && input instanceof Collection) {
			isEmpty = ((Collection<?>) input).isEmpty();
		}

		if (!isEmpty && input instanceof Map) {
			isEmpty = ((Map<?, ?>) input).isEmpty();
		}

		return isEmpty;
	}

}
