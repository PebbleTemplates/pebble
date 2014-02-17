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

import com.mitchellbosecke.pebble.operator.BinaryOperator;
import com.mitchellbosecke.pebble.operator.UnaryOperator;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;

public interface Extension {

	/**
	 * Use this method to provide custom filters.
	 * 
	 * @return A list of filters. It is okay to return null.
	 */
	public Map<String, Filter> getFilters();

	/**
	 * Use this method to provide custom tests.
	 * 
	 * @return A list of tests. It is okay to return null.
	 */
	public Map<String, Test> getTests();

	/**
	 * Use this method to provide custom functions.
	 * 
	 * @return A list of functions. It is okay to return null.
	 */
	public Map<String, Function> getFunctions();

	/**
	 * Use this method to provide custom tags.
	 * 
	 * A TokenParser is used to parse a stream of tokens into Nodes which are
	 * then responsible for compiling themselves into Java.
	 * 
	 * @return A list of TokenParsers. It is okay to return null.
	 */
	public List<TokenParser> getTokenParsers();

	/**
	 * Use this method to provide custom binary operators.
	 * 
	 * @return A list of Operators. It is okay to return null;
	 */
	public List<BinaryOperator> getBinaryOperators();

	/**
	 * Use this method to provide custom unary operators.
	 * 
	 * @return A list of Operators. It is okay to return null;
	 */
	public List<UnaryOperator> getUnaryOperators();

	/**
	 * Use this method to provide variables available to all templates
	 * 
	 * @return Map<String,Object> global variables available to all templates
	 */
	public Map<String, Object> getGlobalVariables();

	/**
	 * Node visitors will travel the AST tree during the compilation phase.
	 * 
	 * @return
	 */
	public List<NodeVisitor> getNodeVisitors();
}
