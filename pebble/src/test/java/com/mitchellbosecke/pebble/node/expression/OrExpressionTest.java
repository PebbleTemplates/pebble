package com.mitchellbosecke.pebble.node.expression;

import java.io.IOException;
import org.junit.Test;

public class OrExpressionTest extends ExpressionTest {

  @Test
  public void testIntExpressionPartFalse() throws IOException {
    this.testExpression("{{ 0 or false }}", "false");
    this.testExpression("{{ true or 0 }}", "true");
  }

  @Test
  public void testIntExpressionPartTrue() throws IOException {
    this.testExpression("{{ 1 or false }}", "true");
    this.testExpression("{{ true or 1 }}", "true");
  }

  @Test
  public void testStringExpressionPartFalse() throws IOException {
    this.testExpression("{{ '' or false }}", "false");
    this.testExpression("{{ true or '' }}", "true");
  }

  @Test
  public void testStringExpressionPartTrue() throws IOException {
    this.testExpression("{{ 'true' or false }}", "true");
    this.testExpression("{{ true or 'true' }}", "true");
  }
}
