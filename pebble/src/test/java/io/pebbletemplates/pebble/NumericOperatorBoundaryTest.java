package io.pebbletemplates.pebble;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.loader.StringLoader;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NumericOperatorBoundaryTest {

  @Test
  void integerDivisionAndModuloByZeroReportPebbleErrors() {
    PebbleEngine engine = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    assertThrows(PebbleException.class, () -> render(engine.getTemplate("{{ 1 / 0 }}")));
    assertThrows(PebbleException.class, () -> render(engine.getTemplate("{{ 1 % 0 }}")));
  }

  @Test
  void floatingPointDivisionAndModuloByZeroFollowIeee754() throws Exception {
    PebbleEngine engine = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    assertEquals("Infinity|NaN",
        render(engine.getTemplate("{{ 1.0 / 0.0 }}|{{ 1.0 % 0.0 }}")));
  }

  @Test
  void negativeRemaindersKeepTheDividendSign() throws Exception {
    PebbleEngine engine = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    assertEquals("-1|1", render(engine.getTemplate("{{ -7 % 3 }}|{{ 7 % -3 }}")));
  }

  @Test
  void largeAndNonTerminatingBigDecimalOperationsRemainPrecise() throws Exception {
    PebbleEngine engine = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();
    PebbleTemplate template =
        engine.getTemplate("{{ large + 1 }}|{{ numerator / denominator }}");
    Map<String, Object> context = new HashMap<>();
    context.put("large", new BigInteger("92233720368547758081234567890"));
    context.put("numerator", BigDecimal.ONE);
    context.put("denominator", BigDecimal.valueOf(3));

    assertEquals("92233720368547758081234567891|"
        + "0.3333333333333333333333333333333333", render(template, context));
  }

  private static String render(PebbleTemplate template) throws Exception {
    return render(template, new HashMap<>());
  }

  private static String render(PebbleTemplate template, Map<String, Object> context)
      throws Exception {
    StringWriter output = new StringWriter();
    template.evaluate(output, context);
    return output.toString();
  }
}
