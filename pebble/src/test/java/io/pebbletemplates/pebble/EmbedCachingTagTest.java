package io.pebbletemplates.pebble;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.loader.ClasspathLoader;
import io.pebbletemplates.pebble.loader.DelegatingLoader;
import io.pebbletemplates.pebble.loader.StringLoader;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.lineSeparator;
import static org.junit.jupiter.api.Assertions.assertEquals;


class EmbedCachingTagTest {

  private final PebbleEngine pebble;
  private final Map<String, Object> context;

  public EmbedCachingTagTest() {
    StringLoader stringLoader = new StringLoader();
    ClasspathLoader classpathLoader = new ClasspathLoader();
    classpathLoader.setPrefix("templates/embed/cache");

    this.pebble = new PebbleEngine.Builder()
        .loader(new DelegatingLoader(Arrays.asList(
            classpathLoader,
            stringLoader
        )))
        .strictVariables(false)
        .cacheActive(true)
        .build();

    Writer writer = new StringWriter();
    this.context = new HashMap<>();
    this.context.put("foo", "FOO");
    this.context.put("bar", "BAR");
  }

  @Test
  void testEmbedNotChangingCachedTemplate() throws PebbleException, IOException {
    Writer writer1 = new StringWriter();
    PebbleTemplate template1 = this.pebble.getTemplate("template1.peb");
    template1.evaluate(writer1, this.context);
    String result1 = writer1.toString();

    Writer writer2 = new StringWriter();
    PebbleTemplate template2 = this.pebble.getTemplate("template2.peb");
    template2.evaluate(writer2, this.context);
    String result2 = writer2.toString();

    assertEquals("" +
            "BEFORE BASE" + lineSeparator() +
            "EMBED OVERRIDE" + lineSeparator() +
            "AFTER BASE",
        result1
    );
    assertEquals("" +
            "BEFORE BASE" + lineSeparator() +
            "EMBED BASE" + lineSeparator() +
                "AFTER BASE",
                result2
        );
    }

}
