package com.mitchellbosecke.pebble;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.error.RootAttributeNotFoundException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

/**
 * Tests if strict mode works in any case.
 *
 * @author Thomas Hunziker
 *
 */
public class StrictModeTest extends AbstractTest {

    @Before
    public void setup() {
        this.pebble.setStrictVariables(true);
    }

    /**
     * Tests that the line number and file name is correctly passed to the
     * exception in strict mode.
     */
    @Test()
    public void testSimpleVariable() throws PebbleException, IOException {

        PebbleTemplate template = pebble.getTemplate("template.strictModeExpression.peb");

        Map<String, Object> context = new HashMap<>();

        Writer writer = new StringWriter();

        try {
            template.evaluate(writer, context);
            Assert.fail("Exception " + RootAttributeNotFoundException.class.getCanonicalName() + " is expected.");
        } catch (RootAttributeNotFoundException e) {
            Assert.assertEquals(e.getFileName(), "template.strictModeExpression.peb");
            Assert.assertEquals(e.getLineNumber(), 2);
        }
    }

}
