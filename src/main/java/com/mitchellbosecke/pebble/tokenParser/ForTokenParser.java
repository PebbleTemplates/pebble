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
import com.mitchellbosecke.pebble.node.NodeExpression;
import com.mitchellbosecke.pebble.node.NodeFor;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionDeclaration;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionVariableName;
import com.mitchellbosecke.pebble.utils.Command;

public class ForTokenParser extends AbstractTokenParser {

	@Override
	public Node parse(Token token) throws SyntaxException {
		TokenStream stream = this.parser.getStream();
		int lineNumber = token.getLineNumber();

		// skip the 'for' token
		stream.next();

		// get the iteration variable
		NodeExpressionDeclaration iterationVariable = this.parser.getExpressionParser().parseDeclarationExpression();

		stream.expect(Token.Type.NAME, "in");

		// get the iterable variable
		NodeExpression iterable = this.parser.getExpressionParser().parseExpression();

		stream.expect(Token.Type.BLOCK_END);

		NodeBody body = this.parser.subparse(decideForFork);

		NodeBody elseBody = null;

		if (stream.current().test(Token.Type.NAME, "else")) {
			// skip the 'else' token
			stream.next();
			stream.expect(Token.Type.BLOCK_END);
			elseBody = this.parser.subparse(decideForEnd);
		}

		// skip the 'endfor' token
		stream.next();

		stream.expect(Token.Type.BLOCK_END);

		return new NodeFor(lineNumber, iterationVariable, (NodeExpressionVariableName) iterable, body, elseBody);
	}

	private Command<Boolean, Token> decideForFork = new Command<Boolean, Token>() {
		@Override
		public Boolean execute(Token token) {
			return token.test(Token.Type.NAME, "else", "endfor");
		}
	};

	private Command<Boolean, Token> decideForEnd = new Command<Boolean, Token>() {
		@Override
		public Boolean execute(Token token) {
			return token.test(Token.Type.NAME, "endfor");
		}
	};

	@Override
	public String getTag() {
		return "for";
	}
}
