package com.mitchellbosecke.pebble.node.expression;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class UnaryNotExpressionTest {

  private static final String templateSource = "{% if not value %}yes{% else %}no{% endif %}";

  private String render(Object foobar) throws IOException {
    return render(false, foobar);
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
  public void testIfNotNull() throws IOException {
    assertEquals("Not Null should be interpreted as TRUE",
            "yes", render(null));
  }

  @Test(expected = PebbleException.class)
  public void testIfNullInStrictMode() throws IOException {
    render(true, null);
  }

  @Test
  public void testIfNotTrue() throws IOException {
    assertEquals("Not true should be interpreted as FALSE",
            "no", render(true));
  }

  @Test
  public void testIfNotFalse() throws IOException {
    assertEquals("Not false should be interpreted as TRUE",
            "yes", render(false));
  }

  @Test
  public void testIfNotIntegerDifferentThanZero() throws IOException {
    assertEquals("Not Integer one should be interpreted as FALSE",
            "no", render(1));
  }

  @Test
  public void testIfNotIntegerZero() throws IOException {
    assertEquals("Not Integer Zero should be interpreted as TRUE",
            "yes", render(0));
  }

  @Test
  public void testIfNotFloatDifferentThanZero() throws IOException {
    assertEquals("Not float different than zero should be interpreted as FALSE",
            "no", render(1.1));
  }

  @Test
  public void testIfNotFloatZero() throws IOException {
    assertEquals("Not float zero should be interpreted as TRUE",
            "yes", render(0.0));
  }

  @Test
  public void testIfNotString() throws IOException {
    assertEquals("Not string should be interpreted as FALSE",
            "no", render("not empty string"));
  }

  @Test
  public void testIfNotEmptyString() throws IOException {
    assertEquals("Not Empty string should be interpreted as TRUE",
            "yes", render(""));
  }

}
