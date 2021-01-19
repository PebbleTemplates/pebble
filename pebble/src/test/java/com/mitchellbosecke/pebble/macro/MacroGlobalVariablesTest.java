package com.mitchellbosecke.pebble.macro;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.loader.StringLoader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MacroGlobalVariablesTest {

  public static final class Extension extends AbstractExtension {
    @Override
    public Map<String, Object> getGlobalVariables() {
      HashMap<String, Object> map = new HashMap<>();
      map.put("someGlobalValue", 18181);
      return map;
    }
  }

  @Test
  void test() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(true).extension(new Extension()).build();
    StringWriter writer = new StringWriter();
    pebble.getTemplate("{% macro m() %}{{ someGlobalValue }}{% endmacro %}{{ m() }}").evaluate(writer);
    assertEquals("18181", writer.toString());
  }
}
