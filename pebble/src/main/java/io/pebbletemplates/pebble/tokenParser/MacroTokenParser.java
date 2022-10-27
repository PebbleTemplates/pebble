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
import io.pebbletemplates.pebble.node.ArgumentsNode;
import io.pebbletemplates.pebble.node.BodyNode;
import io.pebbletemplates.pebble.node.MacroNode;
import io.pebbletemplates.pebble.node.RenderableNode;
import io.pebbletemplates.pebble.parser.Parser;

public class MacroTokenParser implements TokenParser {

  @Override
  public RenderableNode parse(Token token, Parser parser) {

    TokenStream stream = parser.getStream();

    // skip over the 'macro' token
    stream.next();

    String macroName = stream.expect(Token.Type.NAME).getValue();

    ArgumentsNode args = parser.getExpressionParser().parseArguments(true);

    stream.expect(Token.Type.EXECUTE_END);

    // parse the body
    BodyNode body = parser.subparse(tkn -> tkn.test(Token.Type.NAME, "endmacro"));

    // skip the 'endmacro' token
    stream.next();

    stream.expect(Token.Type.EXECUTE_END);

    return new MacroNode(macroName, args, body);
  }

  @Override
  public String getTag() {
    return "macro";
  }
}
