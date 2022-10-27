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

/**
 * Tests if equals is working with enums.
 */
class EnumEqualsTest {

  @Test
  void testEnumComparision() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if 'MY_CONSTANT' equals obj2 %}yes{% else %}no{% endif %}{% if obj2 equals 'MY_CONSTANT' %}yes{% else %}no{% endif %}{% if obj2 equals 'OTHER_CONSTANT' %}no{% else %}yes{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);
    Map<String, Object> context = new HashMap<>();
    context.put("obj2", TestEnum.MY_CONSTANT);

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("yesyesyes", writer.toString());

  }

  public enum TestEnum {

    MY_CONSTANT,

    OTHER_CONSTANT,

  }

}
