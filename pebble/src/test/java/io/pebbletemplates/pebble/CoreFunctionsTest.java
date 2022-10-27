/**
 * **************************************************************************** This file is part of
 * Pebble.
 * <p/>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p/>
 * For the full copyright and license information, please view the LICENSE file that was distributed
 * with this source code. ****************************************************************************
 */
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CoreFunctionsTest {

  public static final String LINE_SEPARATOR = System.lineSeparator();

  @Test
  void testBlockFunction() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(false).build();
    PebbleTemplate template = pebble.getTemplate("templates/function/template.block.peb");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("Default Title" + LINE_SEPARATOR + "Default Title", writer.toString());
  }

  @Test
  void testParentFunction() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(false).build();
    PebbleTemplate template = pebble.getTemplate("templates/function/template.child.peb");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals(
        "parent text" + LINE_SEPARATOR + "\t\tparent head" + LINE_SEPARATOR + "\tchild head"
            + LINE_SEPARATOR, writer.toString());
  }

  /**
   * Issue occurred where parent block didn't have access to the context when invoked via the
   * parent() function.
   */
  @Test
  void testParentBlockHasAccessToContext() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(false).build();
    PebbleTemplate template = pebble
        .getTemplate("templates/function/template.childWithContext.peb");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("bar", writer.toString());
  }

  @Test
  void testParentThenMacro() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(false).build();
    PebbleTemplate template = pebble
        .getTemplate("templates/function/template.childThenParentThenMacro.peb");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("test", writer.toString());
  }

  /**
   * Two levels of parent functions would cause a stack overflow error, #61.
   */
  @Test
  void testParentFunctionWithTwoLevels() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(false).build();
    PebbleTemplate template = pebble.getTemplate("templates/function/template.subchild.peb");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals(
        "parent text" + LINE_SEPARATOR + "\t\t\tparent head" + LINE_SEPARATOR + "\tchild head"
            + LINE_SEPARATOR + "\tsub child head" + LINE_SEPARATOR, writer.toString());
  }

  @Test
  void testMinFunction() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ min(8.0, 1, 4, 5, object.large) }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Map<String, Object> context = new HashMap<>();
    context.put("object", new SimpleObject());

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("1", writer.toString());
  }

  @Test
  void testMaxFunction() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ max(8.0, 1, 4, 5, object.large) }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Map<String, Object> context = new HashMap<>();
    context.put("object", new SimpleObject());

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("20", writer.toString());
  }

  @Test
  void testRangeFunction() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% for i in range(0,5) %}{{ i }}{% endfor %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Map<String, Object> context = new HashMap<>();

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("012345", writer.toString());
  }

  @Test
  void testRangeFunctionIncrement2() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% for i in range(0,10,2) %}{{ i }}{% endfor %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Map<String, Object> context = new HashMap<>();

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("0246810", writer.toString());
  }

  @Test
  void testRangeFunctionDecrement2() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% for i in range(10,0,-2) %}{{ i }}{% endfor %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Map<String, Object> context = new HashMap<>();

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("1086420", writer.toString());
  }

  @Test
  void testRangeFunctionIncrement0() throws PebbleException, IOException {
    assertThrows(PebbleException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
          .strictVariables(false).build();

      String source = "{% for i in range(0,5,0) %}{{ i }}{% endfor %}";
      PebbleTemplate template = pebble.getTemplate(source);

      Map<String, Object> context = new HashMap<>();

      Writer writer = new StringWriter();
      template.evaluate(writer, context);
    });
  }

  @Test
  void testRangeFunctionChar() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% for i in range('a','e') %}{{ i }}{% endfor %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Map<String, Object> context = new HashMap<>();

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("abcde", writer.toString());
  }

  @Test
  void testRangeFunctionCharIncrement2() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% for i in range('a','f',2) %}{{ i }}{% endfor %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Map<String, Object> context = new HashMap<>();

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("ace", writer.toString());
  }

  @Test
  void testRangeFunctionCharDecrement2() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% for i in range('f','a',-2) %}{{ i }}{% endfor %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Map<String, Object> context = new HashMap<>();

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("fdb", writer.toString());
  }

  @Test
  void testRangeFunctionCharIncrement0() throws PebbleException, IOException {
    assertThrows(PebbleException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
          .strictVariables(false).build();

      String source = "{% for i in range('a','e',0) %}{{ i }}{% endfor %}";
      PebbleTemplate template = pebble.getTemplate(source);

      Map<String, Object> context = new HashMap<>();

      Writer writer = new StringWriter();
      template.evaluate(writer, context);
    });
  }

  @Test
  void testRangeFunctionLongVariable() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% for i in range(0,var) %}{{ i }}{% endfor %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Map<String, Object> context = new HashMap<>();
    context.put("var", 5L);

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("012345", writer.toString());
  }

  @Test
  void testRangeFunctionDoubleVariable() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% for i in range(0,var) %}{{ i }}{% endfor %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Map<String, Object> context = new HashMap<>();
    context.put("var", 5.5D);

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("012345", writer.toString());
  }

  @Test
  void testRangeFunctionIntegerVariable() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% for i in range(0,var) %}{{ i }}{% endfor %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Map<String, Object> context = new HashMap<>();
    context.put("var", 5);

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("012345", writer.toString());
  }

  @Test
  void testRangeFunctionIncrementIntegerVariable() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% for i in range(0,var,increment) %}{{ i }}{% endfor %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Map<String, Object> context = new HashMap<>();
    context.put("var", 5);
    context.put("increment", 2);

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("024", writer.toString());
  }

  @Test
  void testRangeFunctionIncrementDoubleVariable() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% for i in range(0,var,increment) %}{{ i }}{% endfor %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Map<String, Object> context = new HashMap<>();
    context.put("var", 5);
    context.put("increment", 2D);

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("024", writer.toString());
  }

  public class SimpleObject {

    public int small = 1;

    public int large = 20;
  }

}
