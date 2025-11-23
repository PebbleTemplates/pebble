package io.pebbletemplates.pebble.extension;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.extension.core.DisallowExtensionCustomizerBuilder;
import io.pebbletemplates.pebble.loader.StringLoader;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExtensionCustomizerTest {

  @Test
  void upperFilterCannotBeUsed() {
    PebbleEngine pebble = new PebbleEngine.Builder()
            .loader(new StringLoader())
            .registerExtensionCustomizer(RemoveUpperCustomizer::new)
            .build();

    Map<String, Object> obj = new HashMap<>();
    obj.put("test", "abc");
    PebbleTemplate template = pebble.getTemplate("{{ test | upper }}");

    PebbleException exception = assertThrows(PebbleException.class, () -> template.evaluate(new StringWriter(), obj));
    assertTrue(exception.getMessage().contains("upper"),
            () -> "Expect upper-Filter to not exist, actual Problem: " + exception.getMessage());
  }

  @Test
  void setDisallowedTokenParserTags() {
    PebbleEngine pebbleEngine = new PebbleEngine.Builder()
            .loader(new StringLoader())
            .registerExtensionCustomizer(new DisallowExtensionCustomizerBuilder()
                    .disallowedTokenParserTags(Collections.singletonList("flush"))
                    .build())
            .build();

    PebbleException exception = assertThrows(PebbleException.class,
            () -> pebbleEngine.getTemplate("{{ k1 }}\n" +
                    "{% flush %}\n" +
                    "{{ k2 }}"));
    assertTrue(exception.getMessage().contains("Unexpected tag name \"flush\""));
  }

  @Test
  void setDisallowedFilters() {
    PebbleEngine pebbleEngine = new PebbleEngine.Builder()
            .loader(new StringLoader())
            .registerExtensionCustomizer(new DisallowExtensionCustomizerBuilder()
                    .disallowedFilterKeys(Collections.singletonList("upper"))
                    .build())
            .build();

    Map<String, Object> obj = new HashMap<>();
    obj.put("test", "abc");
    PebbleTemplate template = pebbleEngine.getTemplate("{{ test | upper }}");

    PebbleException exception = assertThrows(PebbleException.class, () -> template.evaluate(new StringWriter(), obj));
    assertTrue(exception.getMessage().contains("upper"),
            () -> "Expect upper-Filter to not exist, actual Problem: " + exception.getMessage());
  }

  @Test
  void setDisallowedFunctions() {
    PebbleEngine pebbleEngine = new PebbleEngine.Builder()
            .loader(new StringLoader())
            .registerExtensionCustomizer(new DisallowExtensionCustomizerBuilder()
                    .disallowedFunctionKeys(Collections.singletonList("max"))
                    .build())
            .build();

    Map<String, Object> obj = new HashMap<>();
    obj.put("age", 30);
    PebbleTemplate template = pebbleEngine.getTemplate("{{ max(age, 80) }}");

    PebbleException exception = assertThrows(PebbleException.class, () -> template.evaluate(new StringWriter(), obj));
    assertTrue(exception.getMessage().contains("Function or Macro [max] does not exist"));
  }

  @Test
  void setDisallowedBinaryOperatorSymbols() {
    PebbleEngine pebbleEngine = new PebbleEngine.Builder()
            .loader(new StringLoader())
            .registerExtensionCustomizer(new DisallowExtensionCustomizerBuilder()
                    .disallowedBinaryOperatorSymbols(Collections.singletonList(">"))
                    .build())
            .build();

    PebbleException exception = assertThrows(PebbleException.class, () -> pebbleEngine.getTemplate("{% if 10 > 9 %}\n" +
            "{{ name }}" +
            "{% endif %}"));
    assertTrue(exception.getMessage().contains("Unexpected character [>]"));
  }

  @Test
  void setDisallowedUnaryOperatorSymbols() {
    PebbleEngine pebbleEngine = new PebbleEngine.Builder()
            .loader(new StringLoader())
            .registerExtensionCustomizer(new DisallowExtensionCustomizerBuilder()
                    .disallowedUnaryOperatorSymbols(Collections.singletonList("-"))
                    .build())
            .build();

    PebbleException exception = assertThrows(PebbleException.class, () -> pebbleEngine.getTemplate("{{ -num }}"));
    assertTrue(exception.getMessage().contains("Unexpected token \"OPERATOR\" of value \"-\""));
  }

  @Test
  void setDisallowedTestKeys() {
    PebbleEngine pebbleEngine = new PebbleEngine.Builder()
            .loader(new StringLoader())
            .registerExtensionCustomizer(new DisallowExtensionCustomizerBuilder()
                    .disallowedTestKeys(Collections.singletonList("null"))
                    .build())
            .build();

    PebbleTemplate template = pebbleEngine.getTemplate("{% if 123 is null %}\n" +
            "{{ name }}" +
            "{% endif %}");

    PebbleException exception = assertThrows(PebbleException.class, () -> template.evaluate(new StringWriter()));
    assertTrue(exception.getMessage().contains("Wrong operand(s) type in conditional expression"));
  }

  private static class RemoveUpperCustomizer extends ExtensionCustomizer {

    public RemoveUpperCustomizer(Extension core) {
      super(core);
    }

    @Override
    public Map<String, Filter> getFilters() {
      Map<String, Filter> filters = Optional.ofNullable(super.getFilters()).map(HashMap::new)
              .orElseGet(HashMap::new);
      filters.remove("upper");
      return filters;
    }

  }

}
