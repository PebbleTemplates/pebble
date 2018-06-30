/*
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.parser;

import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.node.BodyNode;
import com.mitchellbosecke.pebble.node.RootNode;

public interface Parser {

  RootNode parse(TokenStream stream);

  BodyNode subparse();

  /**
   * Provides the stream of tokens which ultimately need to be "parsed" into Nodes.
   *
   * @return TokenStream
   */
  TokenStream getStream();

  /**
   * Parses the existing TokenStream, starting at the current Token, and ending when the
   * stopCondition is fullfilled.
   *
   * @param stopCondition The condition to stop parsing a segment of the template.
   * @return A node representing the parsed section
   */
  BodyNode subparse(StoppingCondition stopCondition);

  ExpressionParser getExpressionParser();

  String peekBlockStack();

  String popBlockStack();

  void pushBlockStack(String blockName);

}
