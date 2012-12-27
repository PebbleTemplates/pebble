package com.mitchellbosecke.pebble.extension;

import java.util.List;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;
import com.mitchellbosecke.pebble.utils.Operator;

public interface Extension {

	public void initRuntime(PebbleEngine engine);

	// public List<Filter> getFilters();

	public List<TokenParser> getTokenParsers();
	
	public List<Operator> getBinaryOperators();
}
