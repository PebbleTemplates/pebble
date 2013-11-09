/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Original work Copyright (c) 2009-2013 by the Twig Team
 * Modified work Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
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
