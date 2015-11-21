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
import com.mitchellbosecke.pebble.node.IncludeNode;
import com.mitchellbosecke.pebble.node.RenderableNode;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.node.expression.MapExpression;
import com.mitchellbosecke.pebble.parser.Parser;

public class IncludeTokenParser extends AbstractTokenParser {

    @Override
    public RenderableNode parse(Token token, Parser parser) throws ParserException {

        TokenStream stream = parser.getStream();
        int lineNumber = token.getLineNumber();

        // skip over the 'include' token
        stream.next();

        Expression<?> includeExpression = parser.getExpressionParser().parseExpression();

        Token current = stream.current();
        MapExpression mapExpression = null;

        boolean isolate = false;

        // We check if there is an optional 'with' parameter on the include tag.
        if (current.getType().equals(Token.Type.NAME) && current.getValue().equals("with")) {

            // Skip over 'with'
            stream.next();

            Expression<?> parsedExpression = parser.getExpressionParser().parseExpression();

            if (parsedExpression instanceof MapExpression) {
                mapExpression = (MapExpression) parsedExpression;
            } else {
                throw new ParserException(null, String.format("Unexpected expression '%1s'.", parsedExpression
                        .getClass().getCanonicalName()), token.getLineNumber(), stream.getFilename());
            }
        }

        // We check if there is an optional 'isolate' parameter on the
        // include tag. Isolate means that any change to the variable will have
        // no effect to the remaining execution.
        if (stream.current().getType().equals(Token.Type.NAME) && stream.current().getValue().equals("isolate")) {

            // Skip over 'isolate'
            stream.next();

            // Handle the include in isolate mode
            isolate = true;
        }

        stream.expect(Token.Type.EXECUTE_END);

        return new IncludeNode(lineNumber, includeExpression, mapExpression, isolate);
    }

    @Override
    public String getTag() {
        return "include";
    }
}
