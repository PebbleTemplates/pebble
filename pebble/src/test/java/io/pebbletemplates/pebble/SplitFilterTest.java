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

class SplitFilterTest {

  @Test
  void whenSplit_givenInputNull_thenReturnNull() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{% set foo = null | split(',') %}\n"
        + "{{ foo }}");

    Map<String, Object> context = new HashMap<>();

    Writer writer = new StringWriter();
    template.evaluate(writer, context);

    assertEquals("", writer.toString());
  }

  @Test
  void whenSplit_givenNoDelimiter_thenThrowPebbleException() throws IOException {
    assertThrows(PebbleException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
          .strictVariables(false).build();

      PebbleTemplate template = pebble.getTemplate("{{ \"one,two,three\" | split }}");

      Map<String, Object> context = new HashMap<>();

      Writer writer = new StringWriter();
      template.evaluate(writer, context);
    });
  }

  @Test
  void whenSplit_givenInputWithDelimiter_thenSplit() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{% set foo = 'one|two|three' | split('\\|') %}"
        + "{% for var in foo %}"
        + "{{ var }}"
        + "{% endfor %}");

    Map<String, Object> context = new HashMap<>();

    Writer writer = new StringWriter();
    template.evaluate(writer, context);

    assertEquals("onetwothree", writer.toString());
  }

  @Test
  void whenSplit_givenInputWithDelimiterAndPositiveLimit_thenSplit() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble
        .getTemplate("{% set foo = \"one,two,three,four,five\" | split(',',3) %}"
            + "{% for var in foo %}"
            + "{{ var }}"
            + "{% endfor %}");

    Map<String, Object> context = new HashMap<>();

    Writer writer = new StringWriter();
    template.evaluate(writer, context);

    assertEquals("onetwothree,four,five", writer.toString());
  }

  @Test
  void whenSplit_givenInputWithDelimiterAndNegativeLimit_thenSplit() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble
        .getTemplate("{% set foo = \"one,two,three,four,five\" | split(',',-1) %}"
            + "{% for var in foo %}"
            + "{{ var }}"
            + "{% endfor %}");

    Map<String, Object> context = new HashMap<>();

    Writer writer = new StringWriter();
    template.evaluate(writer, context);

    assertEquals("onetwothreefourfive", writer.toString());
  }

  @Test
  void whenSplit_givenInputWithDelimiterAndZeroLimit_thenSplit() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble
        .getTemplate("{% set foo = \"one,two,three,four,five\" | split(',',0) %}"
            + "{% for var in foo %}"
            + "{{ var }}"
            + "{% endfor %}");

    Map<String, Object> context = new HashMap<>();

    Writer writer = new StringWriter();
    template.evaluate(writer, context);

    assertEquals("onetwothreefourfive", writer.toString());
  }
}
