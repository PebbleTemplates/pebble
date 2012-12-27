package com.mitchellbosecke.pebble.tokenParser;

import com.mitchellbosecke.pebble.parser.Parser;

public interface TokenParserBroker {

	public TokenParser getTokenParser(String tag);
	
	public void addTokenParser(TokenParser tokenParser);
	
	public void setParser(Parser parser);
	
	public Parser getParser();
}
