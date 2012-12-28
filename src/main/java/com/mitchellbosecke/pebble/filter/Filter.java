package com.mitchellbosecke.pebble.filter;

import java.util.List;

public interface Filter {

	public String getTag();
	
	public Object apply(List<Object> args);
}
