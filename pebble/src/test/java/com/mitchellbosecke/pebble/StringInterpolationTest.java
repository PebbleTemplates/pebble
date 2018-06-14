package com.mitchellbosecke.pebble;

import static org.junit.Assert.assertEquals;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.StringLoader;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class StringInterpolationTest {

  @Test
  public void testSimpleVariableInterpolation() throws Exception {
    String source = "{{ \"Hello, #{name}\" }}";
    Map<String, Object> ctx = new HashMap<>();
    ctx.put("name", "joe");
    assertEquals("Hello, joe", evaluate(source, ctx));
  }

  @Test
  public void testExpressionInterpolation() throws Exception {
    String src = "{{ \"1 plus 2 equals #{1 + 2}\" }}";
    assertEquals("1 plus 2 equals 3", evaluate(src));
  }

  @Test(expected = PebbleException.class)
  public void testUnclosedInterpolation() throws Exception {
    String src = "{{ \" #{ 1 +\" }}";

    evaluate(src);
  }

  @Test
  public void testDoubleClosedInterpolation() throws Exception {
    String src = "{{ \"#{3}}\" }}";
    assertEquals("3}", evaluate(src));
  }

  @Test
  public void testFunctionInInterpolation() throws Exception {
    String src = "{{ \"Maximum: #{ max(5, 10) } \" }}";
    assertEquals("Maximum: 10 ", evaluate(src));
  }

  @Test
  public void testVerbatimInterpolation() throws Exception {
    String src = "{% verbatim %}{{ \"Sum: #{ 1 + 2 }\" }}{% endverbatim %}";
    assertEquals("{{ \"Sum: #{ 1 + 2 }\" }}", evaluate(src));
  }

  @Test
  public void testInterpolationWithEscapedQuotes() throws Exception {
    String str = "{{ \"The cow says: #{\"\\\"moo\\\"\"}\" }}";
    assertEquals("The cow says: \"moo\"", evaluate(str));
  }

  @Test
  public void testNestedInterpolation0() throws Exception {
    String src = "{{ \"Nested: #{ outer + \" #{ inner }\" }\" }}";
    Map<String, Object> ctx = new HashMap<>();
    ctx.put("outer", "OUTER");
    ctx.put("inner", "INNER");
    assertEquals("Nested: OUTER INNER", evaluate(src, ctx));
  }

  @Test
  public void testNestedInterpolation1() throws Exception {
    String src = "{{ \"#{\"#{\"#{'hi'}\"}\"}\" }}";
    assertEquals("hi", evaluate(src));
  }

  @Test
  public void testInterpolationWhitespace0() throws Exception {
    String src = "{{ \"Outer: #{3+4}\" }}";
    assertEquals("Outer: 7", evaluate(src));
  }

  @Test
  public void testInterpolationWhitespace1() throws Exception {
    String src = "{{ \"Outer: #{ 3 + 4 }\" }}";
    assertEquals("Outer: 7", evaluate(src));
  }

  @Test
  public void testInterpolationWhitespace2() throws Exception {
    String src = "{{ \"Outer:#{ 3 + 4 }\" }}";
    assertEquals("Outer:7", evaluate(src));
  }

  @Test
  public void testInterpolationWhitespace3() throws Exception {
    String src = "{{ \"Outer:#{ 3 + 4 } \" }}";
    assertEquals("Outer:7 ", evaluate(src));
  }

  @Test
  public void testInterpolationWhitespace4() throws Exception {
    String src = "{{ \"Outer:  #{ 3 + 4 }  \" }}";
    assertEquals("Outer:  7  ", evaluate(src));
  }

  @Test
  public void testStringWithNumberSigns() throws Exception {
    String src = "{{ \"#bang #crash }!!\" }}";
    assertEquals("#bang #crash }!!", evaluate(src));
  }

  @Test
  public void testStringWithNumberSignsAndInterpolation() throws Exception {
    String src = "{{ \"The cow said ##{'moo'}#\" }}";
    assertEquals("The cow said #moo#", evaluate(src));
  }

  @Test
  public void testWhitespaceBetweenNumberSignAndCurlyBrace() throws Exception {
    String src = "{{ \"Green eggs and # {ham}\" }}";
    assertEquals("Green eggs and # {ham}", evaluate(src));
  }

  @Test
  public void testStringInsideInterpolation() throws Exception {
    String src = "{{ \"Outer: #{ \"inner\" }\" }}";
    assertEquals("Outer: inner", evaluate(src));
  }

  @Test
  public void testSingleQuoteNoInterpolation() throws Exception {
    String src = "{{ '#{3}'}}";
    assertEquals("#{3}", evaluate(src));
  }

  @Test
  public void testSingleQuoteInsideInterpolation() throws Exception {
    String src = "{{ \"The cow says: #{'moo' + '#{moo}'}\" }}";
    assertEquals("The cow says: moo#{moo}", evaluate(src));
  }

  @Test
  public void testSequentialInterpolations0() throws Exception {
    String src = "{{ \"#{1+1}#{2+2}\" }}";
    assertEquals("24", evaluate(src));
  }

  @Test
  public void testSequentialInterpolations1() throws Exception {
    String src = "{{ \"The #{'cow'} says #{'moo'} and jumps #{'over'} the #{'moon'}\"}}";
    assertEquals("The cow says moo and jumps over the moon", evaluate(src));
  }

  @Test
  public void testVariableContainingInterplationSyntax() throws Exception {
    String src = "{{ \"Hey #{name}\" }}";
    Map<String, Object> ctx = new HashMap<>();
    ctx.put("name", "#{1+1}");
    assertEquals("Hey #{1+1}", evaluate(src, ctx));
  }

  @Test
  public void testNewlineInInterpolation() throws Exception {
    String src = "{{ \"Sum = #{ 'egg\negg'}\" }}";
    assertEquals("Sum = egg\negg", evaluate(src));
  }

  private String evaluate(String template) throws PebbleException, IOException {
    return evaluate(template, null);
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
