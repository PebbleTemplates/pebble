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
import io.pebbletemplates.pebble.node.BodyNode;
import io.pebbletemplates.pebble.node.CacheNode;
import io.pebbletemplates.pebble.node.RenderableNode;
import io.pebbletemplates.pebble.node.expression.Expression;
import io.pebbletemplates.pebble.parser.Parser;

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
