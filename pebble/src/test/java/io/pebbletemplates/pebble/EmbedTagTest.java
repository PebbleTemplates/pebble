package io.pebbletemplates.pebble;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.loader.ClasspathLoader;
import io.pebbletemplates.pebble.loader.DelegatingLoader;
import io.pebbletemplates.pebble.loader.StringLoader;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import io.pebbletemplates.pebble.utils.Pair;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class EmbedTagTest {
  private String input;
  private String templateDirectory;

  private PebbleEngine pebble;
  private Writer writer;
  private Map<String, Object> context;

  @ParameterizedTest
  @ValueSource(strings = {"test0"
      , "test1"
      , "test2"
      , "test3"
      , "test4"
      , "test5"
      , "test6"
      , "test7"
      , "test8"
      , "test9"
      , "test10"
      , "test11"
      , "test12"
      , "test13"
      , "test14"
      , "test15"
      , "test16"
      , "test17"
      , "test18"})
  void tests(String input) throws PebbleException, IOException {
    this.input = input;
    this.setUp();

    this.renderTemplateAndCheck();
  }

  private void setUp() {
    StringLoader stringLoader = new StringLoader();
    ClasspathLoader classpathLoader = new ClasspathLoader();
    this.templateDirectory = "templates/embed/" + this.input;
    classpathLoader.setPrefix(this.templateDirectory);

    this.pebble = new PebbleEngine.Builder()
        .loader(new DelegatingLoader(Arrays.asList(
            classpathLoader,
            stringLoader
        )))
        .strictVariables(false)
        .build();

    this.writer = new StringWriter();
    this.context = new HashMap<>();
    this.context.put("foo", "FOO");
    this.context.put("bar", "BAR");
  }

  private void renderTemplateAndCheck() throws PebbleException, IOException {
    Pair<String, Throwable> actualTemplate = this.renderTemplate();
    String expectedTemplate = this.getResource("./" + this.templateDirectory + "/template.result.txt");
    String expectedTwigTemplate = this.getResource("./" + this.templateDirectory + "/template.result.twig.txt");
    String expectedError = this.getResource("./" + this.templateDirectory + "/template.error.txt");

    // template rendered correctly
    if (actualTemplate.getLeft() != null) {
      assertNotNull(actualTemplate.getLeft());
      assertNull(actualTemplate.getRight());

      assertNotNull(expectedTemplate);
      assertNull(expectedError);

      assertEquals(expectedTemplate, actualTemplate.getLeft());

      // if Twig could render the same template (meaning it doesn't use Pebble-specific syntax), make sure it renders
      // the same thing Twig does (ignoring whitespace)
      if (expectedTwigTemplate != null) {
        assertNotNull(expectedTwigTemplate);
        assertEquals(expectedTwigTemplate.replaceAll("\\s", ""), actualTemplate.getLeft().replaceAll("\\s", ""));
      }
    }

    // template did not render correctly, check the error message
    else {
      assertNull(actualTemplate.getLeft());
      assertNotNull(actualTemplate.getRight());

      actualTemplate.getRight().printStackTrace();

      assertNull(expectedTemplate);
      assertNotNull(expectedError);

      assertEquals(expectedError, actualTemplate.getRight().getMessage());
    }
  }

  private Pair<String, Throwable> renderTemplate() {
    try {
      PebbleTemplate template = this.pebble.getTemplate("template.peb");
      template.evaluate(this.writer, this.context);
      return new Pair<>(this.writer.toString(), null);
    } catch (Throwable t) {
      return new Pair<>(null, t);
    }
  }

  private String getResource(String filename) {
    try {
      File file = new File(
          this
              .getClass()
              .getClassLoader()
              .getResource(filename)
              .getFile()
      );

      return new String(Files.readAllBytes(file.toPath()));
    } catch (Exception e) {
      return null;
    }
  }

}
