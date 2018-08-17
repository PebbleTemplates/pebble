package com.mitchellbosecke.pebble.node;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class IfNodeTest {

  private static final String templateSource = "{% if value %}yes{% else %}no{% endif %}";

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
  public void testIfNull() throws IOException {
    assertEquals("Null should be interpreted as FALSE",
            "no", render(null));
  }

  @Test(expected=PebbleException.class)
  public void testIfNullInStrictMode() throws IOException {
    render(true, null);
  }

  @Test
  public void testIfTrue() throws IOException {
    assertEquals("Null should be interpreted as FALSE",
            "yes", render(true));
  }

  @Test
  public void testIfFalse() throws IOException {
    assertEquals("Null should be interpreted as FALSE",
            "no", render(false));
  }

  @Test
  public void testIfNotZeroInteger() throws IOException {
    assertEquals("Not zero integer should be interpreted as TRUE",
            "yes", render(1));
  }

  @Test
  public void testIfZeroInteger() throws IOException {
    assertEquals("Zero integer should be interpreted as FALSE",
            "no", render(0));
  }

  @Test
  public void testIfNotZeroFloat() throws IOException {
    assertEquals("Not zero float should be interpreted as TRUE",
            "yes", render(1.1));
  }

  @Test
  public void testIfZeroFloat() throws IOException {
    assertEquals("Zero float should be interpreted as FALSE",
            "no", render(0));
  }

  @Test
  public void testIfNotEmptyString() throws IOException {
    assertEquals("Not empty string should be interpreted as TRUE",
            "yes", render("not empty string"));
  }

  @Test
  public void testIfEmptyString() throws IOException {
    assertEquals("Empty string should be interpreted as FALSE",
            "no", render(""));
  }
}
