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
import com.mitchellbosecke.pebble.node.BodyNode;
import com.mitchellbosecke.pebble.node.CacheNode;
import com.mitchellbosecke.pebble.node.RenderableNode;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.parser.Parser;

/**
 * Token parser for the cache tag
 *
 * @author Eric Bussieres
 */
public class CacheTokenParser implements TokenParser {

  public static final String TAG_NAME = "cache";

  @Override
  public String getTag() {
    return TAG_NAME;
  }

  @Override
  public RenderableNode parse(Token token, Parser parser) {
    TokenStream stream = parser.getStream();
    int lineNumber = token.getLineNumber();

    // skip over the 'cache' token
    stream.next();

    Expression<?> expression = parser.getExpressionParser().parseExpression();

    // Skip the expression
    stream.next();

    // now we parse the cache body
    BodyNode cacheBody = parser.subparse(tkn -> tkn.test(Token.Type.NAME, "endcache"));

    // skip the 'endcache' token
    stream.next();

    stream.expect(Token.Type.EXECUTE_END);
    return new CacheNode(lineNumber, expression, cacheBody);
  }
}
