/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble.tokenParser;

import io.pebbletemplates.pebble.lexer.Token;
import io.pebbletemplates.pebble.lexer.TokenStream;
import io.pebbletemplates.pebble.node.BodyNode;
import io.pebbletemplates.pebble.node.PrintNode;
import io.pebbletemplates.pebble.node.RenderableNode;
import io.pebbletemplates.pebble.node.expression.Expression;
import io.pebbletemplates.pebble.node.expression.FilterExpression;
import io.pebbletemplates.pebble.node.expression.RenderableNodeExpression;
import io.pebbletemplates.pebble.parser.Parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses the "filter" tag. It has nothing to do with implementing normal filters.
 */
public class FilterTokenParser implements TokenParser {

  @Override
  public RenderableNode parse(Token token, Parser parser) {
    TokenStream stream = parser.getStream();
    int lineNumber = token.getLineNumber();

    // skip the 'filter' token
    stream.next();

    List<Expression<?>> filterInvocationExpressions = new ArrayList<>();

    filterInvocationExpressions.add(parser.getExpressionParser().parseFilterInvocationExpression());

    while (stream.current().test(Token.Type.OPERATOR, "|")) {
      // skip the '|' token
      stream.next();
      filterInvocationExpressions
          .add(parser.getExpressionParser().parseFilterInvocationExpression());
    }

    stream.expect(Token.Type.EXECUTE_END);

    BodyNode body = parser.subparse(tkn -> tkn.test(Token.Type.NAME, "endfilter"));

    stream.next();
    stream.expect(Token.Type.EXECUTE_END);

    Expression<?> lastExpression = new RenderableNodeExpression(body,
        stream.current().getLineNumber());

    for (Expression<?> filterInvocationExpression : filterInvocationExpressions) {

      FilterExpression filterExpression = new FilterExpression();
      filterExpression.setRight(filterInvocationExpression);
      filterExpression.setLeft(lastExpression);

      lastExpression = filterExpression;
    }

    return new PrintNode(lastExpression, lineNumber);
  }

  @Override
  public String getTag() {
    return "filter";
  }
}
