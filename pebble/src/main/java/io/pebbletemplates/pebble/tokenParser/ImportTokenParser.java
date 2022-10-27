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
import io.pebbletemplates.pebble.node.ImportNode;
import io.pebbletemplates.pebble.node.RenderableNode;
import io.pebbletemplates.pebble.node.expression.Expression;
import io.pebbletemplates.pebble.parser.Parser;

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
