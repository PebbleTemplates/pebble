/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.tokenParser;

import com.mitchellbosecke.pebble.lexer.Token;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.node.RenderableNode;
import com.mitchellbosecke.pebble.node.SetNode;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.parser.Parser;

public class SetTokenParser implements TokenParser {

  @Override
  public RenderableNode parse(Token token, Parser parser) {
    TokenStream stream = parser.getStream();
    int lineNumber = token.getLineNumber();

    // skip the 'set' token
    stream.next();

    String name = parser.getExpressionParser().parseNewVariableName();

    stream.expect(Token.Type.PUNCTUATION, "=");

    Expression<?> value = parser.getExpressionParser().parseExpression();

    stream.expect(Token.Type.EXECUTE_END);

    return new SetNode(lineNumber, name, value);
  }

  @Override
  public String getTag() {
    return "set";
  }
}
