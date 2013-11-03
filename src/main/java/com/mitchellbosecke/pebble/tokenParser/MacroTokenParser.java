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

import com.mitchellbosecke.pebble.error.SyntaxException;
import com.mitchellbosecke.pebble.lexer.Token;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.node.Node;
import com.mitchellbosecke.pebble.node.NodeBody;
import com.mitchellbosecke.pebble.node.NodeMacro;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionArguments;
import com.mitchellbosecke.pebble.utils.Command;

public class MacroTokenParser extends AbstractTokenParser {

	@Override
	public Node parse(Token token) throws SyntaxException {

		TokenStream stream = this.parser.getStream();
		int lineNumber = token.getLineNumber();

		// skip over the 'macro' token
		stream.next();

		String macroName = stream.expect(Token.Type.NAME).getValue();

		NodeExpressionArguments args = this.parser.getExpressionParser().parseArguments(true);

		stream.expect(Token.Type.BLOCK_END);

		// parse the body
		NodeBody body = this.parser.subparse(decideMacroEnd);

		// skip the 'endmacro' token
		stream.next();
		
		stream.expect(Token.Type.BLOCK_END);

		this.parser.addMacro(macroName, new NodeMacro(lineNumber, macroName, args, body));
		return null;
	}

	private Command<Boolean, Token> decideMacroEnd = new Command<Boolean, Token>() {
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
