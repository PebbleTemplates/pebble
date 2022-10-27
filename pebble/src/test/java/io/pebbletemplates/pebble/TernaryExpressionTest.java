/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble;

import io.pebbletemplates.pebble.error.ParserException;
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

class TernaryExpressionTest {

  @Test
  void testTernaryFail1() throws PebbleException {
    assertThrows(ParserException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
          .strictVariables(false).build();

      String source = "{{ 1 > 1 ? 'true' }}";

      pebble.getTemplate(source);
    });
  }

  @Test
  void testTernaryFail2() throws PebbleException {
    assertThrows(ParserException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
          .strictVariables(false).build();

      String source = "{{ 1 > 1 ? : 'true' }}";

      pebble.getTemplate(source);
    });
  }

  @Test
  void testTernaryFail3() throws PebbleException {
    assertThrows(ParserException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
          .strictVariables(false).build();

      String source = "{{ 1 > 1 ? 'true' : }}";

      pebble.getTemplate(source);
    });
  }

  @Test
  void testTernaryFail4() throws PebbleException {
    assertThrows(ParserException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
          .strictVariables(false).build();

      String source = "{{ 1 > 1 ? : }}";

      pebble.getTemplate(source);
    });
  }

  @Test
  void testTernaryFail5() throws PebbleException {
    assertThrows(ParserException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
          .strictVariables(false).build();

      String source = "{{ 1 > 1 ? : ? 'true' : 'false' }}";

      pebble.getTemplate(source);
    });
  }

  @Test
  void testTernaryFail6() throws PebbleException {
    assertThrows(ParserException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
          .strictVariables(false).build();

      String source = "{{ 1 > 1 ? true ? 'true' : 'false' }}";

      pebble.getTemplate(source);
    });
  }

  @Test
  void testTernaryFail7() throws PebbleException {
    assertThrows(ParserException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
          .strictVariables(false).build();

      String source = "{{ 1 > 1 ? : false ? 'true' : 'false' }}";

      pebble.getTemplate(source);
    });
  }

  @Test
  void testTernaryFail8() throws PebbleException {
    assertThrows(ParserException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
          .strictVariables(false).build();

      String source = "{{ 1 > 1 ? 2 > 2 ? 'true' : 'false' }}";

      pebble.getTemplate(source);
    });
  }

  @Test
  void testTernaryFail9() throws PebbleException {
    assertThrows(ParserException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
          .strictVariables(false).build();

      String source = "{{ 1 > 1 ? 2 > 2 ? : 'false' : 'false' }}";

      pebble.getTemplate(source);
    });
  }

  @Test
  void testTernaryFail10() throws PebbleException {
    assertThrows(ParserException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
          .strictVariables(false).build();

      String source = "{{ 1 > 1 ? 2 > 2 ? : : 'false' }}";

      pebble.getTemplate(source);
    });
  }

  @Test
  void testTernaryFail11() throws PebbleException {
    assertThrows(ParserException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
          .strictVariables(false).build();

      String source = "{{ 1 > 1 ? 'true' : 3 > 3 ? 'false' }}";

      pebble.getTemplate(source);
    });
  }

  @Test
  void testTernaryFail12() throws PebbleException {
    assertThrows(ParserException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
          .strictVariables(false).build();

      String source = "{{ 1 > 1 ? 'true' : 3 > 3 ? : 'false' }}";

      pebble.getTemplate(source);
    });
  }

  @Test
  void testTernaryFail13() throws PebbleException {
    assertThrows(ParserException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
          .strictVariables(false).build();

      String source = "{{ 1 > 1 ? 'true' : 3 > 3 ? : }}";

      pebble.getTemplate(source);
    });
  }

  @Test
  void testTernary1() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ 1 == 1 ? 'true' : 'false' }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("true", writer.toString());
  }

  @Test
  void testTernary2() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ 1 > 1 ? 'true' : 'false' }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("false", writer.toString());
  }

  @Test
  void testTernary3() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ 1 > 1 ? true : false ? 2 > 2 ? 'a' : 'b' : 3 == 3 ? 'c' : 'd' }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("c", writer.toString());
  }

  @Test
  void testComplexTernary1() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ ('a' == 'b' ? 2 + 2 : (val - 2 is not even ? true : false) ) ? (min(otherVal,-1) | abs <  3 / 3 - 1 ? false : ['yay!'] contains 'yay!' ) : ('?' is not empty ? ''~'?' : 0) }}";
    PebbleTemplate template = pebble.getTemplate(source);
    Map<String, Object> params = new HashMap<>();
    params.put("val", 3);
    params.put("otherVal", 100);
    Writer writer = new StringWriter();
    template.evaluate(writer, params);
    assertEquals("true", writer.toString());
  }

  @Test
  void testComplexTernary2() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ 'a' == 'b' ? 2 + 2 : val - 2 is not even ? true : false ? min(otherVal,-1) | abs <  3 / 3 - 1 ? false : ['yay!'] contains 'yay!' : '?' is not empty ? ''~'?' : 0 }}";
    PebbleTemplate template = pebble.getTemplate(source);
    Map<String, Object> params = new HashMap<>();
    params.put("val", 3);
    params.put("otherVal", 100);
    Writer writer = new StringWriter();
    template.evaluate(writer, params);
    assertEquals("true", writer.toString());
  }

  @Test
  void testTernaryIntTrue() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate(
        "{{ 1 ? 'true' : 'false' }}");
    StringWriter writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("true", writer.toString());
  }

  @Test
  void testTernaryIntFalse() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate(
        "{{ 0 ? 'true' : 'false' }}");
    StringWriter writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("false", writer.toString());
  }

  @Test
  void testTernaryStringTrue() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate(
        "{{ 'not empty' ? 'true' : 'false' }}");
    StringWriter writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("true", writer.toString());
  }

  @Test
  void testTernaryStringFalse() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate(
        "{{ '' ? 'true' : 'false' }}");
    StringWriter writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("false", writer.toString());
  }

  @Test
  void testTernaryDecimalTrue() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate(
        "{{ 0.000001 ? 'true' : 'false' }}");
    StringWriter writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("true", writer.toString());
  }

  @Test
  void testTernaryDecimalFalse() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate(
        "{{ 0.00000 ? 'true' : 'false' }}");
    StringWriter writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("false", writer.toString());
  }
}
