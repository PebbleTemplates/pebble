package io.pebbletemplates.pebble;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.loader.StringLoader;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StringInterpolationTest {

  @Test
  void testSimpleVariableInterpolation() throws Exception {
    String source = "{{ \"Hello, #{name}\" }}";
    Map<String, Object> ctx = new HashMap<>();
    ctx.put("name", "joe");
    assertEquals("Hello, joe", this.evaluate(source, ctx));
  }

  @Test
  void testExpressionInterpolation() throws Exception {
    String src = "{{ \"1 plus 2 equals #{1 + 2}\" }}";
    assertEquals("1 plus 2 equals 3", this.evaluate(src));
  }

  @Test
  void testUnclosedInterpolation() throws Exception {
    assertThrows(PebbleException.class, () -> {
      String src = "{{ \" #{ 1 +\" }}";
      this.evaluate(src);
    });
  }

  @Test
  void testDoubleClosedInterpolation() throws Exception {
    String src = "{{ \"#{3}}\" }}";
    assertEquals("3}", this.evaluate(src));
  }

  @Test
  void testFunctionInInterpolation() throws Exception {
    String src = "{{ \"Maximum: #{ max(5, 10) } \" }}";
    assertEquals("Maximum: 10 ", this.evaluate(src));
  }

  @Test
  void testVerbatimInterpolation() throws Exception {
    String src = "{% verbatim %}{{ \"Sum: #{ 1 + 2 }\" }}{% endverbatim %}";
    assertEquals("{{ \"Sum: #{ 1 + 2 }\" }}", this.evaluate(src));
  }

  @Test
  void testInterpolationWithEscapedQuotes() throws Exception {
    String str = "{{ \"The cow says: #{\"\\\"moo\\\"\"}\" }}";
    assertEquals("The cow says: \"moo\"", this.evaluate(str));
  }

  @Test
  void testNestedInterpolation0() throws Exception {
    String src = "{{ \"Nested: #{ outer + \" #{ inner }\" }\" }}";
    Map<String, Object> ctx = new HashMap<>();
    ctx.put("outer", "OUTER");
    ctx.put("inner", "INNER");
    assertEquals("Nested: OUTER INNER", this.evaluate(src, ctx));
  }

  @Test
  void testNestedInterpolation1() throws Exception {
    String src = "{{ \"#{\"#{\"#{'hi'}\"}\"}\" }}";
    assertEquals("hi", this.evaluate(src));
  }

  @Test
  void testInterpolationWhitespace0() throws Exception {
    String src = "{{ \"Outer: #{3+4}\" }}";
    assertEquals("Outer: 7", this.evaluate(src));
  }

  @Test
  void testInterpolationWhitespace1() throws Exception {
    String src = "{{ \"Outer: #{ 3 + 4 }\" }}";
    assertEquals("Outer: 7", this.evaluate(src));
  }

  @Test
  void testInterpolationWhitespace2() throws Exception {
    String src = "{{ \"Outer:#{ 3 + 4 }\" }}";
    assertEquals("Outer:7", this.evaluate(src));
  }

  @Test
  void testInterpolationWhitespace3() throws Exception {
    String src = "{{ \"Outer:#{ 3 + 4 } \" }}";
    assertEquals("Outer:7 ", this.evaluate(src));
  }

  @Test
  void testInterpolationWhitespace4() throws Exception {
    String src = "{{ \"Outer:  #{ 3 + 4 }  \" }}";
    assertEquals("Outer:  7  ", this.evaluate(src));
  }

  @Test
  void testStringWithNumberSigns() throws Exception {
    String src = "{{ \"#bang #crash }!!\" }}";
    assertEquals("#bang #crash }!!", this.evaluate(src));
  }

  @Test
  void testStringWithNumberSignsAndInterpolation() throws Exception {
    String src = "{{ \"The cow said ##{'moo'}#\" }}";
    assertEquals("The cow said #moo#", this.evaluate(src));
  }

  @Test
  void testWhitespaceBetweenNumberSignAndCurlyBrace() throws Exception {
    String src = "{{ \"Green eggs and # {ham}\" }}";
    assertEquals("Green eggs and # {ham}", this.evaluate(src));
  }

  @Test
  void testStringInsideInterpolation() throws Exception {
    String src = "{{ \"Outer: #{ \"inner\" }\" }}";
    assertEquals("Outer: inner", this.evaluate(src));
  }

  @Test
  void testSingleQuoteNoInterpolation() throws Exception {
    String src = "{{ '#{3}'}}";
    assertEquals("#{3}", this.evaluate(src));
  }

  @Test
  void testSingleQuoteInsideInterpolation() throws Exception {
    String src = "{{ \"The cow says: #{'moo' + '#{moo}'}\" }}";
    assertEquals("The cow says: moo#{moo}", this.evaluate(src));
  }

  @Test
  void testSequentialInterpolations0() throws Exception {
    String src = "{{ \"#{1+1}#{2+2}\" }}";
    assertEquals("24", this.evaluate(src));
  }

  @Test
  void testSequentialInterpolations1() throws Exception {
    String src = "{{ \"The #{'cow'} says #{'moo'} and jumps #{'over'} the #{'moon'}\"}}";
    assertEquals("The cow says moo and jumps over the moon", this.evaluate(src));
  }

  @Test
  void testVariableContainingInterplationSyntax() throws Exception {
    String src = "{{ \"Hey #{name}\" }}";
    Map<String, Object> ctx = new HashMap<>();
    ctx.put("name", "#{1+1}");
    assertEquals("Hey #{1+1}", this.evaluate(src, ctx));
  }

  @Test
  void testNewlineInInterpolation() throws Exception {
    String src = "{{ \"Sum = #{ 'egg\negg'}\" }}";
    assertEquals("Sum = egg\negg", this.evaluate(src));
  }

  private String evaluate(String template) throws PebbleException, IOException {
    return this.evaluate(template, null);
  }

  private String evaluate(String template, Map<String, Object> context)
      throws PebbleException, IOException {
    Writer writer = new StringWriter();

    new PebbleEngine.Builder()
        .loader(new StringLoader())
        .strictVariables(false)
        .build()
        .getTemplate(template)
        .evaluate(writer, context);
    return writer.toString();
  }
}
