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
import com.mitchellbosecke.pebble.node.BlockNode;
import com.mitchellbosecke.pebble.node.BodyNode;
import com.mitchellbosecke.pebble.node.RenderableNode;
import com.mitchellbosecke.pebble.parser.Parser;

public class BlockTokenParser implements TokenParser {

  @Override
  public RenderableNode parse(Token token, Parser parser) {
    TokenStream stream = parser.getStream();
    int lineNumber = token.getLineNumber();

    // skip over the 'block' token to the name token
    Token blockName = stream.next();

    // expect a name or string for the new block
    if (!blockName.test(Token.Type.NAME) && !blockName.test(Token.Type.STRING)) {

      // we already know an error has occurred but let's just call the
      // typical "expect" method so that we know a proper error
      // message is given to user
      stream.expect(Token.Type.NAME);
    }

    // get the name of the new block
    String name = blockName.getValue();

    // skip over name
    stream.next();

    stream.expect(Token.Type.EXECUTE_END);

    parser.pushBlockStack(name);

    // now we parse the block body
    BodyNode blockBody = parser.subparse(tkn -> tkn.test(Token.Type.NAME, "endblock"));
    parser.popBlockStack();

    //check endblock us exist with block or not
    Token endblock = stream.current();
    if (!endblock.test(Token.Type.NAME, "endblock")) {
      throw new ParserException(null,
          "endblock tag should be present with block tag starting line number ",
          token.getLineNumber(), stream.getFilename());
    }

    // skip the 'endblock' token
    stream.next();

    // check if user included block name in endblock
    Token current = stream.current();
    if (current.test(Token.Type.NAME, name) || current.test(Token.Type.STRING, name)) {
      stream.next();
    }

    stream.expect(Token.Type.EXECUTE_END);
    return new BlockNode(lineNumber, name, blockBody);
  }

  @Override
  public String getTag() {
    return "block";
  }
}
