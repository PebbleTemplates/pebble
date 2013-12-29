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
import com.mitchellbosecke.pebble.node.NodeBody;
import com.mitchellbosecke.pebble.node.NodeMacro;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionArguments;
import com.mitchellbosecke.pebble.utils.Method;

public class MacroTokenParser extends AbstractTokenParser {

	@Override
	public Node parse(Token token) throws SyntaxException {

		TokenStream stream = this.parser.getStream();
		int lineNumber = token.getLineNumber();

		// skip over the 'macro' token
		stream.next();

		String macroName = stream.expect(Token.Type.NAME).getValue();

		NodeExpressionArguments args = this.parser.getExpressionParser().parseArguments(true);

		stream.expect(Token.Type.EXECUTE_END);

		// parse the body
		NodeBody body = this.parser.subparse(decideMacroEnd);

		// skip the 'endmacro' token
		stream.next();
		
		stream.expect(Token.Type.EXECUTE_END);

		this.parser.addMacro(macroName, new NodeMacro(lineNumber, macroName, args, body));
		return null;
	}

	private Method<Boolean, Token> decideMacroEnd = new Method<Boolean, Token>() {
		@Override
		public Boolean execute(Token token) {
			return token.test(Token.Type.NAME, "endmacro");
		}
	};

	@Override
	public String getTag() {
		return "macro";
	}
}
