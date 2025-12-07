package io.pebbletemplates.pebble;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.extension.AbstractExtension;
import io.pebbletemplates.pebble.extension.Filter;
import io.pebbletemplates.pebble.extension.escaper.SafeString;
import io.pebbletemplates.pebble.loader.StringLoader;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DynamicNamedArgsTest {

  static class Operation {
    private String path;
    private List<String> queryParameters;

    Operation(String path, String... queryParameters) {
      this.path = path;
      this.queryParameters = Arrays.asList(queryParameters);
    }

    public String getPath() {
      return path;
    }

    public List<String> getQueryParameters() {
      return queryParameters;
    }
  }

  /**
   * Query parameters are dynamic.
   */
  static class QueryStringFilter implements Filter {
    @Override
    public Object apply(Object input, Map<String, Object> args, PebbleTemplate self, EvaluationContext context, int lineNumber) throws PebbleException {
      if (!(input instanceof Operation)) {
        throw new IllegalArgumentException("Expected Operation but got " + input.getClass());
      }
      Operation operation = (Operation) input;
      return new SafeString(operation.getPath() + operation.getQueryParameters().stream()
          .map(it -> it + "=" + args.getOrDefault(it, "string"))
          .collect(Collectors.joining("&", "?", "")));
    }

    @Override
    public List<String> getArgumentNames() {
      // Dynamic named arguments???
      return Collections.emptyList();
    }
  }

  static class QueryStringExtension extends AbstractExtension {
    @Override
    public Map<String, Filter> getFilters() {
      Map<String, Filter> filters = new HashMap<>();
      filters.put("queryString", new QueryStringFilter());
      return filters;
    }
  }

  @Test
  void shouldSupportDynamicNamedArguments() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .extension(new QueryStringExtension())
        .build();

    // Query parameters are dynamic
    Operation operation = new Operation("/library", "title", "isbn");

    String source = "{{operation | queryString(title=\"Dune\")}}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    Map<String, Object> context = new HashMap<>();
    context.put("operation", operation);
    template.evaluate(writer, context);
    assertEquals("/library?title=Dune&isbn=string", writer.toString());
  }

}
