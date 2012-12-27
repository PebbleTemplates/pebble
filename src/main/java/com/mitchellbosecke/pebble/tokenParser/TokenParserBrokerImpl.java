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

import java.util.HashMap;
import java.util.Map;

import com.mitchellbosecke.pebble.parser.Parser;

public class TokenParserBrokerImpl implements TokenParserBroker {
	
	protected Parser parser;
	protected Map<String, TokenParser> tokenParsers = new HashMap<>();
	
	
	@Override
	public void addTokenParser(TokenParser tokenParser){
		this.tokenParsers.put(tokenParser.getTag(), tokenParser);
	}

	public void removeTokenParser(TokenParser tokenParser){
		this.tokenParsers.remove(tokenParser.getTag());
	}

	@Override
	public TokenParser getTokenParser(String tag) {
		return tokenParsers.get(tag);
	}

	@Override
	public void setParser(Parser parser) {
		this.parser = parser;
		for(TokenParser tokenParser : tokenParsers.values()){
			tokenParser.setParser(parser);
		}

	}

	@Override
	public Parser getParser() {
		return parser;
	}

}
