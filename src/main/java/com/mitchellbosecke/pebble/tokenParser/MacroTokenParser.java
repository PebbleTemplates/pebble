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
import com.mitchellbosecke.pebble.node.ArgumentsNode;
import com.mitchellbosecke.pebble.node.BodyNode;
import com.mitchellbosecke.pebble.node.MacroNode;
import com.mitchellbosecke.pebble.node.RenderableNode;
import com.mitchellbosecke.pebble.parser.Parser;
import com.mitchellbosecke.pebble.parser.StoppingCondition;

public class MacroTokenParser extends AbstractTokenParser {

    @Override
    public RenderableNode parse(Token token, Parser parser) throws ParserException {

        TokenStream stream = parser.getStream();

        // skip over the 'macro' token
        stream.next();

        String macroName = stream.expect(Token.Type.NAME).getValue();

        ArgumentsNode args = parser.getExpressionParser().parseArguments(true);

        stream.expect(Token.Type.EXECUTE_END);

        // parse the body
        BodyNode body = parser.subparse(decideMacroEnd);

        // skip the 'endmacro' token
        stream.next();

        stream.expect(Token.Type.EXECUTE_END);

        return new MacroNode(macroName, args, body);
    }

    private StoppingCondition decideMacroEnd = new StoppingCondition() {

        @Override
        public boolean evaluate(Token token) {
            return token.test(Token.Type.NAME, "endmacro");
        }
    };

    @Override
    public String getTag() {
        return "macro";
    }
}
