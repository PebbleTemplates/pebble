package io.pebbletemplates.pebble.extension;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExtensionCustomizerTest {

  PebbleEngine pebble;

  @BeforeEach
  void setUp() {
    pebble = new PebbleEngine.Builder()
            .registerExtensionCustomizer(RemoveUpperCustomizer::new)
            .build();
  }

  @Test
  void upperFilterCannotBeUsed() throws IOException {
    Map<String, Object> obj = new HashMap<>();
    obj.put("test", "abc");
    PebbleTemplate template = pebble.getLiteralTemplate("{{ test | upper }}");

    PebbleException exception = assertThrows(PebbleException.class, () -> template.evaluate(new StringWriter(), obj));
    assertTrue(exception.getMessage().contains("upper"),
            () -> "Expect upper-Filter to not exist, actual Problem: " + exception.getMessage());
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
