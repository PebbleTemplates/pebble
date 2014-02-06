package com.mitchellbosecke.pebble.extension.core;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import com.mitchellbosecke.pebble.extension.Filter;

public class UrlEncoderFilter implements Filter {

	@Override
	public List<String> getArgumentNames() {
		return null;
	}

	public Object apply(Object input, Map<String, Object> args) {
		if (input == null) {
			return null;
		}
		String arg = (String) input;
		try {
			arg = URLEncoder.encode(arg, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		return arg;
	}

}
