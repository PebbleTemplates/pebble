package com.mitchellbosecke.pebble;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class OverrideCoreExtensionTest {
  @Test
  public void testOverrideCodeExtensionFunction() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder()
            .loader(new StringLoader())
            .extension(new TestExtension())
            .strictVariables(false)
            .build();

    PebbleTemplate template = pebble.getTemplate("{{i18n()}}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("custom i18n function", writer.toString());
  }

  @Test
  public void testOverrideCodeExtensionFilter() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder()
            .loader(new StringLoader())
            .extension(new TestExtension())
            .strictVariables(false)
            .build();

    PebbleTemplate template = pebble.getTemplate("{{ null | date }}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("custom date filter", writer.toString());
  }

  private static class TestExtension extends AbstractExtension {

    @Override
    public Map<String, Function> getFunctions() {
      Map<String, Function> functions = new HashMap<>();
      functions.put("i18n", new CustomI18nFunction());
      return functions;
    }

    @Override
    public Map<String, Filter> getFilters() {
      Map<String, Filter> filters = new HashMap<>();
      filters.put("date", new CustomDateFilter());
      return filters;
    }
  }

  private static class CustomI18nFunction implements Function {
    @Override
    public Object execute(Map<String, Object> args, PebbleTemplate self, EvaluationContext context, int lineNumber) {
      return "custom i18n function";
    }

    @Override
    public List<String> getArgumentNames() {
      return null;
    }
  }

  private static class CustomDateFilter implements Filter {
    @Override
    public Object apply(Object input, Map<String, Object> args, PebbleTemplate self, EvaluationContext context, int lineNumber) throws PebbleException {
      return "custom date filter";
    }

    @Override
    public List<String> getArgumentNames() {
      return null;
    }
  }
}
