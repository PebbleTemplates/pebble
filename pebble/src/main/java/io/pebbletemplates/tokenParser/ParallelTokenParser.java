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
import io.pebbletemplates.node.BodyNode;
import io.pebbletemplates.node.ParallelNode;
import io.pebbletemplates.node.RenderableNode;
import io.pebbletemplates.parser.Parser;

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
