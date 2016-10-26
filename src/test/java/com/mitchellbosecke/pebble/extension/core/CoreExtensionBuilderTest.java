package com.mitchellbosecke.pebble.extension.core;

import com.mitchellbosecke.pebble.AbstractTest;
import com.mitchellbosecke.pebble.extension.Extension;
import org.junit.Test;

import static org.junit.Assert.*;

public class CoreExtensionBuilderTest extends AbstractTest {

    @Test
    public void testBuildDefault() throws Exception {
        Extension extension = new CoreExtension.Builder(null)
                .build();

        assertFalse(
                "The built CoreExtension does not contain any TokenParser",
                extension.getTokenParsers().isEmpty()
        );
        assertFalse(
                "The built CoreExtension does not contain any UnaryOperator",
                extension.getUnaryOperators().isEmpty()
        );
        assertFalse(
                "The built CoreExtension does not contain any BinaryOperator",
                extension.getBinaryOperators().isEmpty()
        );
        assertFalse(
                "The built CoreExtension does not contain any Filter",
                extension.getFilters().isEmpty()
        );
        assertFalse(
                "The built CoreExtension does not contain any Test",
                extension.getTests().isEmpty()
        );
        assertFalse(
                "The built CoreExtension does not contain any NodeVisitorFactory",
                extension.getNodeVisitors().isEmpty()
        );
    }

    @Test
    public void testBuildWhenEnabled() throws Exception {
        Extension extension = new CoreExtension.Builder(null)
                .enable()
                .build();

        assertFalse(
                "The built CoreExtension does not contain any TokenParser",
                extension.getTokenParsers().isEmpty()
        );
        assertFalse(
                "The built CoreExtension does not contain any UnaryOperator",
                extension.getUnaryOperators().isEmpty()
        );
        assertFalse(
                "The built CoreExtension does not contain any BinaryOperator",
                extension.getBinaryOperators().isEmpty()
        );
        assertFalse(
                "The built CoreExtension does not contain any Filter",
                extension.getFilters().isEmpty()
        );
        assertFalse(
                "The built CoreExtension does not contain any Test",
                extension.getTests().isEmpty()
        );
        assertFalse(
                "The built CoreExtension does not contain any NodeVisitorFactory",
                extension.getNodeVisitors().isEmpty()
        );
    }

    @Test
    public void testBuildWhenDisabled() throws Exception {
        Extension extension = new CoreExtension.Builder(null)
                .disable()
                .build();

        assertNull(extension.getTokenParsers());
        assertNull(extension.getUnaryOperators());
        assertNull(extension.getBinaryOperators());
        assertNull(extension.getFilters());
        assertNull(extension.getTests());
        assertNull(extension.getNodeVisitors());
    }

}