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
import com.mitchellbosecke.pebble.node.FlushNode;
import com.mitchellbosecke.pebble.node.RenderableNode;
import com.mitchellbosecke.pebble.parser.Parser;

public class FlushTokenParser extends AbstractTokenParser {

    @Override
    public RenderableNode parse(Token token, Parser parser) throws ParserException {

        TokenStream stream = parser.getStream();
        int lineNumber = token.getLineNumber();

        // skip over the 'flush' token
        stream.next();

        stream.expect(Token.Type.EXECUTE_END);

        return new FlushNode(lineNumber);
    }

    @Override
    public String getTag() {
        return "flush";
    }
}
