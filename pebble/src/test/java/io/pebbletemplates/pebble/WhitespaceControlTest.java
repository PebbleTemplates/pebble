/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.loader.StringLoader;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WhitespaceControlTest {

  /**
   * A windows newline character (i.e. \n\r) in a template should be recognized
   * and output as as a Windows newline character. The Windows newline character
   * should not be converted to a Unix newline character (i.e. \n).
   */
  @Test
  void testWindowsNewlineCharacter() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(true).build();

    PebbleTemplate windowsTemplate = pebble.getTemplate("\r\n");
    Writer windowsWriter = new StringWriter();
    windowsTemplate.evaluate(windowsWriter);
    assertEquals("\r\n", windowsWriter.toString());
  }

  /**
   * A Unix newline character (i.e. \n\r) in a template should be recognized
   * and output as as a Unix newline character. The Unix newline character
   * should not be converted to a Windows newline character (i.e. \r\n).
   */
  @Test
  void testUnixNewlineCharacter() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(true).build();

    PebbleTemplate unixTemplate = pebble.getTemplate("\n");
    Writer unixWriter = new StringWriter();
    unixTemplate.evaluate(unixWriter);
    assertEquals("\n", unixWriter.toString());
  }

  /**
   * A leading Whitespace Control Modifier in an expression delimiter (i.e. Pebble variable reference)
   * should remove whitespace before the variable reference on the same line up to any
   * surrounding text, i.e. a print delimiter.
   *
   * @throws PebbleException
   * @throws IOException
   */
  @Test
  void testLeadingWhitespaceControlModifier() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(true).build();

    PebbleTemplate template = pebble.getTemplate("<li>    	{{- foo }}</li>");
    Writer writer = new StringWriter();

    Map<String, Object> context = new HashMap<>();
    context.put("foo", "bar");
    template.evaluate(writer, context);
    assertEquals("<li>bar</li>", writer.toString());
  }

  /**
   * A trailing Whitespace Control Modifier in an expression delimiter (i.e. Pebble variable reference)
   * should remove whitespace after the variable reference on the same line up to any
   * surrounding text.
   *
   * @throws PebbleException
   * @throws IOException
   */
  @Test
  void testTrailingWhitespaceControlModifier() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(true).build();

    PebbleTemplate template = pebble.getTemplate("<li>{{ foo -}}   	</li>");
    Writer writer = new StringWriter();

    Map<String, Object> context = new HashMap<>();
    context.put("foo", "bar");
    template.evaluate(writer, context);
    assertEquals("<li>bar</li>", writer.toString());
  }

  /**
   * A Whitespace Control Modifier in an expression delimiter (i.e. Pebble variable reference)
   * should not have any effect if there is no whitespace immediately before or after the 
   * variable reference. 
   *
   * @throws PebbleException
   * @throws IOException
   */
  @Test
  void testLeadingWhitespaceTrimWithoutOutsideText() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(true).build();

    PebbleTemplate template = pebble.getTemplate("{{- foo -}}");
    Writer writer = new StringWriter();

    Map<String, Object> context = new HashMap<>();
    context.put("foo", "bar");
    template.evaluate(writer, context);
    assertEquals("bar", writer.toString());
  }

  /**
   * A leading and trailing Whitespace Control Modifiers in an expression delimiter 
   * (i.e. Pebble variable reference) should remove whitespace before and after 
   * the variable reference on the same line up to any surrounding text.
   *
   * @throws PebbleException
   * @throws IOException
   */
  @Test
  void testLeadingAndTrailingWhitespaceControlModifier() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(true).build();

    PebbleTemplate template = pebble
        .getTemplate("{{ foo }} <li>   {{- foo -}}   	</li> {{ foo }}");
    Writer writer = new StringWriter();

    Map<String, Object> context = new HashMap<>();
    context.put("foo", "bar");
    template.evaluate(writer, context);
    assertEquals("bar <li>bar</li> bar", writer.toString());
  }

  /**
   * Newline characters immediately before or after a Whitespace Control Modifier 
   * should be removed.
   *
   * @throws PebbleException
   * @throws IOException
   */
  @Test
  void testWhitespaceControlModifierRemovesNewlines() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(true).build();

    PebbleTemplate template = pebble.getTemplate("<li>\n{{- foo -}}\n</li>");
    Writer writer = new StringWriter();

    Map<String, Object> context = new HashMap<>();
    context.put("foo", "bar");
    template.evaluate(writer, context);
    assertEquals("<li>bar</li>", writer.toString());
  }

  @Test
  void testWhitespaceTrimWithExecuteDelimiter() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(true).build();

    PebbleTemplate template = pebble
        .getTemplate("<li>    	{%- if true %} success {% else %} fail {% endif -%}   	</li>");
    Writer writer = new StringWriter();

    Map<String, Object> context = new HashMap<>();
    context.put("foo", "bar");
    template.evaluate(writer, context);
    assertEquals("<li> success </li>", writer.toString());
  }

  @Test
  void testLeadingWhitespaceTrimWithCommentDelimiter() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(true).build();

    PebbleTemplate template = pebble.getTemplate("<li>    	{#- comment #}</li>");
    Writer writer = new StringWriter();

    template.evaluate(writer);
    assertEquals("<li></li>", writer.toString());
  }

  @Test
  void testTrailingWhitespaceTrimWithCommentDelimiter() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(true).build();

    PebbleTemplate template = pebble.getTemplate("<li>{# comment -#}   	</li>");
    Writer writer = new StringWriter();

    template.evaluate(writer);
    assertEquals("<li></li>", writer.toString());
  }

  @Test
  void testLeadingWhitespaceTrimWithVerbatimTag() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(true).build();

    PebbleTemplate template = pebble
        .getTemplate("<li> {%- verbatim %}{{ bar }} {%- endverbatim %}</li>");
    Writer writer = new StringWriter();

    template.evaluate(writer);
    assertEquals("<li>{{ bar }}</li>", writer.toString());
  }

  @Test
  void testTrailingWhitespaceTrimWithVerbatimTag() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(true).build();

    PebbleTemplate template = pebble
        .getTemplate("<li>{% verbatim -%} {{ bar }}{% endverbatim -%} </li>");
    Writer writer = new StringWriter();

    template.evaluate(writer);
    assertEquals("<li>{{ bar }}</li>", writer.toString());
  }

}
