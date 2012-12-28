package com.mitchellbosecke.pebble.filter;

import java.util.List;

import com.mitchellbosecke.pebble.utils.Command;

public class FilterFunction implements Filter {

	private final String tag;

	private final Command<Object, List<Object>> function;

	public FilterFunction(String tag, Command<Object, List<Object>> function) {
		this.tag = tag;
		this.function = function;
	}

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public Object apply(List<Object> args) {
		return function.execute(args);
	}

}
