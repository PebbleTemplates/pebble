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

import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.lexer.Token;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.node.Node;
import com.mitchellbosecke.pebble.node.NodeAutoEscape;
import com.mitchellbosecke.pebble.node.NodeBody;
import com.mitchellbosecke.pebble.parser.StoppingCondition;

public class AutoEscapeTokenParser extends AbstractTokenParser {

	@Override
	public Node parse(Token token) throws ParserException {
		TokenStream stream = this.parser.getStream();
		int lineNumber = token.getLineNumber();
		
		String strategy = null;
		boolean active = true;

		// skip over the 'autoescape' token
		stream.next();
		
		// did user specify active boolean?
		if(stream.current().test(Token.Type.NAME)){
			active = Boolean.parseBoolean(stream.current().getValue());
			stream.next();
		}
		
		// did user specify a strategy?
		if(stream.current().test(Token.Type.STRING)){
			strategy = stream.current().getValue();
			stream.next();
		}

		stream.expect(Token.Type.EXECUTE_END);

		// now we parse the block body
		NodeBody body = this.parser.subparse(new StoppingCondition() {
			@Override
			public boolean evaluate(Token token) {
				return token.test(Token.Type.NAME, "endautoescape");
			}
		});

		// skip the 'endautoescape' token
		stream.next();

		stream.expect(Token.Type.EXECUTE_END);

		return new NodeAutoEscape(lineNumber, body, active, strategy);
	}

	@Override
	public String getTag() {
		return "autoescape";
	}
}
