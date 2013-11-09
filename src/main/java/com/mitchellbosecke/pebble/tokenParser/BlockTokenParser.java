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
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.node.Node;
import com.mitchellbosecke.pebble.node.NodeBlock;
import com.mitchellbosecke.pebble.node.NodeBody;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionBlockReference;
import com.mitchellbosecke.pebble.utils.Command;

public class BlockTokenParser extends AbstractTokenParser {

	@Override
	public Node parse(Token token) throws SyntaxException {
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
