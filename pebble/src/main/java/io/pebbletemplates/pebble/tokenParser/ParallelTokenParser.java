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
import io.pebbletemplates.pebble.node.ParallelNode;
import io.pebbletemplates.pebble.node.RenderableNode;
import io.pebbletemplates.pebble.parser.Parser;

public class ParallelTokenParser implements TokenParser {

  @Override
  public RenderableNode parse(Token token, Parser parser) {
    TokenStream stream = parser.getStream();
    int lineNumber = token.getLineNumber();

    // skip the 'parallel' token
    stream.next();

    stream.expect(Token.Type.EXECUTE_END);

    BodyNode body = parser.subparse(tkn -> tkn.test(Token.Type.NAME, "endparallel"));

    // skip the 'endparallel' token
    stream.next();

    stream.expect(Token.Type.EXECUTE_END);
    return new ParallelNode(lineNumber, body);
  }

  @Override
  public String getTag() {
    return "parallel";
  }
}
