package com.mitchellbosecke.pebble;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.error.RootAttributeNotFoundException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * Tests if strict mode works in any case.
 *
 * @author Thomas Hunziker
 *
 */
public class StrictModeTest extends AbstractTest {



    /**
     * Tests that the line number and file name is correctly passed to the
     * exception in strict mode.
     */
    @Test()
    public void testComplexVariable() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(true).build();

        PebbleTemplate template = pebble.getTemplate("templates/template.strictModeComplexExpression.peb");

        Map<String, Object> context = new HashMap<>();

        Writer writer = new StringWriter();

        try {
            template.evaluate(writer, context);
            Assert.fail("Exception " + RootAttributeNotFoundException.class.getCanonicalName() + " is expected.");
        } catch (RootAttributeNotFoundException e) {
            Assert.assertEquals(e.getFileName(), "templates/template.strictModeComplexExpression.peb");
            Assert.assertEquals(e.getLineNumber(), (Integer) 2);
        }
    }

    /**
     * Tests that the line number and file name is correctly passed to the
     * exception in strict mode.
     */
    @Test()
    public void testSimpleVariable() throws PebbleException, IOException {

        PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(true).build();
        PebbleTemplate template = pebble.getTemplate("templates/template.strictModeSimpleExpression.peb");

        Map<String, Object> context = new HashMap<>();

        Writer writer = new StringWriter();

        try {
            template.evaluate(writer, context);
            Assert.fail("Exception " + RootAttributeNotFoundException.class.getCanonicalName() + " is expected.");
        } catch (RootAttributeNotFoundException e) {
            Assert.assertEquals("templates/template.strictModeSimpleExpression.peb", e.getFileName());
            Assert.assertEquals((Integer) 2, e.getLineNumber());
        }
    }

}
