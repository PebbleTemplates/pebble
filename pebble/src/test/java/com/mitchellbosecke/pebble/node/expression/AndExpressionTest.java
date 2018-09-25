package com.mitchellbosecke.pebble.node.expression;

import org.junit.Test;

import java.io.IOException;


public class AndExpressionTest extends ExpressionTest {

    @Test
    public void testIntExpressionPartFalse() throws IOException {
        testExpression("{{ 0 and true }}", "false");
        testExpression("{{ false or 0 }}", "false");


    }

    @Test
    public void testIntExpressionPartTrue() throws IOException {
        testExpression("{{ 1 and false }}", "false");
        testExpression("{{ true or 1 }}", "true");

    }

    @Test
    public void testStringExpressionPartFalse() throws IOException {
        testExpression("{{ '' and true }}", "false");
        testExpression("{{ true and '' }}", "false");


    }

    @Test
    public void testStringExpressionPartTrue() throws IOException {
        testExpression("{{ 'true' and false }}", "false");
        testExpression("{{ true or 'true' }}", "true");

    }
}
