/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.loader.StringLoader;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CoreTestsTest {

  @Test
  void testEven() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if 2 is even %}yes{% else %}no{% endif %}{% if 3 is even %}no{% else %}yes{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("yesyes", writer.toString());
  }

  /**
   * Pebble parses numbers as longs so we want to make sure our numerical tests will work even if we
   * force it to take an int as an input.
   */
  @Test
  void testEvenWithInteger() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if num is even %}yes{% else %}no{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Map<String, Object> context = new HashMap<>();
    context.put("num", 2);
    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("yes", writer.toString());
  }

  @Test
  void testNullEven() throws PebbleException, IOException {
    assertThrows(PebbleException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
          .strictVariables(false).build();

      String source = "{% if null is even %}yes{% else %}no{% endif %}";
      PebbleTemplate template = pebble.getTemplate(source);

      Writer writer = new StringWriter();
      template.evaluate(writer);
    });
  }

  @Test
  void testOdd() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if 2 is odd %}no{% else %}yes{% endif %}{% if 3 is odd %}yes{% else %}no{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("yesyes", writer.toString());
  }

  /**
   * Pebble parses numbers as longs so we want to make sure our numerical tests will work even if we
   * force it to take an int as an input.
   */
  @Test
  void testOddWithInteger() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if num is odd %}yes{% else %}no{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Map<String, Object> context = new HashMap<>();
    context.put("num", 3);
    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("yes", writer.toString());
  }

  @Test
  void testNullOdd() throws PebbleException, IOException {
    assertThrows(PebbleException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
          .strictVariables(false).build();

      String source = "{% if null is odd %}yes{% else %}no{% endif %}";
      PebbleTemplate template = pebble.getTemplate(source);

      Writer writer = new StringWriter();
      template.evaluate(writer);
    });
  }

  @Test
  void testNull() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if null is null %}yes{% endif %}{% if obj is null %}yes{% endif %}{% if 2 is null %}no{% else %}yes{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);
    Map<String, Object> context = new HashMap<>();
    context.put("obj", null);

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("yesyesyes", writer.toString());
  }

  @Test
  void testEmpty() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if null is empty() %}yes{% endif %}{% if '  ' is empty() %}yes{% endif %}{% if obj is empty() %}yes{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);
    Map<String, Object> context = new HashMap<>();
    context.put("obj", new ArrayList<String>());

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("yesyesyes", writer.toString());
  }

  @Test
  void testIterables() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if null is iterable() %}no{% else %}yes{% endif %}{% if obj1 is iterable() %}yes{% else %}no{% endif %}{% if obj2 is iterable() %}no{% else %}yes{% endif %}{% if obj3 is iterable() %}yes{% else %}no{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);
    Map<String, Object> context = new HashMap<>();
    context.put("obj1", new ArrayList<String>());
    context.put("obj2", new HashMap<String, Object>());
    context.put("obj3", new String[]{});

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("yesyesyesyes", writer.toString());
  }

  @Test
  void testIsnt() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if 2 is not odd %}yes{% else %}no{% endif %}{% if null is not iterable() %}yes{% else %}no{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("yesyes", writer.toString());
  }

  /**
   * Using the unary "not" operator before a test.
   *
   * Issue #27
   */
  @Test
  void testNegativeTest() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if not (2 is odd) %}yes{% else %}no{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("yes", writer.toString());
  }

  /**
   * Similar to the testNegativeTest() except with an attribute of an object in the context.
   *
   * Issue #27
   */
  @Test
  void testNegativeTestOnAttribute() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if not (classroom.students is empty) %}yes{% else %}no{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);
    Map<String, Object> context = new HashMap<>();
    context.put("classroom", new Classroom());
    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("no", writer.toString());
  }

  @Test
  void testMapTest() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if {} is map %}true{% else %}false{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("true", writer.toString());
  }


  /**
   * Tests if the test function 'defined' is working.
   */
  @Test
  void testDefined() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if test is defined %}yes{% else %}no{% endif %}{% if test2 is defined %}no{% else %}yes{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    Map<String, Object> context = new HashMap<>();
    context.put("test", "yes");
    template.evaluate(writer, context);
    assertEquals("yesyes", writer.toString());
  }

  /**
   * Tests if the test function 'defined' is working on maps.
   */
  @Test
  void testDefinedWithMap() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(true).build();

    String source = "{% if test.test is defined %}yes{% else %}no{% endif %}{% if test.test2 is defined %}no{% else %}yes{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    Map<String, Object> context = new HashMap<>();

    Map<String, Object> map = new HashMap<>();
    map.put("test", "yes");
    context.put("test", map);
    template.evaluate(writer, context);
    assertEquals("yesyes", writer.toString());
  }


  public static class Classroom {

    public static List<Object> students = new ArrayList<>();
  }

}
