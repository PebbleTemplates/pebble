package com.mitchellbosecke.pebble;

import static org.junit.Assert.assertEquals;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.InvocationCountingFunction;
import com.mitchellbosecke.pebble.extension.TestingExtension;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

/**
 * Created by mitch_000 on 2016-11-13.
 */
public class MacroTest {

  private static final String LINE_SEPARATOR = System.lineSeparator();

  @Test
  public void testMacro() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(false).build();
    PebbleTemplate template = pebble.getTemplate("templates/template.macro1.peb");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("	<input name=\"company\" value=\"google\" type=\"text\" />" + LINE_SEPARATOR,
        writer.toString());
  }

  /**
   * This ensures that macro inheritance works properly even if it skips a generation.
   */
  @Test
  public void skipGenerationMacro() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(false).build();
    PebbleTemplate template = pebble.getTemplate("templates/template.skipGenerationMacro1.peb");
    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("success", writer.toString());
  }

  @Test(expected = RuntimeException.class)
  public void testMacrosWithSameName() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();
    PebbleTemplate template = pebble.getTemplate(
        "{{ test() }}{% macro test(one) %}ONE{% endmacro %}{% macro test(one,two) %}TWO{% endmacro %}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("	<input name=\"company\" value=\"google\" type=\"text\" />" + LINE_SEPARATOR,
        writer.toString());
  }

  @Test
  public void testMacroWithDefaultArgument() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();
    PebbleTemplate template = pebble.getTemplate(
        "{{ input(name='country') }}{% macro input(type='text', name) %}{{ type }} {{ name }}{% endmacro %}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("text country", writer.toString());
  }

  /**
   * There was an issue where the second invokation of a macro did not have access to the original
   * arguments any more.
   */
  @Test
  public void testMacroInvokedTwice() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(false).build();
    PebbleTemplate template = pebble.getTemplate("templates/template.macroDouble.peb");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("onetwo", writer.toString());
  }

  @Test
  public void testFunctionInMacroInvokedTwice() throws PebbleException, IOException {

    TestingExtension extension = new TestingExtension();
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false)
        .extension(extension).build();

    PebbleTemplate template = pebble
        .getTemplate(
            "{{ test() }}{% macro test() %}{{ invocationCountingFunction() }}{% endmacro %}");

    Writer writer = new StringWriter();
    template.evaluate(writer);

    InvocationCountingFunction function = extension.getInvocationCountingFunction();
    assertEquals(1, function.getInvocationCount());
  }

  @Test
  public void testMacroInvocationWithoutAllArguments() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();
    PebbleTemplate template = pebble
        .getTemplate("{{ test('1') }}{% macro test(one,two) %}{{ one }}{{ two }}{% endmacro %}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("1", writer.toString());
  }

  /**
   * I was once writing macro output directly to writer which was preventing output from being
   * filtered. I have fixed this now.
   */
  @Test
  public void testMacroBeingFiltered() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(false).build();
    PebbleTemplate template = pebble.getTemplate("templates/template.macro3.peb");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("HELLO" + LINE_SEPARATOR, writer.toString());
  }

  @Test
  public void testImportFile() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(false).build();
    PebbleTemplate template = pebble.getTemplate("templates/template.macro2.peb");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("	<input name=\"company\" value=\"forcorp\" type=\"text\" />" + LINE_SEPARATOR,
        writer.toString());
  }

  @Test
  public void testImportInChildTemplateOutsideOfBlock() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(false).build();
    PebbleTemplate template = pebble.getTemplate("templates/template.macro.child.peb");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("	<input name=\"company\" value=\"forcorp\" type=\"text\" />" + LINE_SEPARATOR,
        writer.toString());
  }

  @Test
  public void testDynamicImport() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(false).build();
    PebbleTemplate template = pebble.getTemplate("templates/template.import.dynamic.peb");

    Map<String, Object> context = new HashMap<>();

    context.put("modern", false);
    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("classic macro" + LINE_SEPARATOR, writer.toString());

    context.put("modern", true);
    writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("ajax macro" + LINE_SEPARATOR, writer.toString());
  }

  @Test
  public void testSetVariableInsideMacro() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(false).build();
    PebbleTemplate template = pebble.getTemplate("templates/macros/setVariableBase.peb");

    Map<String, Object> context = new HashMap<>();
    context.put("unit", "tank");

    Writer writer = new StringWriter();
    template.evaluate(writer, context);

    assertEquals("tankinfantrytank", writer.toString());
  }

  @Test
  public void testMacroHasAccessToGlobalVariables() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();
    String templateContent = "{% set foo = 'bar' %}{{ test(_context) }}{% macro test(_context) %}{% set foo = 'foo' %}{{ _context.foo }}{{ foo }}{% endmacro %}";
    PebbleTemplate template = pebble.getTemplate(templateContent);

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("barfoo", writer.toString());
  }
}
