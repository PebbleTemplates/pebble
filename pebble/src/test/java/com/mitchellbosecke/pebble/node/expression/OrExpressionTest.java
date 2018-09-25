package com.mitchellbosecke.pebble.node.expression;

import org.junit.Test;

import java.io.IOException;

public class OrExpressionTest extends ExpressionTest {

    @Test
    public void testIntExpressionPartFalse() throws IOException {
        testExpression("{{ 0 or false }}", "false");
        testExpression("{{ true or 0 }}", "true");
    }

    @Test
    public void testIntExpressionPartTrue() throws IOException {
        testExpression("{{ 1 or false }}", "true");
        testExpression("{{ true or 1 }}", "true");
    }

    @Test
    public void testStringExpressionPartFalse() throws IOException {
        testExpression("{{ '' or false }}", "false");
        testExpression("{{ true or '' }}", "true");
    }

    @Test
    public void testStringExpressionPartTrue() throws IOException {
        testExpression("{{ 'true' or false }}", "true");
        testExpression("{{ true or 'true' }}", "true");
    }
}
