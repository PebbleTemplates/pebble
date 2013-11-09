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

import java.util.List;
import java.util.Map;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.SyntaxException;
import com.mitchellbosecke.pebble.lexer.Token;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.node.NodeBlock;
import com.mitchellbosecke.pebble.node.NodeBody;
import com.mitchellbosecke.pebble.node.NodeMacro;
import com.mitchellbosecke.pebble.node.NodeRoot;
import com.mitchellbosecke.pebble.utils.Command;

public interface Parser {

	public NodeRoot parse(TokenStream stream) throws SyntaxException;

	public NodeBody subparse() throws SyntaxException;

	public TokenStream getStream();
	
	public void setParentFileName(String parentFileName);

	public ExpressionParser getExpressionParser();

	public void setBlock(String name, NodeBlock block);

	public boolean hasBlock(String name);

	public void pushBlockStack(String name);

	public void popBlockStack();
	
	public String peekBlockStack();

	public PebbleEngine getEngine();

	public NodeBody subparse(Command<Boolean, Token> stopCondition) throws SyntaxException;

	String getParentFileName();

	Map<String, NodeBlock> getBlocks();

	void setBlocks(Map<String, NodeBlock> blocks);

	Map<String,  List<NodeMacro>> getMacros();
	
	public void addMacro(String name, NodeMacro macro);

}
