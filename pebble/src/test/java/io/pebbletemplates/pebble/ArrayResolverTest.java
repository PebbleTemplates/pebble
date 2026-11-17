package io.pebbletemplates.pebble;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.loader.StringLoader;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ArrayResolverTest {

  @Test
  void resolvesPrimitiveAndObjectArrayElements() throws Exception {
    PebbleEngine engine = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(true).build();
    PebbleTemplate template =
        engine.getTemplate("{{ numbers[1] }}:{{ words[0] }}");
    StringWriter output = new StringWriter();
    Map<String, Object> context = new HashMap<>();
    context.put("numbers", new int[]{3, 7});
    context.put("words", new String[]{"first", "second"});
    template.evaluate(output, context);

    assertEquals("7:first", output.toString());
  }

  @Test
  void lenientModeReturnsNullForInvalidIndices() throws Exception {
    PebbleEngine engine = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();
    PebbleTemplate template = engine.getTemplate("{{ values[4] }}|{{ values[-1] }}|{{ values['x'] }}");
    StringWriter output = new StringWriter();
    Map<String, Object> context = new HashMap<>();
    context.put("values", new String[]{"value"});
    template.evaluate(output, context);

    assertEquals("||", output.toString());
  }

  @Test
  void strictModeRejectsOutOfRangeNegativeAndNonNumericIndices() {
    PebbleEngine engine = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(true).build();
    Map<String, Object> context = new HashMap<>();
    context.put("values", new String[]{"value"});

    for (String index : new String[]{"1", "-1", "'invalid'"}) {
      PebbleTemplate template = engine.getTemplate("{{ values[" + index + "] }}");
      assertThrows(PebbleException.class, () -> template.evaluate(new StringWriter(), context));
    }
  }
}
