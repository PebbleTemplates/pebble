/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Original work Copyright (c) 2009-2013 by the Twig Team
 * Modified work Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.parser;

import java.util.Set;

import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.node.BodyNode;
import com.mitchellbosecke.pebble.node.RootNode;
import com.mitchellbosecke.pebble.template.Macro;

public interface Parser {

	public RootNode parse(TokenStream stream) throws ParserException;

	public BodyNode subparse() throws ParserException;

	/**
	 * Provides the stream of tokens which ultimately need to be "parsed" into
	 * Nodes.
	 * 
	 * @return TokenStream
	 */
	public TokenStream getStream();

	/**
	 * Parses the existing TokenStream, starting at the current Token, and
	 * ending when the stopCondition is fullfilled.
	 * 
	 * @param stopCondition
	 * @return
	 * @throws ParserException
	 */
	public BodyNode subparse(StoppingCondition stopCondition) throws ParserException;

	public ExpressionParser getExpressionParser();

	public String peekBlockStack();

	public String popBlockStack();

	public void pushBlockStack(String blockName);

	public void addMacro(Macro macro);
	
	public Set<Macro> getMacros();

}
