package com.mitchellbosecke.pebble.extension.i18n;

import com.mitchellbosecke.pebble.AbstractTest;
import com.mitchellbosecke.pebble.extension.Extension;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class I18nExtensionBuilderTest extends AbstractTest {

    @Test
    public void testBuildDefault() throws Exception {
        Extension extension = new I18nExtension.Builder(null)
                .build();

        assertFalse(
                "The built I18nExtension does not contain any Function",
                extension.getFunctions().isEmpty()
        );
    }

    @Test
    public void testBuildWhenEnabled() throws Exception {
        Extension extension = new I18nExtension.Builder(null)
                .enable()
                .build();

        assertFalse(
                "The built I18nExtension does not contain any Function",
                extension.getFunctions().isEmpty()
        );
    }

    @Test
    public void testBuildWhenDisabled() throws Exception {
        Extension extension = new I18nExtension.Builder(null)
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