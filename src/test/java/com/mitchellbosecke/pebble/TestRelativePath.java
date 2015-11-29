package com.mitchellbosecke.pebble;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import static org.junit.Assert.assertEquals;

/**
 * Tests if relative path works as expected.
 *
 * @author Thomas Hunziker
 *
 */
public class TestRelativePath extends AbstractTest {

    /**
     * Tests if relative includes work.
     */
    @Test
    public void testRelativeInclude() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(true).build();
        PebbleTemplate template = pebble.getTemplate("templates/relativepath/template.relativeinclude1.peb");

        Writer writer = new StringWriter();
        template.evaluate(writer);
        assertEquals("included", writer.toString());
    }

    /**
     * Tests if relative extends work.
     */
    @Test
    public void testRelativeExtends() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(true).build();
        PebbleTemplate template = pebble.getTemplate("templates/relativepath/template.relativeextends1.peb");

        Writer writer = new StringWriter();
        template.evaluate(writer);
        assertEquals("<div>overridden</div>", writer.toString().replaceAll("\\r?\\n", "").replace("\t", "").replace(" ", ""));
    }

    /**
     * Tests if relative imports work.
     */
    @Test
    public void testRelativeImports() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(true).build();
        PebbleTemplate template = pebble.getTemplate("templates/relativepath/template.relativeimport1.peb");

        Writer writer = new StringWriter();
        template.evaluate(writer);
        assertEquals("<input name=\"company\" value=\"forcorp\" type=\"text\" />", writer.toString().replaceAll("\\r?\\n", "").replace("\t", ""));
    }

}
