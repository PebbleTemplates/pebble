package com.mitchellbosecke.pebble;

import static org.junit.Assert.assertEquals;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.FileLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import org.junit.Test;

/**
 * Tests if relative path works as expected.
 *
 * @author Thomas Hunziker
 */
public class TestRelativePath {

  /**
   * Tests if relative includes work.
   */
  @Test
  public void testRelativeInclude() throws PebbleException, IOException {
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
  public void testRelativeExtends() throws PebbleException, IOException {
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
  public void testRelativeImports() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(true).build();
    PebbleTemplate template = pebble
        .getTemplate("templates/relativepath/template.relativeimport1.peb");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("<input name=\"company\" value=\"forcorp\" type=\"text\" />",
        writer.toString().replaceAll("\\r?\\n", "").replace("\t", ""));
  }

  /**
   * Tests if relative includes work. Issue #162.
   */
  @Test
  public void testPathWithBackslashesWithRelativePathWithForwardSlashes()
      throws PebbleException, IOException, URISyntaxException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new FileLoader()).build();
    URL url = this.getClass()
        .getResource("/templates/relativepath/subdirectory1/template.forwardslashes.peb");
    PebbleTemplate template = pebble
        .getTemplate(new File(url.toURI()).getPath()
            .replace("/", "\\")); // ensure backslashes in all environments
    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("included", writer.toString());
  }

  /**
   * Issue #162.
   */
  @Test
  public void testPathWithForwardSlashesWithRelativePathWithBackwardSlashes()
      throws PebbleException, IOException, URISyntaxException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new FileLoader()).build();
    URL url = this.getClass()
        .getResource("/templates/relativepath/subdirectory1/template.backwardslashes.peb");
    PebbleTemplate template = pebble
        .getTemplate(new File(url.toURI()).getPath()
            .replace("\\", "/")); // ensure forward slashes in all environments
    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("included", writer.toString());
  }
}
