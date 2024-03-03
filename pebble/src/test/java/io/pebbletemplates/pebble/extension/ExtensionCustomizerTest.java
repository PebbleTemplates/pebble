package io.pebbletemplates.pebble.extension;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.extension.core.DisallowExtensionCustomizerBuilder;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExtensionCustomizerTest {

  @Test
  void upperFilterCannotBeUsed() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder()
            .registerExtensionCustomizer(RemoveUpperCustomizer::new)
            .build();

    Map<String, Object> obj = new HashMap<>();
    obj.put("test", "abc");
    PebbleTemplate template = pebble.getLiteralTemplate("{{ test | upper }}");

    PebbleException exception = assertThrows(PebbleException.class, () -> template.evaluate(new StringWriter(), obj));
    assertTrue(exception.getMessage().contains("upper"),
            () -> "Expect upper-Filter to not exist, actual Problem: " + exception.getMessage());
  }

  @Test
  void setDisallowedTokenParserTags() {
    PebbleEngine pebbleEngine = new PebbleEngine.Builder()
            .registerExtensionCustomizer(new DisallowExtensionCustomizerBuilder()
                    .disallowedTokenParserTags(Collections.singletonList("flush"))
                    .build())
            .build();

    PebbleException exception = assertThrows(PebbleException.class,
            () -> pebbleEngine.getLiteralTemplate("{{ k1 }}\n" +
                    "{% flush %}\n" +
                    "{{ k2 }}"));
    assertTrue(exception.getMessage().contains("Unexpected tag name \"flush\""));
  }

  @Test
  void setDisallowedFilters() {
    PebbleEngine pebbleEngine = new PebbleEngine.Builder()
            .registerExtensionCustomizer(new DisallowExtensionCustomizerBuilder()
                    .disallowedFilterKeys(Collections.singletonList("upper"))
                    .build())
            .build();

    Map<String, Object> obj = new HashMap<>();
    obj.put("test", "abc");
    PebbleTemplate template = pebbleEngine.getLiteralTemplate("{{ test | upper }}");

    PebbleException exception = assertThrows(PebbleException.class, () -> template.evaluate(new StringWriter(), obj));
    assertTrue(exception.getMessage().contains("upper"),
            () -> "Expect upper-Filter to not exist, actual Problem: " + exception.getMessage());
  }

  @Test
  void setDisallowedFunctions() {
    PebbleEngine pebbleEngine = new PebbleEngine.Builder()
            .registerExtensionCustomizer(new DisallowExtensionCustomizerBuilder()
                    .disallowedFunctionKeys(Collections.singletonList("max"))
                    .build())
            .build();

    Map<String, Object> obj = new HashMap<>();
    obj.put("age", 30);
    PebbleTemplate template = pebbleEngine.getLiteralTemplate("{{ max(age, 80) }}");

    PebbleException exception = assertThrows(PebbleException.class, () -> template.evaluate(new StringWriter(), obj));
    assertTrue(exception.getMessage().contains("Function or Macro [max] does not exist"));
  }

  @Test
  void setDisallowedBinaryOperatorSymbols() {
    PebbleEngine pebbleEngine = new PebbleEngine.Builder()
            .registerExtensionCustomizer(new DisallowExtensionCustomizerBuilder()
                    .disallowedBinaryOperatorSymbols(Collections.singletonList(">"))
                    .build())
            .build();

    PebbleException exception = assertThrows(PebbleException.class, () -> pebbleEngine.getLiteralTemplate("{% if 10 > 9 %}\n" +
            "{{ name }}" +
            "{% endif %}"));
    assertTrue(exception.getMessage().contains("Unexpected character [>]"));
  }

  @Test
  void setDisallowedUnaryOperatorSymbols() throws IOException {
    PebbleEngine pebbleEngine = new PebbleEngine.Builder()
            .registerExtensionCustomizer(new DisallowExtensionCustomizerBuilder()
                    .disallowedUnaryOperatorSymbols(Collections.singletonList("-"))
                    .build())
            .build();

    PebbleException exception = assertThrows(PebbleException.class, () -> pebbleEngine.getLiteralTemplate("{{ -num }}"));
    assertTrue(exception.getMessage().contains("Unexpected token \"OPERATOR\" of value \"-\""));
  }

  @Test
  void setDisallowedTestKeys() throws IOException {
    PebbleEngine pebbleEngine = new PebbleEngine.Builder()
            .registerExtensionCustomizer(new DisallowExtensionCustomizerBuilder()
                    .disallowedTestKeys(Collections.singletonList("null"))
                    .build())
            .build();

    PebbleTemplate template = pebbleEngine.getLiteralTemplate("{% if 123 is null %}\n" +
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
