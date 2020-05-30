package com.mitchellbosecke.pebble.node.expression;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.lexer.LexerImpl;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.node.PrintNode;
import com.mitchellbosecke.pebble.node.RootNode;
import com.mitchellbosecke.pebble.parser.Parser;
import com.mitchellbosecke.pebble.parser.ParserImpl;
import com.mitchellbosecke.pebble.parser.ParserOptions;
import com.mitchellbosecke.pebble.utils.Pair;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StringExpressionParserTest {

  private Pair<Parser, RootNode> testParseExpression(String expression) {
    PebbleEngine pebble = new PebbleEngine.Builder()
        .loader(new StringLoader())
        .strictVariables(false)
        .build();

    LexerImpl lexer = new LexerImpl(
        pebble.getSyntax(),
        pebble.getExtensionRegistry().getUnaryOperators().values(),
        pebble.getExtensionRegistry().getBinaryOperators().values()
    );

    TokenStream tokenStream = lexer.tokenize(new StringReader(expression), "test.peb");

    Parser parser = new ParserImpl(
        pebble.getExtensionRegistry().getUnaryOperators(),
        pebble.getExtensionRegistry().getBinaryOperators(),
        pebble.getExtensionRegistry().getTokenParsers(),
        new ParserOptions()
    );

    return new Pair<>(parser, parser.parse(tokenStream));
  }

  /*
   * Sequential String literals are not allowed, a syntax error should be thrown
   */
  @Test
  void testSequentialStrings_singleQuotes_isSyntaxError() throws IOException {
    assertThrows(ParserException.class, () -> this.testParseExpression("{{ 'one' 'two' }}"));
  }

  /*
   * Sequential String literals are not allowed, a syntax error should be thrown
   */
  @Test
  void testSequentialStrings_doubleQuotes_isSyntaxError() throws IOException {
    assertThrows(ParserException.class, () -> this.testParseExpression("{{ \"one\" \"two\" }}"));
  }

  /*
   * + parses as Add expression. AST should be:
   *
   * AddExpression
   *      LiteralStringExpression('one')
   *      LiteralStringExpression('two')
   */
  @Test
  void testValidExpression_singleQuotes() throws IOException {
    Pair<Parser, RootNode> underTest = this.testParseExpression("{{ 'one' + 'two' }}");

    PrintNode printNode = (PrintNode) underTest.getRight().getBody().getChildren().get(0);
    Expression<?> expr = printNode.getExpression();

    assertThat(expr).isInstanceOf(AddExpression.class);

    Expression<?> leftExpression = ((AddExpression) expr).getLeftExpression();
    Expression<?> rightExpression = ((AddExpression) expr).getRightExpression();

    assertThat(leftExpression).isInstanceOf(LiteralStringExpression.class);
    assertThat(((LiteralStringExpression) leftExpression).getValue()).isEqualTo("one");

    assertThat(rightExpression).isInstanceOf(LiteralStringExpression.class);
    assertThat(((LiteralStringExpression) rightExpression).getValue()).isEqualTo("two");
  }

  /*
   * + parses as Add expression. AST should be:
   *
   * AddExpression
   *      LiteralStringExpression('one')
   *      LiteralStringExpression('two')
   */
  @Test
  void testValidExpression_doubleQuotes() throws IOException {
    Pair<Parser, RootNode> underTest = this.testParseExpression("{{ \"one\" + \"two\" }}");

    PrintNode printNode = (PrintNode) underTest.getRight().getBody().getChildren().get(0);
    Expression<?> expr = printNode.getExpression();

    assertThat(expr).isInstanceOf(AddExpression.class);

    Expression<?> leftExpression = ((AddExpression) expr).getLeftExpression();
    Expression<?> rightExpression = ((AddExpression) expr).getRightExpression();

    assertThat(leftExpression).isInstanceOf(LiteralStringExpression.class);
    assertThat(((LiteralStringExpression) leftExpression).getValue()).isEqualTo("one");

    assertThat(rightExpression).isInstanceOf(LiteralStringExpression.class);
    assertThat(((LiteralStringExpression) rightExpression).getValue()).isEqualTo("two");
  }

  /*
   * ~ parses as Concatenate expression. AST should be:
   *
   * ConcatenateExpression
   *      LiteralStringExpression('one')
   *      LiteralStringExpression('two')
   */
  @Test
  void testValidConcatenationExpression_singleQuotes() throws IOException {
    Pair<Parser, RootNode> underTest = this.testParseExpression("{{ 'one' ~ 'two' }}");

    PrintNode printNode = (PrintNode) underTest.getRight().getBody().getChildren().get(0);
    Expression<?> expr = printNode.getExpression();

    assertThat(expr).isInstanceOf(ConcatenateExpression.class);

    Expression<?> leftExpression = ((ConcatenateExpression) expr).getLeftExpression();
    Expression<?> rightExpression = ((ConcatenateExpression) expr).getRightExpression();

    assertThat(leftExpression).isInstanceOf(LiteralStringExpression.class);
    assertThat(((LiteralStringExpression) leftExpression).getValue()).isEqualTo("one");

    assertThat(rightExpression).isInstanceOf(LiteralStringExpression.class);
    assertThat(((LiteralStringExpression) rightExpression).getValue()).isEqualTo("two");
  }

  /*
   * ~ parses as Concatenate expression. AST should be:
   *
   * ConcatenateExpression
   *      LiteralStringExpression('one')
   *      LiteralStringExpression('two')
   */
  @Test
  void testValidConcatenationExpression_doubleQuotes() throws IOException {
    Pair<Parser, RootNode> underTest = this.testParseExpression("{{ \"one\" ~ \"two\" }}");

    PrintNode printNode = (PrintNode) underTest.getRight().getBody().getChildren().get(0);
    Expression<?> expr = printNode.getExpression();

    assertThat(expr).isInstanceOf(ConcatenateExpression.class);

    Expression<?> leftExpression = ((ConcatenateExpression) expr).getLeftExpression();
    Expression<?> rightExpression = ((ConcatenateExpression) expr).getRightExpression();

    assertThat(leftExpression).isInstanceOf(LiteralStringExpression.class);
    assertThat(((LiteralStringExpression) leftExpression).getValue()).isEqualTo("one");

    assertThat(rightExpression).isInstanceOf(LiteralStringExpression.class);
    assertThat(((LiteralStringExpression) rightExpression).getValue()).isEqualTo("two");
  }

  /*
   * Single-quotes does not parse interpolations. AST should be:
   *
   * LiteralStringExpression('one #{two} three')
   */
  @Test
  void testValidInterpolationExpression_singleQuotes() throws IOException {
    Pair<Parser, RootNode> underTest = this.testParseExpression("{{ 'one #{two} three' }}");

    PrintNode printNode = (PrintNode) underTest.getRight().getBody().getChildren().get(0);
    Expression<?> expr = printNode.getExpression();

    assertThat(expr).isInstanceOf(LiteralStringExpression.class);
    assertThat(((LiteralStringExpression) expr).getValue()).isEqualTo("one #{two} three");
  }

  /*
   * Double-quotes parses interpolations. AST should be:
   *
   * ConcatenateExpression
   *      LiteralStringExpression('one ')
   *      ConcatenateExpression
   *          ContextVariableExpression(two)
   *          LiteralStringExpression(' three')
   */
  @Test
  void testValidInterpolationExpression_doubleQuotes() throws IOException {
    Pair<Parser, RootNode> underTest = this.testParseExpression("{{ \"one #{two} three\" }}");

    PrintNode printNode = (PrintNode) underTest.getRight().getBody().getChildren().get(0);
    Expression<?> expr = printNode.getExpression();

    assertThat(expr).isInstanceOf(ConcatenateExpression.class);

    Expression<?> leftExpression = ((ConcatenateExpression) expr).getLeftExpression();
    Expression<?> rightExpression = ((ConcatenateExpression) expr).getRightExpression();

    assertThat(leftExpression).isInstanceOf(LiteralStringExpression.class);
    assertThat(((LiteralStringExpression) leftExpression).getValue()).isEqualTo("one ");

    assertThat(rightExpression).isInstanceOf(ConcatenateExpression.class);

    Expression<?> right_leftSubExpression = ((ConcatenateExpression) rightExpression).getLeftExpression();
    assertThat(((ContextVariableExpression) right_leftSubExpression).getName()).isEqualTo("two");

    Expression<?> right_rightSubExpression = ((ConcatenateExpression) rightExpression).getRightExpression();
        assertThat(((LiteralStringExpression) right_rightSubExpression).getValue()).isEqualTo(" three");
    }
}
