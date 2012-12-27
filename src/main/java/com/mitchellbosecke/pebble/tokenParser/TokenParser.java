package com.mitchellbosecke.pebble.tokenParser;

import com.mitchellbosecke.pebble.lexer.Token;
import com.mitchellbosecke.pebble.node.Node;
import com.mitchellbosecke.pebble.parser.Parser;

public interface TokenParser {
	
	public Node parse(Token token);
	
	public String getTag();
	
	public void setParser(Parser parser);

}
