package com.mitchellbosecke.pebble.tokenParser;

import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.lexer.Token;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.node.FromNode;
import com.mitchellbosecke.pebble.node.RenderableNode;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.parser.Parser;
import com.mitchellbosecke.pebble.utils.Pair;
import java.util.ArrayList;
import java.util.List;

/**
 * From Token parser for
 *
 * <p>
 * {% from "templateName" import macroName as alias %}
 * <p>
 *
 * @author yanxiyue
 */
public class FromTokenParser implements TokenParser {

  @Override
  public RenderableNode parse(Token token, Parser parser) {

    TokenStream stream = parser.getStream();
    int lineNumber = token.getLineNumber();

    // skip over the 'from' token
    stream.next();

    // parse the teamplateName expression
    Expression<?> fromExpression = parser.getExpressionParser().parseExpression();

    // parse the import
    List<Pair<String, String>> namedMacros = parseNamedMacros(parser);

    stream.expect(Token.Type.EXECUTE_END);

    return new FromNode(lineNumber, fromExpression, namedMacros);
  }

  private List<Pair<String, String>> parseNamedMacros(Parser parser) {
    List<Pair<String, String>> pairs = new ArrayList<>();
    TokenStream stream = parser.getStream();

    stream.expect(Token.Type.NAME, "import");

    Token pre, post;
    while (!stream.current().getType().equals(Token.Type.EXECUTE_END)) {
      pre = stream.expect(Token.Type.NAME);
      if (stream.current().test(Token.Type.NAME, "as")) {
        // Skips over 'as'
        stream.next();

        post = stream.expect(Token.Type.NAME);

        pairs.add(new Pair<>(post.getValue(), pre.getValue()));
      } else {
        pairs.add(new Pair<>(pre.getValue(), pre.getValue()));
      }

      Token token = stream.current();
      if (token.test(Token.Type.PUNCTUATION, ",")) {
        // Skips over ','
        stream.next();
      } else if (token.getType().equals(Token.Type.EXECUTE_END)) {
        break;

      } else {
        String message = String.format(
            "Unexpected token of value \"%s\" and type %s, expected token of type %s or ',' ",
            token.getValue(), token.getType().toString(), Token.Type.EXECUTE_END);
        throw new ParserException(null, message, token.getLineNumber(), stream.getFilename());
      }
    }

    return pairs;
  }

  @Override
  public String getTag() {
    return "from";
  }
}
