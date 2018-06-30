package com.mitchellbosecke.pebble;

import static java.util.Locale.CANADA;
import static org.junit.Assert.assertEquals;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class RenderWithoutEndBlockTest {

  @Test(expected = PebbleException.class)
  public void testRenderWithoutEndBlock() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "Prefix {% block block_a %}Block A{% block block_b %}Block B{% endblock %} Postfix";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer_a = new StringWriter();
    template.evaluateBlock("block_a", writer_a);
  }

  @Test
  public void testRenderWithEndBlock() throws PebbleException, IOException {
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

  @Test(expected = PebbleException.class)
  public void testRenderWithoutEndBlockWithLocale() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "Prefix {% block block_a %}Block A{% block block_b %}Block B{% endblock %} Postfix";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer_a = new StringWriter();
    template.evaluateBlock("block_a", writer_a, CANADA);
    assertEquals("Block A", writer_a.toString());

    Writer writer_b = new StringWriter();
    template.evaluateBlock("block_b", writer_b, CANADA);
    assertEquals("Block B", writer_b.toString());
  }

  @Test
  public void testRenderWithEndBlockWithLocale() throws PebbleException, IOException {
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

  @Test(expected = PebbleException.class)
  public void testRenderWithoutEndBlockWithContext() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "Prefix {% block block_a %}{{vara}}{% endblock %}{% block block_b %}{{varb}} Postfix";
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
  public void testRenderEndBlockWithContext() throws PebbleException, IOException {
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


  @Test(expected = PebbleException.class)
  public void testRenderWithoutEndBlockWithContextAndLocale() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "Prefix {% block block_a %}{{vara}}{% block block_b %}{{varb}}{% endblock %} Postfix";
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
  public void testRenderWithEndBlockWithContextAndLocale() throws PebbleException, IOException {
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
  public void testRenderWithoutEndBlockTest() throws PebbleException, IOException {
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
