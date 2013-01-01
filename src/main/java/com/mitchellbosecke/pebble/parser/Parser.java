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

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.lexer.Token;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.node.NodeBlock;
import com.mitchellbosecke.pebble.node.NodeBody;
import com.mitchellbosecke.pebble.node.NodeMacro;
import com.mitchellbosecke.pebble.node.NodeRoot;
import com.mitchellbosecke.pebble.utils.Command;

public interface Parser {

	public NodeRoot parse(TokenStream stream);

	public NodeBody subparse();

	public TokenStream getStream();

	public void setParentClassName(String parentClassName);
	
	public void setParentFileName(String parentFileName);

	public String getParentClassName();

	public ExpressionParser getExpressionParser();

	public void setBlock(String name, NodeBlock block);

	public void setMacro(String name, NodeMacro macro);

	public boolean hasBlock(String name);

	public void pushBlockStack(String name);

	public void popBlockStack();

	public PebbleEngine getEngine();

	public NodeBody subparse(Command<Boolean, Token> stopCondition);

}
