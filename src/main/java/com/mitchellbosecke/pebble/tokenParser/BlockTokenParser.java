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
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.node.Node;
import com.mitchellbosecke.pebble.node.NodeBlock;
import com.mitchellbosecke.pebble.node.NodeBody;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionBlockReference;
import com.mitchellbosecke.pebble.utils.Command;

public class BlockTokenParser extends AbstractTokenParser {

	@Override
	public Node parse(Token token) {
		TokenStream stream = this.parser.getStream();
		int lineNumber = token.getLineNumber();
		
		// skip over the 'block' token
		stream.next();

		// expect a name for the new block
		Token blockName = stream.expect(Token.Type.NAME);

		// get the name of the new block
		String name = blockName.getValue();

		NodeBlock block = new NodeBlock(lineNumber, name);

		this.parser.setBlock(name, block);
		// this.parser.setLocalScope();
		this.parser.pushBlockStack(name);

		stream.expect(Token.Type.BLOCK_END);

		// now we parse the block body
		NodeBody blockBody = this.parser.subparse(new Command<Boolean, Token>() {
			@Override
			public Boolean execute(Token token) {
				return token.test(Token.Type.NAME, "endblock");
			}
		});
		
		// skip the 'endblock' token
		stream.next();
		
		// check for a proper endblock name
		stream.expect(Token.Type.NAME, name, String.format("Unexpected endblock. Expected '%s' but was given '%s'.",
				name, stream.current().getValue()));

		block.setBody(blockBody);
		this.parser.popBlockStack();
		// this.parser.popLocalScope();
		
		stream.expect(Token.Type.BLOCK_END);

		return new NodeExpressionBlockReference(stream.current().getLineNumber(), name, false);
	}

	@Override
	public String getTag() {
		return "block";
	}
}
