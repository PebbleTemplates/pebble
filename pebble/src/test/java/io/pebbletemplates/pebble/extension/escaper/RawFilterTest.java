package io.pebbletemplates.pebble.extension.escaper;

import io.pebbletemplates.pebble.loader.StringLoader;
import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RawFilterTest {

  @Test
  void testRawFilter() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();
    PebbleTemplate template = pebble.getTemplate("{{ text | upper | raw }}");
    Map<String, Object> context = new HashMap<>();
    context.put("text", "<br />");
    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("<BR />", writer.toString());
  }

  @Test
  void testRawFilterNotBeingLast() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();
    PebbleTemplate template = pebble.getTemplate("{{ text | raw | upper}}");
    Map<String, Object> context = new HashMap<>();
    context.put("text", "<br />");
    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("&lt;BR /&gt;", writer.toString());
  }

  @Test
  void testRawFilterWithinAutoescapeToken() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false)
        .autoEscaping(false).build();
    PebbleTemplate template = pebble
        .getTemplate("{% autoescape 'html' %}{{ text|raw }}{% endautoescape %}");
    Map<String, Object> context = new HashMap<>();
    context.put("text", "<br />");
    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("<br />", writer.toString());
  }

  @Test
  void testRawFilterWithJsonObject() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();
    PebbleTemplate template = pebble.getTemplate("{{ text | raw }}");
    Map<String, Object> context = new HashMap<>();
    context.put("text", new JsonObject());
    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals(JsonObject.JSON_VALUE, writer.toString());
  }

  @Test
  void testRawFilterWithNullObject() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();
    PebbleTemplate template = pebble.getTemplate("{{ text | raw }}");
    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("", writer.toString());
  }

  private class JsonObject {

    public static final String JSON_VALUE = "{\"menu\": {\"id\": \"file\",\"value\": \"File\",\"popup\": {\"menuitem\": [{\"value\": \"New\", \"onclick\": \"CreateNewDoc()\"},{\"value\": \"Open\", \"onclick\": \"OpenDoc()\"},{\"value\": \"Close\", \"onclick\": \"CloseDoc()\"}]}}}";

    @Override
    public String toString() {
      return JSON_VALUE;
    }
  }
}
