package io.pebbletemplates.pebble.node.expression;

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

class UnaryNotExpressionTest {

  private static final String templateSource = "{% if not value %}yes{% else %}no{% endif %}";

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
  void testIfNotNull() throws IOException {
    assertEquals("yes", this.render(null), "Not Null should be interpreted as TRUE");
  }

  @Test
  void testIfNullInStrictMode() throws IOException {
    assertThrows(PebbleException.class, () -> this.render(true, null));
  }

  @Test
  void testIfNotTrue() throws IOException {
    assertEquals("no", this.render(true), "Not true should be interpreted as FALSE");
  }

  @Test
  void testIfNotFalse() throws IOException {
    assertEquals("yes", this.render(false), "Not false should be interpreted as TRUE");
  }

  @Test
  void testIfNotIntegerDifferentThanZero() throws IOException {
    assertEquals("no", this.render(1), "Not Integer one should be interpreted as FALSE");
  }

  @Test
  void testIfNotIntegerZero() throws IOException {
    assertEquals("yes", this.render(0), "Not Integer Zero should be interpreted as TRUE");
  }

  @Test
  void testIfNotFloatDifferentThanZero() throws IOException {
    assertEquals("no", this.render(1.1), "Not float different than zero should be interpreted as FALSE");
  }

  @Test
  void testIfNotFloatZero() throws IOException {
    assertEquals("yes", this.render(0.0), "Not float zero should be interpreted as TRUE");
  }

  @Test
  void testIfNotString() throws IOException {
    assertEquals("no", this.render("not empty string"), "Not string should be interpreted as FALSE");
  }

  @Test
  void testIfNotEmptyString() throws IOException {
    assertEquals("yes", this.render(""), "Not Empty string should be interpreted as TRUE");
  }

}
