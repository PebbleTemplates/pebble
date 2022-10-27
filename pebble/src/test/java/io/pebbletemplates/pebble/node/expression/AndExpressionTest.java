package io.pebbletemplates.pebble.node.expression;

import org.junit.jupiter.api.Test;

import java.io.IOException;


class AndExpressionTest extends ExpressionTest {

  @Test
  void testIntExpressionPartFalse() throws IOException {
    this.testExpression("{{ 0 and true }}", "false");
    this.testExpression("{{ false or 0 }}", "false");
  }

  @Test
  void testIntExpressionPartTrue() throws IOException {
    this.testExpression("{{ 1 and false }}", "false");
    this.testExpression("{{ true or 1 }}", "true");
  }

  @Test
  void testStringExpressionPartFalse() throws IOException {
    this.testExpression("{{ '' and true }}", "false");
    this.testExpression("{{ true and '' }}", "false");
  }

  @Test
  void testStringExpressionPartTrue() throws IOException {
    this.testExpression("{{ 'true' and false }}", "false");
    this.testExpression("{{ true or 'true' }}", "true");
  }
}
