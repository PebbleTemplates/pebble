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
import java.util.Locale;
import org.junit.Test;

public class I18nExtensionTest {

  @Test
  public void testSimpleLookup() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).build();

    PebbleTemplate template = pebble.getTemplate("{{ i18n('testMessages','greeting') }}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("Hello", writer.toString());
  }

  @Test
  public void testMessageWithNamedArguments() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).build();

    PebbleTemplate template = pebble
        .getTemplate("{{ i18n(bundle='testMessages',key='greeting') }}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("Hello", writer.toString());
  }

  @Test
  public void testLookupWithLocale() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).build();

    PebbleTemplate template = pebble.getTemplate("{{ i18n('testMessages','greeting') }}");

    Writer writer = new StringWriter();
    template.evaluate(writer, new Locale("es", "US"));
    assertEquals("Hola", writer.toString());
  }

  @Test
  public void testLookupSpecialChar() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).build();

    PebbleTemplate template = pebble
        .getTemplate("{{ i18n('testMessages','greeting.specialchars') }}");

    Writer writer = new StringWriter();
    template.evaluate(writer, new Locale("es", "US"));
    assertEquals("Hola español", writer.toString());
  }

  @Test
  public void testMessageWithParams() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).build();

    PebbleTemplate template = pebble
        .getTemplate("{{ i18n('testMessages','greeting.someone', 'Pebble') }}");

    Writer writer = new StringWriter();
    template.evaluate(writer, new Locale("es", "US"));
    assertEquals("Hola, Pebble", writer.toString());
  }
}
