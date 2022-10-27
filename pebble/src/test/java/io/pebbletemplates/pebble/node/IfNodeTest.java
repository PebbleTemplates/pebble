package io.pebbletemplates.pebble.node;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.loader.StringLoader;
import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IfNodeTest {

  private static final String templateSource = "{% if value %}yes{% else %}no{% endif %}";

  private String render(Object foobar) throws IOException {
    return this.render(false, foobar);
  }

  private String render(boolean strict, Object value) throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder()
        .loader(new StringLoader())
            .strictVariables(strict)
            .build();

    PebbleTemplate template = pebble.getTemplate(templateSource);
    Map<String, Object> context = new HashMap<>();
    context.put("value", value);

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    return writer.toString();
  }


  @Test
  void testIfNull() throws IOException {
    assertEquals("no", this.render(null), "Null should be interpreted as FALSE");
  }

  @Test
  void testIfNullInStrictMode() throws IOException {
    assertThrows(PebbleException.class, () -> this.render(true, null));
  }

  @Test
  void testIfTrue() throws IOException {
    assertEquals("yes", this.render(true), "Null should be interpreted as FALSE");
  }

  @Test
  void testIfFalse() throws IOException {
    assertEquals("no", this.render(false), "Null should be interpreted as FALSE");
  }

  @Test
  void testIfNotZeroInteger() throws IOException {
    assertEquals("yes", this.render(1), "Not zero integer should be interpreted as TRUE");
  }

  @Test
  void testIfZeroInteger() throws IOException {
    assertEquals("no", this.render(0), "Zero integer should be interpreted as FALSE");
  }

  @Test
  void testIfNotZeroFloat() throws IOException {
    assertEquals("yes", this.render(1.1), "Not zero float should be interpreted as TRUE");
  }

  @Test
  void testIfZeroFloat() throws IOException {
    assertEquals("no", this.render(0), "Zero float should be interpreted as FALSE");
  }

  @Test
  void testIfNotEmptyString() throws IOException {
    assertEquals("yes", this.render("not empty string"), "Not empty string should be interpreted as TRUE");
  }

  @Test
  void testIfEmptyString() throws IOException {
    assertEquals("no", this.render(""), "Empty string should be interpreted as FALSE");
  }
}
