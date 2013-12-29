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
import com.mitchellbosecke.pebble.node.NodeExpression;
import com.mitchellbosecke.pebble.node.NodeSet;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionDeclaration;

public class SetTokenParser extends AbstractTokenParser {

	@Override
	public Node parse(Token token) throws SyntaxException {
		TokenStream stream = this.parser.getStream();
		int lineNumber = token.getLineNumber();
		
		// skip the 'set' token
		stream.next();

		NodeExpressionDeclaration name = this.parser.getExpressionParser().parseDeclarationExpression();

		stream.expect(Token.Type.PUNCTUATION, "=");
		
		NodeExpression value = this.parser.getExpressionParser().parseExpression();

		stream.expect(Token.Type.EXECUTE_END);

		return new NodeSet(lineNumber, name, value);
	}

	@Override
	public String getTag() {
		return "set";
	}
}
