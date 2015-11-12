/*******************************************************************************
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.tokenParser;

import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.lexer.Token;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.node.BodyNode;
import com.mitchellbosecke.pebble.node.ParallelNode;
import com.mitchellbosecke.pebble.node.RenderableNode;
import com.mitchellbosecke.pebble.parser.Parser;
import com.mitchellbosecke.pebble.parser.StoppingCondition;

public class ParallelTokenParser extends AbstractTokenParser {

    @Override
    public RenderableNode parse(Token token, Parser parser) throws ParserException {
        TokenStream stream = parser.getStream();
        int lineNumber = token.getLineNumber();

        // skip the 'parallel' token
        stream.next();

        stream.expect(Token.Type.EXECUTE_END);

        BodyNode body = parser.subparse(decideParallelEnd);

        // skip the 'endparallel' token
        stream.next();

        stream.expect(Token.Type.EXECUTE_END);
        return new ParallelNode(lineNumber, body);
    }

    private StoppingCondition decideParallelEnd = new StoppingCondition() {

        @Override
        public boolean evaluate(Token token) {
            return token.test(Token.Type.NAME, "endparallel");
        }
    };

    @Override
    public String getTag() {
        return "parallel";
    }
}
