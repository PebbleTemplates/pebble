/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.tokenParser;

import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.lexer.Token;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.node.BlockNode;
import com.mitchellbosecke.pebble.node.BodyNode;
import com.mitchellbosecke.pebble.node.RenderableNode;
import com.mitchellbosecke.pebble.parser.StoppingCondition;

public class BlockTokenParser extends AbstractTokenParser {

	@Override
	public RenderableNode parse(Token token) throws ParserException {
		TokenStream stream = this.parser.getStream();
		int lineNumber = token.getLineNumber();

		// skip over the 'block' token
		stream.next();

		// expect a name for the new block
		Token blockName = stream.expect(Token.Type.NAME);

		// get the name of the new block
		String name = blockName.getValue();

		stream.expect(Token.Type.EXECUTE_END);

		this.parser.pushBlockStack(name);
		// now we parse the block body
		BodyNode blockBody = this.parser.subparse(new StoppingCondition() {
			@Override
			public boolean evaluate(Token token) {
				return token.test(Token.Type.NAME, "endblock");
			}
		});
		this.parser.popBlockStack();

		// skip the 'endblock' token
		stream.next();

		// check if user included block name in endblock
		Token current = stream.current();
		if (current.test(Token.Type.NAME, name)) {
			stream.next();
		}

		stream.expect(Token.Type.EXECUTE_END);
		return new BlockNode(lineNumber, name, blockBody);
	}

	@Override
	public String getTag() {
		return "block";
	}
}
