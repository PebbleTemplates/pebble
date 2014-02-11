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

import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.node.NodeBlock;
import com.mitchellbosecke.pebble.node.NodeBody;
import com.mitchellbosecke.pebble.node.NodeExpression;
import com.mitchellbosecke.pebble.node.NodeMacro;
import com.mitchellbosecke.pebble.node.NodeRoot;

public interface Parser {

	public NodeRoot parse(TokenStream stream) throws ParserException;

	public NodeBody subparse() throws ParserException;

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
	public NodeBody subparse(StoppingCondition stopCondition) throws ParserException;

	public void setParentTemplateExpression(NodeExpression parentTemplateExpression);

	public ExpressionParser getExpressionParser();

	public void pushBlockStack(String name);

	public void popBlockStack();

	public String peekBlockStack();

	NodeExpression getParentTemplateExpression();

	public void addMacro(String name, NodeMacro macro) throws ParserException;

	public void addBlock(String name, NodeBlock block);

}
