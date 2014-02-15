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
import java.util.Map;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.operator.BinaryOperator;
import com.mitchellbosecke.pebble.operator.UnaryOperator;
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
	public List<BinaryOperator> getBinaryOperators(){
		return null;
	}
	
	@Override
	public List<UnaryOperator> getUnaryOperators(){
		return null;
	}
	
	@Override
	public Map<String,Filter> getFilters(){
		return null;
	}
	
	@Override
	public Map<String, Test> getTests(){
		return null;
	}
	
	@Override
	public Map<String, Function> getFunctions(){
		return null;
	}

	@Override
	public Map<String,Object> getGlobalVariables(){
		return null;
	}
	
	@Override
	public List<NodeVisitor> getNodeVisitors(){
		return null;
	}
}
