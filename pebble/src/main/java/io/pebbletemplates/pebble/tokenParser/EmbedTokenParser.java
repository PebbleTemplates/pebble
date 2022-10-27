/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble.tokenParser;

import io.pebbletemplates.pebble.error.ParserException;
import io.pebbletemplates.pebble.lexer.Token;
import io.pebbletemplates.pebble.lexer.TokenStream;
import io.pebbletemplates.pebble.node.BlockNode;
import io.pebbletemplates.pebble.node.EmbedNode;
import io.pebbletemplates.pebble.node.RenderableNode;
import io.pebbletemplates.pebble.node.expression.Expression;
import io.pebbletemplates.pebble.node.expression.MapExpression;
import io.pebbletemplates.pebble.parser.Parser;

import java.util.ArrayList;
import java.util.List;

public class EmbedTokenParser implements TokenParser {

  private BlockTokenParser blockTokenParser = new BlockTokenParser();

  @Override
  public RenderableNode parse(Token token, Parser parser) {
    TokenStream stream = parser.getStream();
    int lineNumber = token.getLineNumber();

    // skip over the 'embed' token
    stream.next();

    Expression<?> embedExpression = parser.getExpressionParser().parseExpression();

    Token current = stream.current();
    MapExpression mapExpression = null;

    // We check if there is an optional 'with' parameter on the embed tag.
    if (current.getType().equals(Token.Type.NAME) && current.getValue().equals("with")) {

      // Skip over 'with'
      stream.next();

      Expression<?> parsedExpression = parser.getExpressionParser().parseExpression();

      if (parsedExpression instanceof MapExpression) {
        mapExpression = (MapExpression) parsedExpression;
      } else {
        throw new ParserException(null,
            String.format("Unexpected expression '%1s'.", parsedExpression
                .getClass().getCanonicalName()), token.getLineNumber(), stream.getFilename());
      }
    }

    stream.expect(Token.Type.EXECUTE_END);

    List<BlockNode> blocks = parseBlocks(token, parser, stream);

    return new EmbedNode(lineNumber, embedExpression, mapExpression, blocks);
  }

  private List<BlockNode> parseBlocks(Token token, Parser parser, TokenStream stream) {
    List<BlockNode> blocks = new ArrayList<>();

    while(true) {
      BlockNode node = parseBlock(token, parser, stream);

      if(node == null) break;

      blocks.add(node);
    }

    return blocks;
  }

  private BlockNode parseBlock(Token token, Parser parser, TokenStream stream) {
    if(stream.current().test(Token.Type.TEXT)) {
      Token textToken = stream.expect(Token.Type.TEXT);
      if(textToken.getValue().trim().length() > 0) {
        throw new ParserException(null, "A template that extends another one cannot include content outside blocks. Did you forget to put the content inside a {% block %} tag?", textToken.getLineNumber(), stream.getFilename());
      }
    }

    stream.expect(Token.Type.EXECUTE_START);

    // we're finished with blocks, expect {% endembed %}
    if(stream.current().test(Token.Type.NAME, "end" + this.getTag())) {
      stream.expect(Token.Type.NAME, "end" + this.getTag());
      stream.expect(Token.Type.EXECUTE_END);
      return null;
    }

    // otherwise start parsing a block tag and let the actual BlockTokenParser do the rest (to make sure it parses the
    // same as top-level blocks)
    return (BlockNode) blockTokenParser.parse(token, parser);
  }

  @Override
  public String getTag() {
    return "embed";
  }
}
