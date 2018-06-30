/*
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
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

public class NewlineTrimmingTest {

  @Test
  public void testPrintDefault() throws PebbleException, IOException {

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
  public void testPrintForceToTrue() throws PebbleException, IOException {

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

  @Test
  public void testPrintSetToFalse() throws PebbleException, IOException {

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
  public void testPrintDefaultTwoNewlines() throws PebbleException, IOException {

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

  @Test
  public void testPrintSetToFalseTwoNewlines() throws PebbleException, IOException {

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
  public void testCommentDefault() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .build();

    PebbleTemplate template = pebble.getTemplate("{# comment1 #}\n{# comment2 #}");

    Writer writer = new StringWriter();

    template.evaluate(writer);

    assertEquals("", writer.toString());
  }

  @Test
  public void testCommentForceToTrue() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .newLineTrimming(true)
        .build();

    PebbleTemplate template = pebble.getTemplate("{# comment1 #}\n{# comment2 #}");

    Writer writer = new StringWriter();

    template.evaluate(writer);

    assertEquals("", writer.toString());
  }

  @Test
  public void testCommentSetToFalse() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .newLineTrimming(false)
        .build();

    PebbleTemplate template = pebble.getTemplate("{# comment1 #}\n{# comment2 #}");

    Writer writer = new StringWriter();

    template.evaluate(writer);

    assertEquals("\n", writer.toString());
  }

  @Test
  public void testExecuteDefault() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .build();

    PebbleTemplate template = pebble.getTemplate("{% if true %}\n{% endif %}");

    Writer writer = new StringWriter();

    template.evaluate(writer);

    assertEquals("", writer.toString());
  }

  @Test
  public void testExecuteForceToTrue() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .newLineTrimming(true)
        .build();

    PebbleTemplate template = pebble.getTemplate("{% if true %}\n{% endif %}");

    Writer writer = new StringWriter();

    template.evaluate(writer);

    assertEquals("", writer.toString());
  }

  @Test
  public void testExecuteSetToFalse() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .newLineTrimming(false)
        .build();

    PebbleTemplate template = pebble.getTemplate("{% if true %}\n{% endif %}");

    Writer writer = new StringWriter();

    template.evaluate(writer);

    assertEquals("\n", writer.toString());
  }


}
