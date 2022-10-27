package io.pebbletemplates.pebble;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.extension.AbstractExtension;
import io.pebbletemplates.pebble.extension.Filter;
import io.pebbletemplates.pebble.extension.Function;
import io.pebbletemplates.pebble.loader.StringLoader;
import io.pebbletemplates.pebble.node.expression.UnaryExpression;
import io.pebbletemplates.pebble.node.expression.BinaryExpression;
import io.pebbletemplates.pebble.operator.Associativity;
import io.pebbletemplates.pebble.operator.BinaryOperator;
import io.pebbletemplates.pebble.operator.BinaryOperatorImpl;
import io.pebbletemplates.pebble.operator.UnaryOperator;
import io.pebbletemplates.pebble.operator.UnaryOperatorImpl;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.EvaluationContextImpl;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import io.pebbletemplates.pebble.template.PebbleTemplateImpl;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OverrideCoreExtensionTest {

  @Test
  void testOverrideCodeExtensionFunction() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder()
        .loader(new StringLoader())
        .extension(new TestExtension())
        .build();

    PebbleTemplate template = pebble.getTemplate("{{i18n()}}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("custom i18n function", writer.toString());
  }

  @Test
  void testOverrideCodeExtensionFilter() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder()
        .loader(new StringLoader())
        .extension(new TestExtension())
        .build();

    PebbleTemplate template = pebble.getTemplate("{{ null | date }}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("custom date filter", writer.toString());
  }

  @Test
  void testOverrideCoreExtensionUnaryOperator() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder()
        .loader(new StringLoader())
        .extension(new TestExtension())
        .allowOverrideCoreOperators(true)
        .build();

    PebbleTemplate template = pebble.getTemplate("{{ not true }}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("custom unary operator", writer.toString());
  }

  @Test
  void testByDefaultPreventsOverrideCoreExtensionUnaryOperator() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder()
        .loader(new StringLoader())
        .extension(new TestExtension())
        .build();

    PebbleTemplate template = pebble.getTemplate("{{ not true }}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("false", writer.toString());
  }

  @Test
  void testOverrideCoreExtensionBinaryOperator() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder()
        .loader(new StringLoader())
        .extension(new TestExtension())
        .allowOverrideCoreOperators(true)
        .build();

    PebbleTemplate template = pebble.getTemplate("{{ 2 == 2 }}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("custom binary operator", writer.toString());
  }

  @Test
  void testByDefaultPreventsOverrideCoreExtensionBinaryOperator() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder()
        .loader(new StringLoader())
        .extension(new TestExtension())
        .build();

    PebbleTemplate template = pebble.getTemplate("{{ 2 == 2 }}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("true", writer.toString());
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

    @Override
    public List<BinaryOperator> getBinaryOperators() {
      BinaryOperatorImpl equalsOperator = new BinaryOperatorImpl(
          "==", 30, FakeEqualsExpression.class, Associativity.LEFT);

      return singletonList(equalsOperator);
    }

    @Override
    public List<UnaryOperator> getUnaryOperators() {
      UnaryOperatorImpl equalsOperator = new UnaryOperatorImpl(
          "not", 500, FakeUnaryNotExpression.class);

      return singletonList(equalsOperator);
    }
  }

  private static class CustomI18nFunction implements Function {

    @Override
    public Object execute(Map<String, Object> args, PebbleTemplate self, EvaluationContext context,
        int lineNumber) {
      return "custom i18n function";
    }

    @Override
    public List<String> getArgumentNames() {
      return null;
    }
  }

  public static class FakeEqualsExpression extends BinaryExpression<String> {

    @Override
    public String evaluate(PebbleTemplateImpl self, EvaluationContextImpl context) {
      return "custom binary operator";
    }
  }

  public static class FakeUnaryNotExpression extends UnaryExpression {

    @Override
    public String evaluate(PebbleTemplateImpl self, EvaluationContextImpl context) {
      return "custom unary operator";
    }
  }

  private static class CustomDateFilter implements Filter {

    @Override
    public Object apply(Object input, Map<String, Object> args, PebbleTemplate self,
        EvaluationContext context, int lineNumber) throws PebbleException {
      return "custom date filter";
    }

    @Override
    public List<String> getArgumentNames() {
      return null;
    }
  }
}
