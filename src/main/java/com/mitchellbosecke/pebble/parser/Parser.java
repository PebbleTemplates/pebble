/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2012 Mitchell Bosecke.
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 
 * Unported License. To view a copy of this license, visit 
 * http://creativecommons.org/licenses/by-sa/3.0/
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

	public void setParentClassName(String parentClassName);
	
	public void setParentFileName(String parentFileName);

	public String getParentClassName();

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
