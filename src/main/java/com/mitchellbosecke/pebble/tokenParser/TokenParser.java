/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2012 Mitchell Bosecke.
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 
 * Unported License. To view a copy of this license, visit 
 * http://creativecommons.org/licenses/by-sa/3.0/
 ******************************************************************************/
package com.mitchellbosecke.pebble.tokenParser;

import com.mitchellbosecke.pebble.lexer.Token;
import com.mitchellbosecke.pebble.node.Node;
import com.mitchellbosecke.pebble.parser.Parser;

public interface TokenParser {
	
	public Node parse(Token token);
	
	public String getTag();
	
	public void setParser(Parser parser);

}
