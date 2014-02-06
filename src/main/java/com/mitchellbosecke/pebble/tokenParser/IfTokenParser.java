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

import java.util.ArrayList;
import java.util.List;

import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.lexer.Token;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.node.Node;
import com.mitchellbosecke.pebble.node.NodeBody;
import com.mitchellbosecke.pebble.node.NodeExpression;
import com.mitchellbosecke.pebble.node.NodeIf;
import com.mitchellbosecke.pebble.parser.StoppingCondition;
import com.mitchellbosecke.pebble.utils.Pair;

public class IfTokenParser extends AbstractTokenParser {

	@Override
	public Node parse(Token token) throws ParserException {
		TokenStream stream = this.parser.getStream();
		int lineNumber = token.getLineNumber();

		// skip the 'if' token
		stream.next();

		List<Pair<NodeExpression, NodeBody>> conditionsWithBodies = new ArrayList<>();

		NodeExpression expression = this.parser.getExpressionParser().parseExpression();

		stream.expect(Token.Type.EXECUTE_END);

		NodeBody body = this.parser.subparse(decideIfFork);

		conditionsWithBodies.add(new Pair<NodeExpression, NodeBody>(expression, body));

		NodeBody elseBody = null;
		boolean end = false;
		while (!end) {
			switch (stream.current().getValue()) {
				case "else":
					stream.next();
					stream.expect(Token.Type.EXECUTE_END);
					elseBody = this.parser.subparse(decideIfEnd);
					break;

				case "elseif":
					stream.next();
					expression = this.parser.getExpressionParser().parseExpression();
					stream.expect(Token.Type.EXECUTE_END);
					body = this.parser.subparse(decideIfFork);
					conditionsWithBodies.add(new Pair<NodeExpression, NodeBody>(expression, body));
					break;

				case "endif":
					stream.next();
					end = true;
					break;
				default:
					throw new ParserException(
							null,
							String.format("Unexpected end of template. Pebble was looking for the following tags \"else\", \"elseif\", or \"endif\""),
							stream.current().getLineNumber(), stream.getFilename());
			}
		}

		stream.expect(Token.Type.EXECUTE_END);
		return new NodeIf(lineNumber, conditionsWithBodies, elseBody);
	}

	private StoppingCondition decideIfFork = new StoppingCondition() {
		@Override
		public boolean evaluate(Token token) {
			return token.test(Token.Type.NAME, "elseif", "else", "endif");
		}
	};

	private StoppingCondition decideIfEnd = new StoppingCondition() {
		@Override
		public boolean evaluate(Token token) {
			return token.test(Token.Type.NAME, "endif");
		}
	};

	@Override
	public String getTag() {
		return "if";
	}
}
