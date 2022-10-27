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
import io.pebbletemplates.node.BodyNode;
import io.pebbletemplates.node.CacheNode;
import io.pebbletemplates.node.RenderableNode;
import io.pebbletemplates.node.expression.Expression;
import io.pebbletemplates.parser.Parser;

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
