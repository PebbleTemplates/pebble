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
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class I18nExtensionTest {

  @Test
  void testSimpleLookup() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).build();

    PebbleTemplate template = pebble.getTemplate("{{ i18n('testMessages','greeting') }}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("Hello", writer.toString());
  }

  @Test
  void testMessageWithNamedArguments() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).build();

    PebbleTemplate template = pebble
        .getTemplate("{{ i18n(bundle='testMessages',key='greeting') }}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("Hello", writer.toString());
  }

  @Test
  void testLookupWithLocale() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).build();

    PebbleTemplate template = pebble.getTemplate("{{ i18n('testMessages','greeting') }}");

    Writer writer = new StringWriter();
    template.evaluate(writer, new Locale("es", "US"));
    assertEquals("Hola", writer.toString());
  }

  @Test
  void testLookupSpecialChar() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).build();

    PebbleTemplate template = pebble
        .getTemplate("{{ i18n('testMessages','greeting.specialchars') }}");

    Writer writer = new StringWriter();
    template.evaluate(writer, new Locale("es", "US"));
    assertEquals("Hola español", writer.toString());
  }

  @Test
  void testMessageWithParams() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).build();

    PebbleTemplate template = pebble
        .getTemplate("{{ i18n('testMessages','greeting.someone', 'Pebble') }}");

    Writer writer = new StringWriter();
    template.evaluate(writer, new Locale("es", "US"));
    assertEquals("Hola, Pebble", writer.toString());
  }
}
