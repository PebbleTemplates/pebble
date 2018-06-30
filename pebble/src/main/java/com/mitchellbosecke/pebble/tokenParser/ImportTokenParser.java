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
import com.mitchellbosecke.pebble.node.ImportNode;
import com.mitchellbosecke.pebble.node.RenderableNode;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.parser.Parser;

public class ImportTokenParser implements TokenParser {

  @Override
  public RenderableNode parse(Token token, Parser parser) {

    TokenStream stream = parser.getStream();
    int lineNumber = token.getLineNumber();

    // skip over the 'import' token
    stream.next();

    Expression<?> importExpression = parser.getExpressionParser().parseExpression();

    Token current = stream.current();
    String alias = null;

    // We check if there is an optional 'as' keyword on the import tag.
    if (current.getType().equals(Token.Type.NAME) && current.getValue().equals("as")) {

      // Skip over 'as'
      stream.next();

      current = stream.expect(Token.Type.NAME);
      alias = current.getValue();
    }

    stream.expect(Token.Type.EXECUTE_END);

    return new ImportNode(lineNumber, importExpression, alias);
  }

  @Override
  public String getTag() {
    return "import";
  }
}
