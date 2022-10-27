/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.tokenParser;

import io.pebbletemplates.lexer.Token;
import io.pebbletemplates.lexer.TokenStream;
import io.pebbletemplates.node.RenderableNode;
import io.pebbletemplates.node.SetNode;
import io.pebbletemplates.node.expression.Expression;
import io.pebbletemplates.parser.Parser;

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
