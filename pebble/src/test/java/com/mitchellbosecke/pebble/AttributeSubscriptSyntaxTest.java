/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble;

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

public class AttributeSubscriptSyntaxTest {

  @SuppressWarnings("serial")
  @Test
  public void testAccessingValueWithSubscript() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ person['first-name'] }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Map<String, Object> context = new HashMap<>();
    context.put("person", new HashMap<String, Object>() {

      {
        this.put("first-name", "Bob");
      }
    });

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("Bob", writer.toString());
  }

  @SuppressWarnings("serial")
  @Test
  public void testAccessingValueWithExpressionSubscript() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source1 = "{% set var = 'apple' %}{{ colors[var] }}";
    PebbleTemplate template1 = pebble.getTemplate(source1);

    String source2 = "{% set var = 'pear' %}{{ colors[var] }}";
    PebbleTemplate template2 = pebble.getTemplate(source2);

    Map<String, Object> context = new HashMap<>();
    context.put("colors", new HashMap<String, Object>() {

      {
        this.put("apple", "red");
        this.put("pear", "green");
      }
    });

    Writer writer1 = new StringWriter();
    template1.evaluate(writer1, context);
    assertEquals("red", writer1.toString());

    Writer writer2 = new StringWriter();
    template2.evaluate(writer2, context);
    assertEquals("green", writer2.toString());
  }

  @SuppressWarnings("serial")
  @Test
  public void testAccessingValueWithIntegerExpressionSubscript()
      throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source1 = "{{ colors[one] }}";
    PebbleTemplate template1 = pebble.getTemplate(source1);

    String source2 = "{{ colors[two] }}";
    PebbleTemplate template2 = pebble.getTemplate(source2);

    Map<String, Object> context = new HashMap<>();
    context.put("colors", new HashMap<Long, Object>() {

      {
        this.put(1L, "red");
        this.put(2L, "green");
      }
    });
    context.put("one", 1L);
    context.put("two", 2L);

    Writer writer1 = new StringWriter();
    template1.evaluate(writer1, context);
    assertEquals("red", writer1.toString());

    Writer writer2 = new StringWriter();
    template2.evaluate(writer2, context);
    assertEquals("green", writer2.toString());
  }

  @SuppressWarnings("serial")
  @Test
  public void testAccessingNestedValuesWithSubscript() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ person['name']['first'] }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Map<String, Object> context = new HashMap<>();
    context.put("person", new HashMap<String, Object>() {

      {
        this.put("name", new HashMap<String, Object>() {

          {
            this.put("first", "Bob");
          }
        });
      }
    });

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("Bob", writer.toString());
  }

  @SuppressWarnings("serial")
  @Test
  public void testMixAndMatchingAttributeSyntax() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ person['name'].first }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Map<String, Object> context = new HashMap<>();
    context.put("person", new HashMap<String, Object>() {

      {
        this.put("name", new HashMap<String, Object>() {

          {
            this.put("first", "Bob");
          }
        });
      }
    });

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("Bob", writer.toString());

    source = "{{ person.name['first'] }}";
    template = pebble.getTemplate(source);

    writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("Bob", writer.toString());
  }
}
