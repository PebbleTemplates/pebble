package com.mitchellbosecke.pebble.test;

import java.util.List;

public interface Test {

	public String getTag();
	
	public Boolean apply(List<Object> args);
}
