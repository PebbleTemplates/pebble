/*******************************************************************************
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
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
import com.mitchellbosecke.pebble.node.BodyNode;
import com.mitchellbosecke.pebble.node.IfNode;
import com.mitchellbosecke.pebble.node.RenderableNode;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.parser.Parser;
import com.mitchellbosecke.pebble.parser.StoppingCondition;
import com.mitchellbosecke.pebble.utils.Pair;

public class IfTokenParser extends AbstractTokenParser {

    @Override
    public RenderableNode parse(Token token, Parser parser) throws ParserException {
        TokenStream stream = parser.getStream();
        int lineNumber = token.getLineNumber();

        // skip the 'if' token
        stream.next();

        List<Pair<Expression<?>, BodyNode>> conditionsWithBodies = new ArrayList<>();

        Expression<?> expression = parser.getExpressionParser().parseExpression();

        stream.expect(Token.Type.EXECUTE_END);

        BodyNode body = parser.subparse(decideIfFork);

        conditionsWithBodies.add(new Pair<Expression<?>, BodyNode>(expression, body));

        BodyNode elseBody = null;
        boolean end = false;
        while (!end) {
            switch (stream.current().getValue()) {
            case "else":
                stream.next();
                stream.expect(Token.Type.EXECUTE_END);
                elseBody = parser.subparse(decideIfEnd);
                break;

            case "elseif":
                stream.next();
                expression = parser.getExpressionParser().parseExpression();
                stream.expect(Token.Type.EXECUTE_END);
                body = parser.subparse(decideIfFork);
                conditionsWithBodies.add(new Pair<Expression<?>, BodyNode>(expression, body));
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
        return new IfNode(lineNumber, conditionsWithBodies, elseBody);
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
