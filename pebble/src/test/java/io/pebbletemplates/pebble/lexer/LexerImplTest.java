package io.pebbletemplates.pebble.lexer;

import io.pebbletemplates.pebble.extension.ExtensionRegistry;
import io.pebbletemplates.pebble.extension.core.CoreExtension;
import io.pebbletemplates.pebble.loader.Loader;
import io.pebbletemplates.pebble.loader.StringLoader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;

import static org.assertj.core.api.Assertions.assertThat;

class LexerImplTest {

  @SuppressWarnings("unused")
  private final Logger logger = LoggerFactory.getLogger(LexerImplTest.class);

  /**
   * The Lexer Implementation under test.
   */
  private LexerImpl lexer;

  // Don't really care what the template name is, but need to have it.
  // Defined here to remove clutter from the tests
  private final String TEMPLATE_NAME = "Template Name";

  /**
   * Configure and instantiate a Lexer instance before each test.
   */
  @BeforeEach
  void setup() {
    Syntax syntax = new Syntax.Builder().setEnableNewLineTrimming(false).build();

    ExtensionRegistry extensionRegistry = new ExtensionRegistry();
    extensionRegistry.addExtension(new CoreExtension());

    this.lexer = new LexerImpl(syntax,
        extensionRegistry.getUnaryOperators().values(),
        extensionRegistry.getBinaryOperators().values());
  }

  /**
   * Test Tokenizing text.
   */
  @Test
  void testTokenizeText() {
    Loader<String> loader = new StringLoader();
    Reader templateReader = loader.getReader(" template content ");

    TokenStream tokenStream = this.lexer.tokenize(templateReader, this.TEMPLATE_NAME);

    assertThat(tokenStream.peek(0).getType()).isEqualTo(Token.Type.TEXT);
    assertThat(tokenStream.peek(0).getValue()).isEqualTo(" template content ");
    assertThat(tokenStream.peek(1).getType()).isEqualTo(Token.Type.EOF);
  }

  /**
   * Test tokenizing an expression, e.g. {{ expression }}
   */
  @Test
  void testTokenizeExpression() {
    Loader<String> loader = new StringLoader();
    Reader templateReader = loader.getReader("{{ whatever }}");

    TokenStream tokenStream = this.lexer.tokenize(templateReader, this.TEMPLATE_NAME);

    assertThat(tokenStream.peek(0).getType()).isEqualTo(Token.Type.PRINT_START);
    assertThat(tokenStream.peek(0).getValue()).isNull();

    assertThat(tokenStream.peek(1).getType()).isEqualTo(Token.Type.NAME);
    assertThat(tokenStream.peek(1).getValue()).isEqualTo("whatever");

    assertThat(tokenStream.peek(2).getType()).isEqualTo(Token.Type.PRINT_END);
    assertThat(tokenStream.peek(2).getValue()).isEqualTo("}}");

    assertThat(tokenStream.peek(3).getType()).isEqualTo(Token.Type.EOF);
    assertThat(tokenStream.peek(3).getValue()).isNull();
  }

  /**
   * Test tokenizing an expression, e.g. {{ expression }}
   */
  @Test
  void testVariableNameStartingWithOperator() {
    Loader<String> loader = new StringLoader();
    Reader templateReader = loader.getReader("{{ is_active + contains0 }}");

    TokenStream tokenStream = this.lexer.tokenize(templateReader, this.TEMPLATE_NAME);

    int i = 0;
    assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.PRINT_START);
    assertThat(tokenStream.peek(i++).getValue()).isNull();

    assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.NAME);
    assertThat(tokenStream.peek(i++).getValue()).isEqualTo("is_active");

    assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.OPERATOR);
    assertThat(tokenStream.peek(i++).getValue()).isEqualTo("+");

    assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.NAME);
    assertThat(tokenStream.peek(i++).getValue()).isEqualTo("contains0");

    assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.PRINT_END);
    assertThat(tokenStream.peek(i++).getValue()).isEqualTo("}}");

    assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.EOF);
    assertThat(tokenStream.peek(i++).getValue()).isNull();
  }

  /**
   * Test tokenizing Punctuation, such as the dot in item.itemType
   */
  @Test
  void testPunctuation() {

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder
        .append("{% if item.itemType %}");
    Loader<String> loader = new StringLoader();
    Reader templateReader = loader.getReader(stringBuilder.toString());

    TokenStream tokenStream = this.lexer.tokenize(templateReader, this.TEMPLATE_NAME);

    int i = 0;
    assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.EXECUTE_START);
    assertThat(tokenStream.peek(i++).getValue()).isNull();

    assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.NAME);
    assertThat(tokenStream.peek(i++).getValue()).isEqualTo("if");

    assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.NAME);
    assertThat(tokenStream.peek(i++).getValue()).isEqualTo("item");

    assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.PUNCTUATION);
    assertThat(tokenStream.peek(i++).getValue()).isEqualTo(".");

    assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.NAME);
    assertThat(tokenStream.peek(i++).getValue()).isEqualTo("itemType");

    assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.EXECUTE_END);
    assertThat(tokenStream.peek(i++).getValue()).isEqualTo("%}");
	
	assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.EOF);
	assertThat(tokenStream.peek(i++).getValue()).isNull(); 
  }

  /**
   * Test tokenizing an if statement that includes an operation and a String token
   */
  @Test
  void testIfStatementWithOperatorAndStringToken() {

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder
        .append("{% if item equals \"string1\" %}");
    Loader<String> loader = new StringLoader();
    Reader templateReader = loader.getReader(stringBuilder.toString());

    TokenStream tokenStream = this.lexer.tokenize(templateReader, this.TEMPLATE_NAME);

    int i = 0;
    assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.EXECUTE_START);
    assertThat(tokenStream.peek(i++).getValue()).isNull();

    assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.NAME);
    assertThat(tokenStream.peek(i++).getValue()).isEqualTo("if");

    assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.NAME);
    assertThat(tokenStream.peek(i++).getValue()).isEqualTo("item");

    assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.OPERATOR);
    assertThat(tokenStream.peek(i++).getValue()).isEqualTo("equals");

    assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.STRING);
    assertThat(tokenStream.peek(i++).getValue()).isEqualTo("string1");

    assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.EXECUTE_END);
    assertThat(tokenStream.peek(i++).getValue()).isEqualTo("%}");
  }

  /**
   * Test tokenize an "if" statement
   */
  @Test
  void testIfStatement() {

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder
        .append("{% if item equals \"whatever\" %}\n")
        .append("some text\n")
        .append("{% endif %}");
    Loader<String> loader = new StringLoader();
    Reader templateReader = loader.getReader(stringBuilder.toString());

    TokenStream tokenStream = this.lexer.tokenize(templateReader, this.TEMPLATE_NAME);

    int i = 0;
    assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.EXECUTE_START);
    assertThat(tokenStream.peek(i++).getValue()).isNull();

    assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.NAME);
    assertThat(tokenStream.peek(i++).getValue()).isEqualTo("if");

    assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.NAME);
    assertThat(tokenStream.peek(i++).getValue()).isEqualTo("item");

    assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.OPERATOR);
    assertThat(tokenStream.peek(i++).getValue()).isEqualTo("equals");

    assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.STRING);
    assertThat(tokenStream.peek(i++).getValue()).isEqualTo("whatever");

    assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.EXECUTE_END);
    assertThat(tokenStream.peek(i++).getValue()).isEqualTo("%}");

	assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.TEXT);
	assertThat(tokenStream.peek(i++).getValue()).isEqualTo("\nsome text\n");
	
	assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.EXECUTE_START);
	assertThat(tokenStream.peek(i++).getValue()).isNull(); 
	
	assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.NAME);
	assertThat(tokenStream.peek(i++).getValue()).isEqualTo("endif");
	
	assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.EXECUTE_END);
	assertThat(tokenStream.peek(i++).getValue()).isEqualTo("%}");
	
	assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.EOF);
	assertThat(tokenStream.peek(i++).getValue()).isNull(); 
  }

  /**
   * Test tokenizing a For Loop
   */
  @Test
  void testTokenizeForLoop() {

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder
        .append("{% for item in items %}\n")
        .append("stuff\n")
        .append("{% endfor %}");
    Loader<String> loader = new StringLoader();
    Reader templateReader = loader.getReader(stringBuilder.toString());

    TokenStream tokenStream = this.lexer.tokenize(templateReader, this.TEMPLATE_NAME);

    assertThat(tokenStream.peek(0).getType()).isEqualTo(Token.Type.EXECUTE_START);
    assertThat(tokenStream.peek(0).getValue()).isNull();

    assertThat(tokenStream.peek(1).getType()).isEqualTo(Token.Type.NAME);
    assertThat(tokenStream.peek(1).getValue()).isEqualTo("for");

    assertThat(tokenStream.peek(2).getType()).isEqualTo(Token.Type.NAME);
    assertThat(tokenStream.peek(2).getValue()).isEqualTo("item");

    assertThat(tokenStream.peek(3).getType()).isEqualTo(Token.Type.NAME);
    assertThat(tokenStream.peek(3).getValue()).isEqualTo("in");

    assertThat(tokenStream.peek(4).getType()).isEqualTo(Token.Type.NAME);
    assertThat(tokenStream.peek(4).getValue()).isEqualTo("items");

    assertThat(tokenStream.peek(5).getType()).isEqualTo(Token.Type.EXECUTE_END);
    assertThat(tokenStream.peek(5).getValue()).isEqualTo("%}");
	
	assertThat(tokenStream.peek(6).getType()).isEqualTo(Token.Type.TEXT);
	assertThat(tokenStream.peek(6).getValue()).isEqualTo("\nstuff\n");
	
	assertThat(tokenStream.peek(7).getType()).isEqualTo(Token.Type.EXECUTE_START);
	assertThat(tokenStream.peek(7).getValue()).isNull(); 
	
	assertThat(tokenStream.peek(8).getType()).isEqualTo(Token.Type.NAME);
	assertThat(tokenStream.peek(8).getValue()).isEqualTo("endfor");
	
	assertThat(tokenStream.peek(9).getType()).isEqualTo(Token.Type.EXECUTE_END);
	assertThat(tokenStream.peek(9).getValue()).isEqualTo("%}");
	
	assertThat(tokenStream.peek(10).getType()).isEqualTo(Token.Type.EOF);
	assertThat(tokenStream.peek(10).getValue()).isNull(); 
  }

  /**
   * Test tokenizing an if statement with a Whitespace Control character, i.e. the "-"
   */
  @Test
  void testIfStatementWithWhitespaceControl() {

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder
        .append("{% if item equals \"whatever\" -%}\n")
        .append("some text\n")
        .append("{%- endif %}");
    Loader<String> loader = new StringLoader();
    Reader templateReader = loader.getReader(stringBuilder.toString());

    TokenStream tokenStream = this.lexer.tokenize(templateReader, this.TEMPLATE_NAME);

    int i = 0;
    assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.EXECUTE_START);
    assertThat(tokenStream.peek(i++).getValue()).isNull();

    assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.NAME);
    assertThat(tokenStream.peek(i++).getValue()).isEqualTo("if");

    assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.NAME);
    assertThat(tokenStream.peek(i++).getValue()).isEqualTo("item");

    assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.OPERATOR);
    assertThat(tokenStream.peek(i++).getValue()).isEqualTo("equals");

    assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.STRING);
    assertThat(tokenStream.peek(i++).getValue()).isEqualTo("whatever");

    assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.EXECUTE_END);
    assertThat(tokenStream.peek(i++).getValue()).isEqualTo("%}");

	assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.TEXT);
	assertThat(tokenStream.peek(i++).getValue()).isEqualTo("some text");
	
	assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.EXECUTE_START);
	assertThat(tokenStream.peek(i++).getValue()).isNull(); 
	
	assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.NAME);
	assertThat(tokenStream.peek(i++).getValue()).isEqualTo("endif");
	
	assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.EXECUTE_END);
	assertThat(tokenStream.peek(i++).getValue()).isEqualTo("%}");
	
	assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.EOF);
	assertThat(tokenStream.peek(i++).getValue()).isNull(); 
  }

  /**
   * Test a combination of template syntax to demonstrate a complex token stream
   */
  @Test
  void testComplexTemplate() {

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder
        .append("text before for loop followed by blank line\n")
        .append("{% for item in items %}\n")
        .append("{% if item.itemType equals \"ITEM_TYPE1\" -%}\n")
        .append("Item 1\n")
        .append("{% elseif item.itemType equals \"ITEM_TYPE2\" -%}\n")
        .append("Item 2\n")
        .append("{%- endif -%}")
        .append("{% endfor -%}")
        .append("text after for loop preceded by blank line");
    Loader<String> loader = new StringLoader();
    Reader templateReader = loader.getReader(stringBuilder.toString());

    TokenStream tokenStream = this.lexer.tokenize(templateReader, this.TEMPLATE_NAME);

    int i = 0;
    assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.TEXT);
    assertThat(tokenStream.peek(i++).getValue()).isEqualTo("text before for loop followed by blank line\n");

    assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.EXECUTE_START);
    assertThat(tokenStream.peek(i++).getValue()).isNull();

    assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.NAME);
    assertThat(tokenStream.peek(i++).getValue()).isEqualTo("for");

    assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.NAME);
    assertThat(tokenStream.peek(i++).getValue()).isEqualTo("item");

    assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.NAME);
    assertThat(tokenStream.peek(i++).getValue()).isEqualTo("in");

    assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.NAME);
    assertThat(tokenStream.peek(i++).getValue()).isEqualTo("items");
	
	assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.EXECUTE_END);
	assertThat(tokenStream.peek(i++).getValue()).isEqualTo("%}");
	
	// note that the new line character included as part of a TEXT token
	assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.TEXT);
	assertThat(tokenStream.peek(i++).getValue()).isEqualTo("\n");
	
	assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.EXECUTE_START);
	assertThat(tokenStream.peek(i++).getValue()).isNull(); 
	
	assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.NAME);
	assertThat(tokenStream.peek(i++).getValue()).isEqualTo("if");
	
	assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.NAME);
	assertThat(tokenStream.peek(i++).getValue()).isEqualTo("item");
	
	assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.PUNCTUATION);
	assertThat(tokenStream.peek(i++).getValue()).isEqualTo(".");
	
	assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.NAME);
	assertThat(tokenStream.peek(i++).getValue()).isEqualTo("itemType");
	
	assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.OPERATOR);
	assertThat(tokenStream.peek(i++).getValue()).isEqualTo("equals");
	
	assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.STRING);
	assertThat(tokenStream.peek(i++).getValue()).isEqualTo("ITEM_TYPE1");
	
	assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.EXECUTE_END);
	assertThat(tokenStream.peek(i++).getValue()).isEqualTo("%}"); 
	
	assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.TEXT);
	assertThat(tokenStream.peek(i++).getValue()).isEqualTo("Item 1\n");
	
	assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.EXECUTE_START);
	assertThat(tokenStream.peek(i++).getValue()).isNull(); 
	
	assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.NAME);
	assertThat(tokenStream.peek(i++).getValue()).isEqualTo("elseif");
	
	assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.NAME);
	assertThat(tokenStream.peek(i++).getValue()).isEqualTo("item");
	
	assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.PUNCTUATION);
	assertThat(tokenStream.peek(i++).getValue()).isEqualTo(".");
	
	assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.NAME);
	assertThat(tokenStream.peek(i++).getValue()).isEqualTo("itemType");
	
	assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.OPERATOR);
	assertThat(tokenStream.peek(i++).getValue()).isEqualTo("equals");
	
	assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.STRING);
	assertThat(tokenStream.peek(i++).getValue()).isEqualTo("ITEM_TYPE2");
	
	assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.EXECUTE_END);
	assertThat(tokenStream.peek(i++).getValue()).isEqualTo("%}");
	
	assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.TEXT);
	assertThat(tokenStream.peek(i++).getValue()).isEqualTo("Item 2");
	
	assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.EXECUTE_START);
	assertThat(tokenStream.peek(i++).getValue()).isNull(); 
	
	assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.NAME);
	assertThat(tokenStream.peek(i++).getValue()).isEqualTo("endif");
	
	assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.EXECUTE_END);
	assertThat(tokenStream.peek(i++).getValue()).isEqualTo("%}");
	
	assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.EXECUTE_START);
	assertThat(tokenStream.peek(i++).getValue()).isNull(); 

	assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.NAME);
	assertThat(tokenStream.peek(i++).getValue()).isEqualTo("endfor");
	
	assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.EXECUTE_END);
	assertThat(tokenStream.peek(i++).getValue()).isEqualTo("%}");
	
	assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.TEXT);
	assertThat(tokenStream.peek(i++).getValue()).isEqualTo("text after for loop preceded by blank line");
	
	assertThat(tokenStream.peek(i).getType()).isEqualTo(Token.Type.EOF);
	assertThat(tokenStream.peek(i++).getValue()).isNull(); 
  }
  
}
