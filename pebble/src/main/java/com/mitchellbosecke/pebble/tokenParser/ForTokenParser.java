/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.tokenParser;

import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.lexer.Token;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.node.BodyNode;
import com.mitchellbosecke.pebble.node.ForNode;
import com.mitchellbosecke.pebble.node.RenderableNode;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.parser.Parser;

public class ForTokenParser implements TokenParser {

  @Override
  public RenderableNode parse(Token token, Parser parser) {
    TokenStream stream = parser.getStream();
    int lineNumber = token.getLineNumber();

    // skip the 'for' token
    stream.next();

    // get the iteration variable
    String iterationVariable = parser.getExpressionParser().parseNewVariableName();

    stream.expect(Token.Type.NAME, "in");

    // get the iterable variable
    Expression<?> iterable = parser.getExpressionParser().parseExpression();

    stream.expect(Token.Type.EXECUTE_END);

    BodyNode body = parser.subparse(tkn -> tkn.test(Token.Type.NAME, "else", "endfor"));

    BodyNode elseBody = null;

    if (stream.current().test(Token.Type.NAME, "else")) {
      // skip the 'else' token
      stream.next();
      stream.expect(Token.Type.EXECUTE_END);
      elseBody = parser.subparse(tkn -> tkn.test(Token.Type.NAME, "endfor"));
    }

    if (stream.current().getValue() == null) {
      throw new ParserException(
          null,
          "Unexpected end of template. Pebble was looking for the \"endfor\" tag",
          stream.current().getLineNumber(), stream.getFilename());
    }
    // skip the 'endfor' token
    stream.next();
    stream.expect(Token.Type.EXECUTE_END);

    return new ForNode(lineNumber, iterationVariable, iterable, body, elseBody);
  }

  @Override
  public String getTag() {
    return "for";
  }
}
