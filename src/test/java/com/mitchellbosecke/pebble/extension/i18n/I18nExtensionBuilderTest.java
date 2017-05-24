package com.mitchellbosecke.pebble.extension.i18n;

import com.mitchellbosecke.pebble.AbstractTest;
import com.mitchellbosecke.pebble.extension.Extension;
import com.mitchellbosecke.pebble.extension.Function;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void testBuildWithDefaultFunction() throws Exception {
        Extension extension = new I18nExtension.Builder(null)
                .build();

        assertTrue("expected i18n function to be a i18nFunction", extension.getFunctions().get("i18n") instanceof i18nFunction);
    }

    @Test
    public void testBuildWithCustomFunction() throws Exception {
        Function i18n = new TestFunction();
        Extension extension = new I18nExtension.Builder(null)
                .useFunction(i18n)
                .build();

        assertEquals("expected i18n function to be a TestFunction", i18n, extension.getFunctions().get("i18n"));
    }

    private static class TestFunction implements Function{

        @Override
        public List<String> getArgumentNames() {
            return null;
        }

        @Override
        public Object execute(Map<String, Object> args) {
            return null;
        }
    }

}