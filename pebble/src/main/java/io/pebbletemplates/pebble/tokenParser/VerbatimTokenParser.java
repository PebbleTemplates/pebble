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
import io.pebbletemplates.pebble.node.RenderableNode;
import io.pebbletemplates.pebble.parser.Parser;

/**
 * This is just a dummy class to point developers into the right direction; the verbatim tag had to
 * be implemented directly into the lexer.
 *
 * @author mbosecke
 */
public class VerbatimTokenParser implements TokenParser {

  @Override
  public RenderableNode parse(Token token, Parser parser) {

    throw new UnsupportedOperationException();
  }

  @Override
  public String getTag() {
    return "verbatim";
  }
}
