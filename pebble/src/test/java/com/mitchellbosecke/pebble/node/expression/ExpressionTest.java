package com.mitchellbosecke.pebble.node.expression;

import static org.junit.Assert.assertEquals;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.io.IOException;
import java.io.StringWriter;

public abstract class ExpressionTest {

  private PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
      .strictVariables(false).build();

  protected void testExpression(String templateName, String expected) throws IOException {
    PebbleTemplate template = this.pebble.getTemplate(templateName);
    StringWriter writer = new StringWriter();
    template.evaluate(writer);
    assertEquals(expected, writer.toString());
  }
}
