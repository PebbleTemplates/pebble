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

import com.mitchellbosecke.pebble.parser.Parser;

public interface TokenParserBroker {

	public TokenParser getTokenParser(String tag);
	
	public void addTokenParser(TokenParser tokenParser);
	
	public void setParser(Parser parser);
	
	public Parser getParser();
}
