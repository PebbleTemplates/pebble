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
import com.mitchellbosecke.pebble.node.ImportNode;
import com.mitchellbosecke.pebble.node.RenderableNode;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.parser.Parser;

public class ImportTokenParser extends AbstractTokenParser {

    @Override
    public RenderableNode parse(Token token, Parser parser) throws ParserException {

        TokenStream stream = parser.getStream();
        int lineNumber = token.getLineNumber();

        // skip over the 'import' token
        stream.next();

        Expression<?> importExpression = parser.getExpressionParser().parseExpression();

        stream.expect(Token.Type.EXECUTE_END);

        return new ImportNode(lineNumber, importExpression);
    }

    @Override
    public String getTag() {
        return "import";
    }
}
