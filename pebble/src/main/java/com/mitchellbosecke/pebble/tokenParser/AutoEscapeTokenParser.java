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
import com.mitchellbosecke.pebble.node.AutoEscapeNode;
import com.mitchellbosecke.pebble.node.BodyNode;
import com.mitchellbosecke.pebble.node.RenderableNode;
import com.mitchellbosecke.pebble.parser.Parser;

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
