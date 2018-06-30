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
import com.mitchellbosecke.pebble.lexer.Token.Type;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.node.BodyNode;
import com.mitchellbosecke.pebble.node.PrintNode;
import com.mitchellbosecke.pebble.node.RenderableNode;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.node.expression.FilterExpression;
import com.mitchellbosecke.pebble.node.expression.RenderableNodeExpression;
import com.mitchellbosecke.pebble.parser.Parser;
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

    while (stream.current().test(Type.OPERATOR, "|")) {
      // skip the '|' token
      stream.next();
      filterInvocationExpressions
          .add(parser.getExpressionParser().parseFilterInvocationExpression());
    }

    stream.expect(Token.Type.EXECUTE_END);

    BodyNode body = parser.subparse(tkn -> tkn.test(Type.NAME, "endfilter"));

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
