package com.mitchellbosecke.pebble.tokenParser;

import com.mitchellbosecke.pebble.lexer.Token;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.node.Node;
import com.mitchellbosecke.pebble.node.NodeBody;
import com.mitchellbosecke.pebble.node.NodeExpression;
import com.mitchellbosecke.pebble.node.NodeFor;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionDeclaration;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionName;
import com.mitchellbosecke.pebble.utils.Command;

public class ForTokenParser extends AbstractTokenParser {

	@Override
	public Node parse(Token token) {
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
		
		NodeBody body = this.parser.subparse(decideForEnd);
		
		
		// skip the 'endfor' token for now
		//TODO: handle else
		stream.next();
		
		stream.expect(Token.Type.BLOCK_END);
		
		return new NodeFor(lineNumber, iterationVariable, (NodeExpressionName)iterable, body);
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
