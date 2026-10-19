package io.pebbletemplates.pebble;

import io.pebbletemplates.pebble.loader.StringLoader;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OperatorUtilsTest {
  @Test
  void whenAddToList_thenShouldNotModifyLeftOperand() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder()
            .loader(new StringLoader())
            .strictVariables(false)
            .build();

    String input = "{{ a + b }} | {{ a }}";
    String expected = "[x, y, z] | [x]";

    Map<String, Object> context = new HashMap<>();
    context.put("a", new ArrayList<>(Collections.singletonList("x")));
    context.put("b", Arrays.asList("y", "z"));

    PebbleTemplate template = pebble.getTemplate(input);
    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals(expected, writer.toString());
  }

  @Test
  void whenSubstractToList_thenShouldNotModifyLeftOperand() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder()
            .loader(new StringLoader())
            .strictVariables(false)
            .build();

    String input = "{{ a - b }} | {{ a }}";
    String expected = "[x, y] | [x, y, z]";

    Map<String, Object> context = new HashMap<>();
    context.put("a", Arrays.asList("x", "y", "z"));
    context.put("b", Collections.singletonList("z"));

    PebbleTemplate template = pebble.getTemplate(input);
    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals(expected, writer.toString());
  }

  @Test
  void whenComparingLargeLongs_thenShouldDistinguishThem() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder()
            .loader(new StringLoader())
            .strictVariables(false)
            .build();

    // distinct longs that collapse to the same double
    Map<String, Object> context = new HashMap<>();
    context.put("a", 325055142682428416L);
    context.put("b", 325055142682428417L);

    String input = "{% if a == b %}eq{% else %}ne{% endif %}"
            + "|{% if a != b %}ne{% else %}eq{% endif %}"
            + "|{% if a < b %}lt{% endif %}"
            + "|{% if b > a %}gt{% endif %}";
    String expected = "ne|ne|lt|gt";

    PebbleTemplate template = pebble.getTemplate(input);
    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals(expected, writer.toString());
  }

  @Test
  void whenComparingBigNumbers_thenShouldBeExact() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder()
            .loader(new StringLoader())
            .strictVariables(false)
            .build();

    Map<String, Object> context = new HashMap<>();
    context.put("a", new BigInteger("9223372036854775808"));
    context.put("b", new BigInteger("9223372036854775809"));
    context.put("c", new BigDecimal("9223372036854775808.0"));

    String input = "{% if a == b %}eq{% else %}ne{% endif %}"
            + "|{% if a == c %}eq{% else %}ne{% endif %}"
            + "|{% if b > a %}gt{% endif %}";
    String expected = "ne|eq|gt";

    PebbleTemplate template = pebble.getTemplate(input);
    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals(expected, writer.toString());
  }

  @Test
  void whenAddingToBigInteger_thenShouldNotTruncateToInt() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder()
            .loader(new StringLoader())
            .strictVariables(false)
            .build();

    Map<String, Object> context = new HashMap<>();
    context.put("a", new BigInteger("9223372036854775807"));

    String input = "{{ a + 1 }}";
    String expected = "9223372036854775808";

    PebbleTemplate template = pebble.getTemplate(input);
    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals(expected, writer.toString());
  }
}
