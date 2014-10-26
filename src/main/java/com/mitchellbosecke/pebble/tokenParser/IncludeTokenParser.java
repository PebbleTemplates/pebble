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

public class IncludeTokenParser extends AbstractTokenParser {

    @Override
    public RenderableNode parse(Token token) throws ParserException {

        TokenStream stream = this.parser.getStream();
        int lineNumber = token.getLineNumber();

        // skip over the 'include' token
        stream.next();

        Expression<?> includeExpression = this.parser.getExpressionParser().parseExpression();

        stream.expect(Token.Type.EXECUTE_END);

        return new IncludeNode(lineNumber, includeExpression);
    }

    @Override
    public String getTag() {
        return "include";
    }
}
