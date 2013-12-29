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

import com.mitchellbosecke.pebble.error.SyntaxException;
import com.mitchellbosecke.pebble.lexer.Token;
import com.mitchellbosecke.pebble.node.Node;
import com.mitchellbosecke.pebble.parser.Parser;

public interface TokenParser {

	/**
	 * The "tag" is used to determine when to use a particular instance of a
	 * TokenParser. For example, the TokenParser that handles the "block" tag
	 * would return "block" with this method.
	 * 
	 * @return The tag used to define this TokenParser.
	 */
	public String getTag();

	/**
	 * Each TokenParser instance will have access to the primary Pebble Parser
	 * before the parse(Token token) method is invoked.
	 * 
	 * The primary parser will provide the TokenStream which a TokenParser
	 * will require.
	 * 
	 * @param parser
	 */
	public void setParser(Parser parser);

	/**
	 * The TokenParser is responsible to convert all the necessary tokens
	 * into appropriate Nodes. It can access tokens using parser.getTokenStream().
	 * 
	 * 
	 * 
	 * @param token
	 * @return
	 * @throws SyntaxException
	 */
	public Node parse(Token token) throws SyntaxException;

}
