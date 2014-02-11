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
import com.mitchellbosecke.pebble.node.NodeExpression;

public class ExtendsTokenParser extends AbstractTokenParser {

	@Override
	public Node parse(Token token) throws ParserException {
		TokenStream stream = this.parser.getStream();
		int lineNumber = token.getLineNumber();

		// skip the 'extends' token
		stream.next();

		if (this.parser.getParentTemplateExpression() != null) {
			throw new ParserException(null, "Multiple extend tags are not allowed.", lineNumber, parser.getStream()
					.getFilename());
		}

		NodeExpression parentTemplateExpression = parser.getExpressionParser().parseExpression();
		this.parser.setParentTemplateExpression(parentTemplateExpression);

		stream.expect(Token.Type.EXECUTE_END);
		return null;
	}

	@Override
	public String getTag() {
		return "extends";
	}
}
