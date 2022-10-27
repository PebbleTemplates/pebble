package io.pebbletemplates.pebble;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * This class tests if includes with parameters work.
 *
 * @author Thomas Hunziker
 */
class IncludeWithParameterTest {

  /**
   * Test if parameters are processed correctly.
   */
  @Test
  void testIncludeWithParameters() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(false).build();
    PebbleTemplate template = pebble.getTemplate("templates/template.includeWithParameter1.peb");
    Map<String, Object> context = new HashMap<>();

    context.put("contextVariable", "some-context-variable");
    context.put("level", 1);
    Writer writer = new StringWriter();
    template.evaluate(writer, context);

    String expectedOutput =
        "simple:simple-value" + "contextVariable:some-context-variable" + "map.position:left"
            + "map.contextVariable:some-context-variable" + "level:2" + "level-main:1";

    assertEquals(expectedOutput, writer.toString());

  }

  @Test
  void testIncludeWithParametersIsolated() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(false).build();
    PebbleTemplate template = pebble
        .getTemplate("templates/template.includeWithParameterNotIsolated1.peb");
    Map<String, Object> context = new HashMap<>();

    Writer writer = new StringWriter();
    template.evaluate(writer, context);

    String expectedOutput = "bazbar";

    assertEquals(expectedOutput, writer.toString());

  }

  @Test
  void testIncludeWithParameterObject() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(false).build();
    PebbleTemplate template = pebble
        .getTemplate("templates/template.includeWithParameterObject1.peb");
    Map<String, Object> context = new HashMap<>();
    context.put("object", new TestObject());

    Writer writer = new StringWriter();
    template.evaluate(writer, context);

    assertEquals("Hello title", writer.toString());
  }

  public static class TestObject {

    public String title = "title";
  }
}
