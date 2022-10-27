package io.pebbletemplates.pebble;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.loader.StringLoader;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import static java.util.Locale.CANADA;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RenderSingleBlockTest {

  @Test
  void testRenderSingleBlock() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "Prefix {% block block_a %}Block A{% endblock %}{% block block_b %}Block B{% endblock %} Postfix";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer_a = new StringWriter();
    template.evaluateBlock("block_a", writer_a);
    assertEquals("Block A", writer_a.toString());

    Writer writer_b = new StringWriter();
    template.evaluateBlock("block_b", writer_b);
    assertEquals("Block B", writer_b.toString());
  }

  @Test
  void testRenderSingleBlockWithLocale() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "Prefix {% block block_a %}Block A{% endblock %}{% block block_b %}Block B{% endblock %} Postfix";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer_a = new StringWriter();
    template.evaluateBlock("block_a", writer_a, CANADA);
    assertEquals("Block A", writer_a.toString());

    Writer writer_b = new StringWriter();
    template.evaluateBlock("block_b", writer_b, CANADA);
    assertEquals("Block B", writer_b.toString());
  }

  @Test
  void testRenderSingleBlockWithContext() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "Prefix {% block block_a %}{{vara}}{% endblock %}{% block block_b %}{{varb}}{% endblock %} Postfix";
    PebbleTemplate template = pebble.getTemplate(source);

    Map<String, Object> context = new HashMap<>();
    context.put("vara", "FOO");
    context.put("varb", "BAR");

    Writer writer_a = new StringWriter();
    template.evaluateBlock("block_a", writer_a, context, CANADA);
    assertEquals("FOO", writer_a.toString());

    Writer writer_b = new StringWriter();
    template.evaluateBlock("block_b", writer_b, context, CANADA);
    assertEquals("BAR", writer_b.toString());
  }

  @Test
  void testRenderSingleBlockWithContextAndLocale() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "Prefix {% block block_a %}{{vara}}{% endblock %}{% block block_b %}{{varb}}{% endblock %} Postfix";
    PebbleTemplate template = pebble.getTemplate(source);

    Map<String, Object> context = new HashMap<>();
    context.put("vara", "FOO");
    context.put("varb", "BAR");

    Writer writer_a = new StringWriter();
    template.evaluateBlock("block_a", writer_a, context);
    assertEquals("FOO", writer_a.toString());

    Writer writer_b = new StringWriter();
    template.evaluateBlock("block_b", writer_b, context);
    assertEquals("BAR", writer_b.toString());
  }

  @Test
  void testRenderSingleExtendedBlock() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(true).build();
    PebbleTemplate template = pebble
        .getTemplate("templates/single-block/template.renderextendedblock1.peb");

    Writer writer_a = new StringWriter();
    template.evaluateBlock("container_a", writer_a);
    assertEquals("Block A extended", writer_a.toString());

    Writer writer_b = new StringWriter();
    template.evaluateBlock("container_b", writer_b);
    assertEquals("Block B extended", writer_b.toString());
  }

}
