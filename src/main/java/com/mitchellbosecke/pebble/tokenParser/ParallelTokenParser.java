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
import com.mitchellbosecke.pebble.node.NodeBody;
import com.mitchellbosecke.pebble.node.NodeParallel;
import com.mitchellbosecke.pebble.utils.Function;

public class ParallelTokenParser extends AbstractTokenParser {

	@Override
	public Node parse(Token token) throws ParserException {
		TokenStream stream = this.parser.getStream();
		int lineNumber = token.getLineNumber();
		
		// skip the 'parallel' token
		stream.next();

		stream.expect(Token.Type.EXECUTE_END);

		NodeBody body = this.parser.subparse(decideParallelEnd);

		// skip the 'endparallel' token
		stream.next();
		
		stream.expect(Token.Type.EXECUTE_END);
		return new NodeParallel(lineNumber, body);
	}

	private Function<Boolean, Token> decideParallelEnd = new Function<Boolean, Token>() {
		@Override
		public Boolean execute(Token token) {
			return token.test(Token.Type.NAME, "endparallel");
		}
	};

	@Override
	public String getTag() {
		return "parallel";
	}
}
