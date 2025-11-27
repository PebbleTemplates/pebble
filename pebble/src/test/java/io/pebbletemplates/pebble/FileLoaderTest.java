package io.pebbletemplates.pebble;

import io.pebbletemplates.pebble.error.LoaderException;
import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.loader.FileLoader;
import io.pebbletemplates.pebble.loader.Loader;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FileLoaderTest {

  @Test
  void testFileLoaderPrefixNull() {
    assertThrows(LoaderException.class, () -> new FileLoader(null));
  }

  @Test
  void testFileLoaderPrefixEmpty() {
    assertThrows(LoaderException.class, () -> new FileLoader(" "));
  }

  @Test
  void testFileLoaderPrefixRelativePath() {
    assertThrows(LoaderException.class, () -> new FileLoader(" ../bar "));
  }

  @Test
  void testFileLoader() throws PebbleException, IOException, URISyntaxException {
    String prefix = Paths.get(this.getClass().getClassLoader().getResource("templates").toURI()).toString();
    Loader<?> loader = new FileLoader(prefix);
    loader.setSuffix(".suffix");
    PebbleEngine engine = new PebbleEngine.Builder().loader(loader).strictVariables(false).build();
    PebbleTemplate template1 = engine.getTemplate("template.loaderTest.peb");
    Writer writer1 = new StringWriter();
    template1.evaluate(writer1);
    assertEquals("SUCCESS", writer1.toString());
  }

  @Test
  void testFileLoaderAbsoluteTemplateName() throws PebbleException, URISyntaxException {
    String prefix = Paths.get(this.getClass().getClassLoader().getResource("templates").toURI()).toString();
    Loader<?> loader = new FileLoader(prefix);
    loader.setSuffix(".suffix");
    PebbleEngine engine = new PebbleEngine.Builder().loader(loader).strictVariables(false).build();
    assertThrows(LoaderException.class, () -> engine.getTemplate("/template.loaderTest.peb"));
  }

  @Test
  void testFileLoaderTemplateNameIsADirectory() throws PebbleException, URISyntaxException {
    String prefix = Paths.get(this.getClass().getClassLoader().getResource("templates").toURI()).toString();
    Loader<?> loader = new FileLoader(prefix);
    PebbleEngine engine = new PebbleEngine.Builder().loader(loader).strictVariables(false).build();
    assertThrows(LoaderException.class, () -> engine.getTemplate("loader"));
  }

  @Test
  void testFileLoaderRelativeTemplateName() throws PebbleException, IOException, URISyntaxException {
    String prefix = Paths.get(this.getClass().getClassLoader().getResource("templates").toURI()).getParent().toString();
    Loader<?> loader = new FileLoader(prefix);
    loader.setSuffix(".suffix");
    PebbleEngine engine = new PebbleEngine.Builder().loader(loader).strictVariables(false).build();
    PebbleTemplate template1 = engine.getTemplate("templates/template.loaderTest.peb");
    Writer writer1 = new StringWriter();
    template1.evaluate(writer1);
    assertEquals("SUCCESS", writer1.toString());
  }

  @Test
  void testFileLoaderPathTraversal() throws PebbleException, URISyntaxException {
    String prefix = Paths.get(this.getClass().getClassLoader().getResource("templates").toURI()).toString();
    Loader<?> loader = new FileLoader(prefix);
    loader.setSuffix(".peb");
    PebbleEngine engine = new PebbleEngine.Builder().loader(loader).strictVariables(false).build();
    assertThrows(LoaderException.class, () -> engine.getTemplate("../template-tests/DoubleNestedIfStatement"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"%2e%2e%2f", "%2e%2e/", "..%2f", "%2e%2e%5c", "%2e%2e\\", "..%5c", "%252e%252e%255c", "..%255c"})
  void testFileLoaderPathTraversalEncoded(String relativePath) throws URISyntaxException {
    String prefix = Paths.get(this.getClass().getClassLoader().getResource("templates").toURI()).toString();
    Loader<?> loader = new FileLoader(prefix);
    loader.setSuffix(".peb");
    PebbleEngine engine = new PebbleEngine.Builder().loader(loader).strictVariables(false).build();
    assertThrows(LoaderException.class, () -> engine.getTemplate(relativePath + "template-tests/DoubleNestedIfStatement"));
  }

  /**
   * Tests if relative includes work. Issue #162.
   */
  @Test
  void testFileLoaderPathWithBackslash() throws IOException, URISyntaxException {
    String prefix = Paths.get(this.getClass().getClassLoader().getResource("templates").toURI()).toString();
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new FileLoader(prefix)).build();
    PebbleTemplate template = pebble.getTemplate("relativepath/subdirectory1/template.forwardslashes.peb".replace("/", "\\")); // ensure backslashes in all environments
    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("included", writer.toString());
  }

  /**
   * Issue #162.
   */
  @Test
  void testFileLoaderPathWithForwardSlash() throws IOException, URISyntaxException {
    String prefix = Paths.get(this.getClass().getClassLoader().getResource("templates").toURI()).toString();
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new FileLoader(prefix)).build();
    PebbleTemplate template = pebble.getTemplate("relativepath/subdirectory1/template.backwardslashes.peb");
    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("included", writer.toString());
  }
}
