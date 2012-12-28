/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2012 Mitchell Bosecke.
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 
 * Unported License. To view a copy of this license, visit 
 * http://creativecommons.org/licenses/by-sa/3.0/
 ******************************************************************************/
package com.mitchellbosecke.pebble.extension;

import java.util.List;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.filter.Filter;
import com.mitchellbosecke.pebble.parser.Operator;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;

public interface Extension {

	public void initRuntime(PebbleEngine engine);

	public List<Filter> getFilters();

	public List<TokenParser> getTokenParsers();
	
	public List<Operator> getBinaryOperators();
}
