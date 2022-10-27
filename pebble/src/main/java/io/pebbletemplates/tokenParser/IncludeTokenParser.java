/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.tokenParser;

import io.pebbletemplates.error.ParserException;
import io.pebbletemplates.lexer.Token;
import io.pebbletemplates.lexer.TokenStream;
import io.pebbletemplates.node.IncludeNode;
import io.pebbletemplates.node.RenderableNode;
import io.pebbletemplates.node.expression.Expression;
import io.pebbletemplates.node.expression.MapExpression;
import io.pebbletemplates.parser.Parser;

public class IncludeTokenParser implements TokenParser {

  @Override
  public RenderableNode parse(Token token, Parser parser) {

    TokenStream stream = parser.getStream();
    int lineNumber = token.getLineNumber();

    // skip over the 'include' token
    stream.next();

    Expression<?> includeExpression = parser.getExpressionParser().parseExpression();

    Token current = stream.current();
    MapExpression mapExpression = null;

    // We check if there is an optional 'with' parameter on the include tag.
    if (current.getType().equals(Token.Type.NAME) && current.getValue().equals("with")) {

      // Skip over 'with'
      stream.next();

      Expression<?> parsedExpression = parser.getExpressionParser().parseExpression();

      if (parsedExpression instanceof MapExpression) {
        mapExpression = (MapExpression) parsedExpression;
      } else {
        throw new ParserException(null,
            String.format("Unexpected expression '%1s'.", parsedExpression
                .getClass().getCanonicalName()), token.getLineNumber(), stream.getFilename());
      }

    }

    stream.expect(Token.Type.EXECUTE_END);

    return new IncludeNode(lineNumber, includeExpression, mapExpression);
  }

  @Override
  public String getTag() {
    return "include";
  }
}
