package io.pebbletemplates.pebble;

import io.pebbletemplates.pebble.loader.StringLoader;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
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
}
