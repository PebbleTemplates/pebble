package io.pebbletemplates.pebble.extension.core;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.loader.StringLoader;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AbsFilterTest {

  private final AbsFilter filter = new AbsFilter();

  @Test
  void supportsAllSpecializedNumberTypesAndGenericNumberFallback() throws PebbleException {
    assertEquals(5, this.filter.apply(-5, null, null, null, 1));
    assertEquals(5, this.filter.apply((byte) -5, null, null, null, 1));
    assertEquals(5, this.filter.apply((short) -5, null, null, null, 1));
    assertEquals(5.5f, this.filter.apply(-5.5f, null, null, null, 1));
    assertEquals(5L, this.filter.apply(-5L, null, null, null, 1));
    assertEquals(5.5d, this.filter.apply(-5.5d, null, null, null, 1));
    assertEquals(new BigDecimal("5.50"),
        this.filter.apply(new BigDecimal("-5.50"), null, null, null, 1));
    assertEquals(BigInteger.valueOf(5),
        this.filter.apply(BigInteger.valueOf(-5), null, null, null, 1));

    Number uncommonNumber = new Number() {
      @Override
      public int intValue() {
        return -6;
      }

      @Override
      public long longValue() {
        return -6;
      }

      @Override
      public float floatValue() {
        return -6.25f;
      }

      @Override
      public double doubleValue() {
        return -6.25d;
      }
    };
    Number result = this.filter.apply(uncommonNumber, null, null, null, 1);
    assertInstanceOf(Double.class, result);
    assertEquals(6.25d, result.doubleValue());
  }

  @Test
  void rejectsNullAndNonNumericTemplateInputs() {
    PebbleTemplate template =
        new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build()
            .getTemplate("{{ value | abs }}");

    assertThrows(PebbleException.class,
        () -> template.evaluate(new StringWriter(), Collections.singletonMap("value", null)));
    assertThrows(PebbleException.class,
        () -> template.evaluate(new StringWriter(), Collections.singletonMap("value", "text")));
  }
}
