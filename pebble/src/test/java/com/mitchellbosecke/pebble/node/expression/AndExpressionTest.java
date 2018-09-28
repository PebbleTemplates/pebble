package com.mitchellbosecke.pebble.node.expression;

import java.io.IOException;
import org.junit.Test;


public class AndExpressionTest extends ExpressionTest {

  @Test
  public void testIntExpressionPartFalse() throws IOException {
    this.testExpression("{{ 0 and true }}", "false");
    this.testExpression("{{ false or 0 }}", "false");
  }

  @Test
  public void testIntExpressionPartTrue() throws IOException {
    this.testExpression("{{ 1 and false }}", "false");
    this.testExpression("{{ true or 1 }}", "true");
  }

  @Test
  public void testStringExpressionPartFalse() throws IOException {
    this.testExpression("{{ '' and true }}", "false");
    this.testExpression("{{ true and '' }}", "false");
  }

  @Test
  public void testStringExpressionPartTrue() throws IOException {
    this.testExpression("{{ 'true' and false }}", "false");
    this.testExpression("{{ true or 'true' }}", "true");
  }
}
