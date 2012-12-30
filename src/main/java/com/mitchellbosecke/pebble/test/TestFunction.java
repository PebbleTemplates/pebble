package com.mitchellbosecke.pebble.test;

import java.util.List;

import com.mitchellbosecke.pebble.utils.Command;

public class TestFunction implements Test {

	private final String tag;

	private final Command<Boolean, List<Object>> function;

	public TestFunction(String tag, Command<Boolean, List<Object>> function) {
		this.tag = tag;
		this.function = function;
	}

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public Boolean apply(List<Object> args) {
		return function.execute(args);
	}

}
