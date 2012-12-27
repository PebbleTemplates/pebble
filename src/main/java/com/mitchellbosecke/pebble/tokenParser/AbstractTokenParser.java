package com.mitchellbosecke.pebble.tokenParser;

import com.mitchellbosecke.pebble.parser.Parser;


public abstract class AbstractTokenParser implements TokenParser{
	
	protected Parser parser;
	
	@Override
	public void setParser(Parser parser){
		this.parser = parser;
	}
	
}
