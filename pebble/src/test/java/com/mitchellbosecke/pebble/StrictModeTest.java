package com.mitchellbosecke.pebble;

import static org.junit.Assert.assertEquals;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.error.RootAttributeNotFoundException;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests if strict mode works in any case.
 *
 * @author Thomas Hunziker
 */
public class StrictModeTest {


  /**
   * Tests that the line number and file name is correctly passed to the exception in strict mode.
   */
  @Test()
  public void testComplexVariable() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(true).build();

    PebbleTemplate template = pebble
        .getTemplate("templates/template.strictModeComplexExpression.peb");

    Map<String, Object> context = new HashMap<>();

    Writer writer = new StringWriter();

    try {
      template.evaluate(writer, context);
      Assert.fail(
          "Exception " + RootAttributeNotFoundException.class.getCanonicalName() + " is expected.");
    } catch (RootAttributeNotFoundException e) {
      Assert.assertEquals(e.getFileName(), "templates/template.strictModeComplexExpression.peb");
      Assert.assertEquals(e.getLineNumber(), (Integer) 2);
    }
  }

  /**
   * Tests that the line number and file name is correctly passed to the exception in strict mode.
   */
  @Test()
  public void testSimpleVariable() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(true).build();
    PebbleTemplate template = pebble
        .getTemplate("templates/template.strictModeSimpleExpression.peb");

    Map<String, Object> context = new HashMap<>();

    Writer writer = new StringWriter();

    try {
      template.evaluate(writer, context);
      Assert.fail(
          "Exception " + RootAttributeNotFoundException.class.getCanonicalName() + " is expected.");
    } catch (RootAttributeNotFoundException e) {
      Assert.assertEquals("templates/template.strictModeSimpleExpression.peb", e.getFileName());
      Assert.assertEquals((Integer) 2, e.getLineNumber());
    }
  }

  @Test
  public void whenStrictVariableEnabledWithAndExpressionAndLeftOperandFalse_thenDontEvaluateRightExpression()
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
  public void whenStrictVariableEnabledWithOrExpressionAndLeftOperandTrue_thenDontEvaluateRightExpression()
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
