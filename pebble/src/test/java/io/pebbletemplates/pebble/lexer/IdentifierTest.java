/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble.lexer;

import io.pebbletemplates.pebble.error.ParserException;
import io.pebbletemplates.pebble.loader.StringLoader;
import io.pebbletemplates.pebble.PebbleEngine;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IdentifierTest {

  @Test
  void common() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();
    HashMap<String, Object> context = new HashMap<>();
    context.put("fromContext", "ing");
    StringWriter writer = new StringWriter();
    pebble.getTemplate("{% set hello1 = \"test\" %}{{ hello1 }}{{ fromContext }}").evaluate(writer, context);
    assertEquals("testing", writer.toString());
  }

  @Test
  void digitStart() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();
    StringWriter writer = new StringWriter();
    assertThrows(ParserException.class, () -> pebble.getTemplate("{{ 0digit }}").evaluate(writer));
  }

  @Test
  void extendedLatin() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();
    HashMap<String, Object> context = new HashMap<>();
    context.put("éabcêwçłë_0", "test string");
    StringWriter writer = new StringWriter();
    pebble.getTemplate("{{ éabcêwçłë_0 }}").evaluate(writer, context);
    assertEquals("test string", writer.toString());
  }

  @Test
  void arabic() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();
    HashMap<String, Object> context = new HashMap<>();
    context.put("شششش", "test string");
    StringWriter writer = new StringWriter();
    pebble.getTemplate("{{ شششش }}").evaluate(writer, context);
    assertEquals("test string", writer.toString());
  }

  @Test
  void chinese() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();
    HashMap<String, Object> context = new HashMap<>();
    context.put("水", "test string");
    StringWriter writer = new StringWriter();
    pebble.getTemplate("{{ 水 }}").evaluate(writer, context);
    assertEquals("test string", writer.toString());
  }
}
