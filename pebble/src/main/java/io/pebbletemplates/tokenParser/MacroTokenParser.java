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
import io.pebbletemplates.node.ArgumentsNode;
import io.pebbletemplates.node.BodyNode;
import io.pebbletemplates.node.MacroNode;
import io.pebbletemplates.node.RenderableNode;
import io.pebbletemplates.parser.Parser;

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
