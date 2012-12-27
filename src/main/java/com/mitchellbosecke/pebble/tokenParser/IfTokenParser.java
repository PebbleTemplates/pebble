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

import java.util.ArrayList;
import java.util.List;

import com.mitchellbosecke.pebble.error.SyntaxException;
import com.mitchellbosecke.pebble.lexer.Token;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.node.Node;
import com.mitchellbosecke.pebble.node.NodeBody;
import com.mitchellbosecke.pebble.node.NodeExpression;
import com.mitchellbosecke.pebble.node.NodeIf;
import com.mitchellbosecke.pebble.utils.Command;
import com.mitchellbosecke.pebble.utils.Pair;

public class IfTokenParser extends AbstractTokenParser {

	@Override
	public Node parse(Token token) {
		TokenStream stream = this.parser.getStream();
		int lineNumber = token.getLineNumber();
		
		// skip the 'if' token
		stream.next();

		List<Pair<NodeExpression, NodeBody>> conditionsWithBodies = new ArrayList<>();

		NodeExpression expression = this.parser.getExpressionParser().parseExpression();

		stream.expect(Token.Type.BLOCK_END);

		NodeBody body = this.parser.subparse(decideIfFork);

		conditionsWithBodies.add(new Pair<NodeExpression, NodeBody>(expression, body));

		NodeBody elseBody = null;
		boolean end = false;
		while (!end) {
			switch (stream.current().getValue()) {
				case "else":
					stream.next();
					stream.expect(Token.Type.BLOCK_END);
					elseBody = this.parser.subparse(decideIfEnd);
					break;

				case "elseif":
					stream.next();
					expression = this.parser.getExpressionParser().parseExpression();
					stream.expect(Token.Type.BLOCK_END);
					body = this.parser.subparse(decideIfFork);
					conditionsWithBodies.add(new Pair<NodeExpression, NodeBody>(expression, body));
					break;

				case "endif":
					stream.next();
					end = true;
					break;
				default:
					throw new SyntaxException(
							String.format("Unexpected end of template. Pebble was looking for the following tags \"else\", \"elseif\", or \"endif\""),
							stream.current().getLineNumber(), stream.getFilename());
			}
		}

		stream.expect(Token.Type.BLOCK_END);
		return new NodeIf(lineNumber, conditionsWithBodies, elseBody);
	}

	private Command<Boolean, Token> decideIfFork = new Command<Boolean, Token>() {
		@Override
		public Boolean execute(Token token) {
			return token.test(Token.Type.NAME, "elseif", "else", "endif");
		}
	};

	private Command<Boolean, Token> decideIfEnd = new Command<Boolean, Token>() {
		@Override
		public Boolean execute(Token token) {
			return token.test(Token.Type.NAME, "endif");
		}
	};

	@Override
	public String getTag() {
		return "if";
	}
}
