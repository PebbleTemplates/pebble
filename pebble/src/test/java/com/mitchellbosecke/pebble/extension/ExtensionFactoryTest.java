package com.mitchellbosecke.pebble.extension;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.core.CoreExtension;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExtensionFactoryTest {

  PebbleEngine pebble;

  @BeforeEach
  void setUp() {
    pebble = new PebbleEngine.Builder()
            .addExtensionCustomizer(CoreExtension.class, RemoveUpper::new)
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

  private static class RemoveUpper extends ExtensionCustomizer {

    public RemoveUpper(Extension core) {
      super(core);
    }

    @Override
    public Map<String, Filter> getFilters() {
      Map<String, Filter> filters = new HashMap<>(super.getFilters());
      filters.remove("upper");
      return filters;
    }

  }

}
