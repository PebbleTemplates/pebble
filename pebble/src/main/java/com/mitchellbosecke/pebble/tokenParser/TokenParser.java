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
import com.mitchellbosecke.pebble.node.RenderableNode;
import com.mitchellbosecke.pebble.parser.Parser;

/**
 * A TokenParser is responsible for converting a stream of Tokens into a Node. A TokenParser often
 * has to temporarily delegate responsibility to Pebble's main Parser or Pebble's ExpressionParser.
 *
 * @author Mitchell
 */
public interface TokenParser {

  /**
   * The "tag" is used to determine when to use a particular instance of a TokenParser. For example,
   * the TokenParser that handles the "block" tag would return "block" with this method.
   *
   * @return The tag used to define this TokenParser.
   */
  String getTag();

  /**
   * The TokenParser is responsible to convert all the necessary tokens into appropriate Nodes. It
   * can access tokens using parser.getTokenStream().
   *
   * The tag may be self contained like the "extends" tag or it may have a start and end point with
   * content in the middle like the "block" tag. If it contains content in the middle, it can use
   * parser.subparse(stopCondition) to parse the middle content at which point responsibility comes
   * back to the TokenParser to parse the end point.
   *
   * It is the responsibility of the TokenParser to ensure that when it is complete, the "current"
   * token of the primary Parser's TokenStream is pointing to the NEXT token. USUALLY this means the
   * last statement in this parse method, immediately prior to the return statement, is the
   * following which will consume one token:
   *
   * stream.expect(Token.Type.EXECUTE_END);
   *
   * Here are two relatively simple examples of how TokenParsers are implemented:
   *
   * - self contained: com.mitchellbosecke.pebble.tokenParser.SetTokenParser - middle content:
   * com.mitchellbosecke.pebble.tokenParser.BlockTokenParser
   *
   * @param token The token to parse
   * @param parser the parser which should be used to parse the token
   * @return A node representation of the token
   */
  RenderableNode parse(Token token, Parser parser);

}
