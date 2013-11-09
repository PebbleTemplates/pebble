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
import com.mitchellbosecke.pebble.node.NodeImport;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionDeclaration;

public class ImportTokenParser extends AbstractTokenParser {

	@Override
	public Node parse(Token token) throws SyntaxException {

		TokenStream stream = this.parser.getStream();
		int lineNumber = token.getLineNumber();

		// skip over the 'import' token
		stream.next();

		NodeExpression importExpression = this.parser.getExpressionParser().parseExpression();

		stream.expect(Token.Type.NAME, "as");
		
		NodeExpressionDeclaration var = this.parser.getExpressionParser().parseDeclarationExpression();

		stream.expect(Token.Type.BLOCK_END);

		return new NodeImport(lineNumber, importExpression, var);
	}

	@Override
	public String getTag() {
		return "import";
	}
}
