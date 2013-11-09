/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Original work Copyright (c) 2009-2013 by the Twig Team
 * Modified work Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.extension;

import java.util.List;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.filter.Filter;
import com.mitchellbosecke.pebble.parser.Operator;
import com.mitchellbosecke.pebble.test.Test;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;

public abstract class AbstractExtension implements Extension {

	@Override
	public void initRuntime(PebbleEngine engine) {

	}

	@Override
	public List<TokenParser> getTokenParsers() {
		return null;
	}
	
	@Override
	public List<Operator> getBinaryOperators(){
		return null;
	}
	
	@Override
	public List<Operator> getUnaryOperators(){
		return null;
	}
	
	@Override
	public List<Filter> getFilters(){
		return null;
	}
	
	@Override
	public List<Test> getTests(){
		return null;
	}

}
