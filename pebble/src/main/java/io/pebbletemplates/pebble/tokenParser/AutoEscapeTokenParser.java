/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble.tokenParser;

import io.pebbletemplates.pebble.error.ParserException;
import io.pebbletemplates.pebble.lexer.Token;
import io.pebbletemplates.pebble.lexer.TokenStream;
import io.pebbletemplates.pebble.node.AutoEscapeNode;
import io.pebbletemplates.pebble.node.BodyNode;
import io.pebbletemplates.pebble.node.RenderableNode;
import io.pebbletemplates.pebble.parser.Parser;

public class AutoEscapeTokenParser implements TokenParser {

  @Override
  public RenderableNode parse(Token token, Parser parser) {
    TokenStream stream = parser.getStream();
    int lineNumber = token.getLineNumber();

    String strategy = null;
    boolean active = true;

    // skip over the 'autoescape' token
    stream.next();

    // did user specify active boolean?
    if (stream.current().test(Token.Type.NAME)) {
      active = Boolean.parseBoolean(stream.current().getValue());
      stream.next();
    }

    // did user specify a strategy?
    if (stream.current().test(Token.Type.STRING)) {
      strategy = stream.current().getValue();
      stream.next();
    }

    stream.expect(Token.Type.EXECUTE_END);

    // now we parse the block body
    BodyNode body = parser.subparse(tkn -> tkn.test(Token.Type.NAME, "endautoescape"));

    // Check that the body ended with the expected closing tag before consuming it.
    Token endautoescape = stream.current();
    if (!endautoescape.test(Token.Type.NAME, "endautoescape")) {
      throw new ParserException(null,
          "endautoescape tag should be present with autoescape tag starting line number ",
          token.getLineNumber(), stream.getFilename());
    }

    // skip the 'endautoescape' token
    stream.next();

    stream.expect(Token.Type.EXECUTE_END);

    return new AutoEscapeNode(lineNumber, body, active, strategy);
  }

  @Override
  public String getTag() {
    return "autoescape";
  }
}
