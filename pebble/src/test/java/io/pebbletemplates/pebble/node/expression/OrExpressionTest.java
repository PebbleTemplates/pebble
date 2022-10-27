package io.pebbletemplates.pebble.node.expression;

import org.junit.jupiter.api.Test;

import java.io.IOException;

class OrExpressionTest extends ExpressionTest {

  @Test
  void testIntExpressionPartFalse() throws IOException {
    this.testExpression("{{ 0 or false }}", "false");
    this.testExpression("{{ true or 0 }}", "true");
  }

  @Test
  void testIntExpressionPartTrue() throws IOException {
    this.testExpression("{{ 1 or false }}", "true");
    this.testExpression("{{ true or 1 }}", "true");
  }

  @Test
  void testStringExpressionPartFalse() throws IOException {
    this.testExpression("{{ '' or false }}", "false");
    this.testExpression("{{ true or '' }}", "true");
  }

  @Test
  void testStringExpressionPartTrue() throws IOException {
    this.testExpression("{{ 'true' or false }}", "true");
    this.testExpression("{{ true or 'true' }}", "true");
  }
}
