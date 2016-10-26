package com.mitchellbosecke.pebble.extension;

import com.mitchellbosecke.pebble.AbstractTest;
import org.junit.Test;

import static org.junit.Assert.assertNull;

public class NoOpExtensionTest extends AbstractTest {

    @Test
    public void testTokenParsersAreNull(){
        assertNull(new NoOpExtension().getTokenParsers());
    }

    @Test
    public void testBinaryOperatorsAreNull(){
        assertNull(new NoOpExtension().getBinaryOperators());
    }

    @Test
    public void testUnaryOperatorsAreNull(){
        assertNull(new NoOpExtension().getUnaryOperators());
    }

    @Test
    public void testFiltersAreNull(){
        assertNull(new NoOpExtension().getFilters());
    }

    @Test
    public void testTestsAreNull(){
        assertNull(new NoOpExtension().getTests());
    }

    @Test
    public void testFunctionsAreNull(){
        assertNull(new NoOpExtension().getFunctions());
    }

    @Test
    public void testGlobalVariablesAreNull(){
        assertNull(new NoOpExtension().getGlobalVariables());
    }

    @Test
    public void testNodeVisiorsAreNull(){
        assertNull(new NoOpExtension().getNodeVisitors());
    }

}