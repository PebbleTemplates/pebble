package io.pebbletemplates.pebble;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests if relative path works as expected.
 *
 * @author Thomas Hunziker
 */
class TestRelativePath {

  /**
   * Tests if relative includes work.
   */
  @Test
  void testRelativeInclude() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(true).build();
    PebbleTemplate template = pebble
        .getTemplate("templates/relativepath/template.relativeinclude1.peb");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("included", writer.toString());
  }

  /**
   * Tests if relative extends work.
   */
  @Test
  void testRelativeExtends() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(true).build();
    PebbleTemplate template = pebble
        .getTemplate("templates/relativepath/template.relativeextends1.peb");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("<div>overridden</div>",
        writer.toString().replaceAll("\\r?\\n", "").replace("\t", "").replace(" ", ""));
  }

  /**
   * Tests if relative imports work.
   */
  @Test
  void testRelativeImports() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(true).build();
    PebbleTemplate template = pebble
        .getTemplate("templates/relativepath/template.relativeimport1.peb");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("<input name=\"company\" value=\"forcorp\" type=\"text\" />",
        writer.toString().replaceAll("\\r?\\n", "").replace("\t", ""));
  }
}
