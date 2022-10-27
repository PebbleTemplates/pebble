/*
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
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
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NewlineTrimmingTest {

  @Test
  void testPrintDefault() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .build();

    PebbleTemplate template = pebble.getTemplate("{{param1}}\n{{param2}}");

    Writer writer = new StringWriter();

    Map<String, Object> params = new HashMap<>();
    params.put("param1", "val1");
    params.put("param2", "val2");

    template.evaluate(writer, params);

    assertEquals("val1val2", writer.toString());
  }

  @Test
  void testPrintForceToTrue() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .newLineTrimming(true)
        .build();

    PebbleTemplate template = pebble.getTemplate("{{param1}}\n{{param2}}");

    Writer writer = new StringWriter();

    Map<String, Object> params = new HashMap<>();
    params.put("param1", "val1");
    params.put("param2", "val2");

    template.evaluate(writer, params);

    assertEquals("val1val2", writer.toString());
  }

  /**
   * Given that Newline Trimming is disabled,
   * a template that contains one newline character with text on each line
   * should output one newline character.
   */
  @Test
  void testNewLineIncludedWhen_NewLineTrimmingIsFalse() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .newLineTrimming(false)
        .build();

    PebbleTemplate template = pebble.getTemplate("{{param1}}\n{{param2}}");

    Writer writer = new StringWriter();

    Map<String, Object> params = new HashMap<>();
    params.put("param1", "val1");
    params.put("param2", "val2");

    template.evaluate(writer, params);

    assertEquals("val1\nval2", writer.toString());
  }

  @Test
  void testPrintDefaultTwoNewlines() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .build();

    PebbleTemplate template = pebble.getTemplate("{{param1}}\n\n{{param2}}");

    Writer writer = new StringWriter();

    Map<String, Object> params = new HashMap<>();
    params.put("param1", "val1");
    params.put("param2", "val2");

    template.evaluate(writer, params);

    assertEquals("val1\nval2", writer.toString());
  }

  /**
   * Given that Newline Trimming is disabled,
   * a template that contains one or more consecutive newline characters
   * should output one newline character.
   */
  @Test
  void testOneNewLineWhen_NewLineTrimmingFalseAndConsecutiveNewLinesInTemplate() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .newLineTrimming(false)
        .build();

    PebbleTemplate template = pebble.getTemplate("{{param1}}\n\n{{param2}}");

    Writer writer = new StringWriter();

    Map<String, Object> params = new HashMap<>();
    params.put("param1", "val1");
    params.put("param2", "val2");

    template.evaluate(writer, params);

    assertEquals("val1\n\nval2", writer.toString());
  }

  @Test
  void testCommentDefault() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .build();

    PebbleTemplate template = pebble.getTemplate("{# comment1 #}\n{# comment2 #}");

    Writer writer = new StringWriter();

    template.evaluate(writer);

    assertEquals("", writer.toString());
  }

  @Test
  void testCommentForceToTrue() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .newLineTrimming(true)
        .build();

    PebbleTemplate template = pebble.getTemplate("{# comment1 #}\n{# comment2 #}");

    Writer writer = new StringWriter();

    template.evaluate(writer);

    assertEquals("", writer.toString());
  }

  @Test
  void testCommentSetToFalse() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .newLineTrimming(false)
        .build();

    PebbleTemplate template = pebble.getTemplate("{# comment1 #}\n{# comment2 #}");

    Writer writer = new StringWriter();

    template.evaluate(writer);

    assertEquals("\n", writer.toString());
  }

  @Test
  void testExecuteDefault() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .build();

    PebbleTemplate template = pebble.getTemplate("{% if true %}\n{% endif %}");

    Writer writer = new StringWriter();

    template.evaluate(writer);

    assertEquals("", writer.toString());
  }

  @Test
  void testExecuteForceToTrue() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .newLineTrimming(true)
        .build();

    PebbleTemplate template = pebble.getTemplate("{% if true %}\n{% endif %}");

    Writer writer = new StringWriter();

    template.evaluate(writer);

    assertEquals("", writer.toString());
  }

  @Test
  void testExecuteSetToFalse() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .newLineTrimming(false)
        .build();

    PebbleTemplate template = pebble.getTemplate("{% if true %}\n{% endif %}");

    Writer writer = new StringWriter();

    template.evaluate(writer);

    assertEquals("\n", writer.toString());
  }


}
