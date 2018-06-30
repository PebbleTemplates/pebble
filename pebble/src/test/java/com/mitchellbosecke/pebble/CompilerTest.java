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

public class CompilerTest {

  @Test
  public void testCompile() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("hello {{ foo }}");
    Map<String, Object> context = new HashMap<>();
    context.put("foo", "BAR");
    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("hello BAR", writer.toString());
  }

  /**
   * There was an issue where one failed template would prevent future templates from being
   * compiled.
   */
  @Test(timeout = 3000)
  public void testCompilationMutexIsAlwaysReleased() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(false).build();
    try {
      pebble.getTemplate("non-existing");
    } catch (Exception e) {

    }
    PebbleTemplate template = pebble.getTemplate("templates/template.general.peb");
    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("test", writer.toString());
  }

}
