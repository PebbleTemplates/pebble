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
import com.mitchellbosecke.pebble.node.NodeExpression;
import com.mitchellbosecke.pebble.node.NodeFor;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionNewVariableName;
import com.mitchellbosecke.pebble.parser.StoppingCondition;

public class ForTokenParser extends AbstractTokenParser {

	@Override
	public Node parse(Token token) throws ParserException {
		TokenStream stream = this.parser.getStream();
		int lineNumber = token.getLineNumber();

		// skip the 'for' token
		stream.next();

		// get the iteration variable
		NodeExpressionNewVariableName iterationVariable = this.parser.getExpressionParser().parseNewVariableName();

		stream.expect(Token.Type.NAME, "in");

		// get the iterable variable
		NodeExpression iterable = this.parser.getExpressionParser().parseExpression();

		stream.expect(Token.Type.EXECUTE_END);

		NodeBody body = this.parser.subparse(decideForFork);

		NodeBody elseBody = null;

		if (stream.current().test(Token.Type.NAME, "else")) {
			// skip the 'else' token
			stream.next();
			stream.expect(Token.Type.EXECUTE_END);
			elseBody = this.parser.subparse(decideForEnd);
		}

		// skip the 'endfor' token
		stream.next();

		stream.expect(Token.Type.EXECUTE_END);

		return new NodeFor(lineNumber, iterationVariable, (NodeExpression) iterable, body, elseBody);
	}

	private StoppingCondition decideForFork = new StoppingCondition() {
		@Override
		public boolean evaluate(Token token) {
			return token.test(Token.Type.NAME, "else", "endfor");
		}
	};

	private StoppingCondition decideForEnd = new StoppingCondition() {
		@Override
		public boolean evaluate(Token token) {
			return token.test(Token.Type.NAME, "endfor");
		}
	};

	@Override
	public String getTag() {
		return "for";
	}
}
