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
import com.mitchellbosecke.pebble.node.NodeBlock;
import com.mitchellbosecke.pebble.node.NodeBody;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionBlockReference;
import com.mitchellbosecke.pebble.utils.Function;

public class BlockTokenParser extends AbstractTokenParser {

	@Override
	public Node parse(Token token) throws ParserException {
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
		this.parser.pushBlockStack(name);

		stream.expect(Token.Type.EXECUTE_END);

		// now we parse the block body
		NodeBody blockBody = this.parser.subparse(new Function<Boolean, Token>() {
			@Override
			public Boolean execute(Token token) {
				return token.test(Token.Type.NAME, "endblock");
			}
		});
		
		// skip the 'endblock' token
		stream.next();
		
		// check if user included block name in endblock
		Token current = stream.current();
		if(current.test(Token.Type.NAME, name)){
			stream.next();
		}

		block.setBody(blockBody);
		this.parser.popBlockStack();
		
		stream.expect(Token.Type.EXECUTE_END);

		return new NodeExpressionBlockReference(stream.current().getLineNumber(), name, false);
	}

	@Override
	public String getTag() {
		return "block";
	}
}
