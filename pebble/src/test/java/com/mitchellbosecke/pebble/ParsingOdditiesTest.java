/*
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble;

import static org.junit.Assert.assertEquals;

import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ParsingOdditiesTest {

  @Rule
  public final ExpectedException thrown = ExpectedException.none();

  @Test
  public void testEscapeCharactersText() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(false).build();
    PebbleTemplate template = pebble.getTemplate("templates/template.escapeCharactersInText.peb");
    Map<String, Object> context = new HashMap<>();
    Writer writer = new StringWriter();
    template.evaluate(writer, context);
  }

  @Test
  public void testExpressionInArguments() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble
        .getTemplate("{{ input(1 + 1) }}{% macro input(value) %}{{value}}{% endmacro %}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("2", writer.toString());
  }

  @Test
  public void testPositionalAndNamedArguments()
      throws PebbleException, IOException, ParseException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false)
        .defaultLocale(Locale.ENGLISH).build();

    String source = "{{ stringDate | date('yyyy/MMMM/d', existingFormat='yyyy-MMMM-d') }}";

    PebbleTemplate template = pebble.getTemplate(source);
    Map<String, Object> context = new HashMap<>();
    DateFormat format = new SimpleDateFormat("yyyy-MMMM-d", Locale.ENGLISH);
    Date realDate = format.parse("2012-July-01");
    context.put("stringDate", format.format(realDate));

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("2012/July/1", writer.toString());
  }

  @Test
  public void testPositionalArgumentAfterNamedArguments() throws PebbleException {
    //Arrange
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false)
        .defaultLocale(Locale.ENGLISH).build();

    String source = "{{ stringDate | date(existingFormat='yyyy-MMMM-d', 'yyyy/MMMM/d') }}";

    this.thrown.expect(ParserException.class);

    //Act + Assert
    pebble.getTemplate(source);
  }

  @Test
  public void testVariableNamePrefixedWithOperatorName() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble
        .getTemplate("{{ organization }} {{ nothing }} {{ andy }} {{ equalsy }} {{ istanbul }}");
    Map<String, Object> context = new HashMap<>();
    context.put("organization", "organization");
    context.put("nothing", "nothing");
    context.put("andy", "andy");
    context.put("equalsy", "equalsy");
    context.put("istanbul", "istanbul");
    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("organization nothing andy equalsy istanbul", writer.toString());
  }

  @Test
  public void testAttributeNamePrefixedWithOperatorName() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ foo.org }}");
    Map<String, Object> context = new HashMap<>();
    context.put("foo", new Foo("success"));
    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("success", writer.toString());
  }

  public static class Foo {

    public String org;

    public Foo(String org) {
      this.org = org;
    }
  }

  @Test(expected = PebbleException.class)
  public void testIncorrectlyNamedArgument() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble
        .getTemplate("{{ 'This is a test of the abbreviate filter' | abbreviate(WRONG=16) }}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("This is a tes...", writer.toString());
  }

  @Test
  public void testStringConstantWithLinebreak() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ 'test\ntest' }}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("test\ntest", writer.toString());
  }

  @Test
  public void testStringWithDifferentQuotationMarks() throws PebbleException {
    //Arrange
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{'test\"}}";

    this.thrown.expect(ParserException.class);

    //Act + Assert
    pebble.getTemplate(source);
  }

  @Test
  public void testSingleQuoteWithinDoubleQuotes() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{\"te'st\"}}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("te'st", writer.toString());

    template = pebble.getTemplate("{{\"te\\'st\"}}");
    writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("te\\'st", writer.toString());

    template = pebble.getTemplate("{{'te\\'st'}}");
    writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("te'st", writer.toString());
  }

}
