package io.pebbletemplates.pebble;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.error.RootAttributeNotFoundException;
import io.pebbletemplates.pebble.loader.StringLoader;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests if strict mode works in any case.
 *
 * @author Thomas Hunziker
 */
class StrictModeTest {


  /**
   * Tests that the line number and file name is correctly passed to the exception in strict mode.
   */
  @Test()
  void testComplexVariable() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(true).build();

    PebbleTemplate template = pebble
        .getTemplate("templates/template.strictModeComplexExpression.peb");

    Map<String, Object> context = new HashMap<>();

    Writer writer = new StringWriter();

    try {
      template.evaluate(writer, context);
      fail("Exception " + RootAttributeNotFoundException.class.getCanonicalName() + " is expected.");
    } catch (RootAttributeNotFoundException e) {
      assertEquals(e.getFileName(), "templates/template.strictModeComplexExpression.peb");
      assertEquals(e.getLineNumber(), (Integer) 2);
    }
  }

  /**
   * Tests that the line number and file name is correctly passed to the exception in strict mode.
   */
  @Test()
  void testSimpleVariable() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(true).build();
    PebbleTemplate template = pebble
        .getTemplate("templates/template.strictModeSimpleExpression.peb");

    Map<String, Object> context = new HashMap<>();

    Writer writer = new StringWriter();

    try {
      template.evaluate(writer, context);
      fail(
          "Exception " + RootAttributeNotFoundException.class.getCanonicalName() + " is expected.");
    } catch (RootAttributeNotFoundException e) {
      assertEquals("templates/template.strictModeSimpleExpression.peb", e.getFileName());
      assertEquals((Integer) 2, e.getLineNumber());
    }
  }

  @Test
  void whenStrictVariableEnabledWithAndExpressionAndLeftOperandFalse_thenDontEvaluateRightExpression()
      throws PebbleException, IOException {
    PebbleEngine engine = new PebbleEngine
        .Builder()
        .loader(new StringLoader())
        .strictVariables(true)
        .autoEscaping(false)
        .build();

    PebbleTemplate template = engine.getTemplate("{%- set a = null -%}\n" +
        "{{- a is not null and a.toLowerCase() == \"abc\" -}}\n" +
        "{%- if a is not null and a.toLowerCase() == \"abc\" -%}\n" +
        "Do something" +
        "{%- endif -%}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("false", writer.toString());
  }

  @Test
  void whenStrictVariableEnabledWithOrExpressionAndLeftOperandTrue_thenDontEvaluateRightExpression()
      throws PebbleException, IOException {
    PebbleEngine engine = new PebbleEngine
        .Builder()
        .loader(new StringLoader())
        .strictVariables(true)
        .autoEscaping(false)
        .build();

    PebbleTemplate template = engine.getTemplate("{%- set a = null -%}\n" +
        "{{- a is null or a.toLowerCase() == \"abc\" -}}\n" +
        "{%- if a is null or a.toLowerCase() == \"abc\" -%}\n" +
        "Do something" +
        "{%- endif -%}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("trueDo something", writer.toString());
  }
}
