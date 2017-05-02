package com.mitchellbosecke.pebble.macro;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.junit.Test;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

/**
 * Test calls of macros
 *
 */
public class TestMacroCalls {

	
	/**
	 * Checks, if macros are called to often
	 * @throws PebbleException
	 * @throws IOException
	 */
    @Test
    public void testMultipleMacroCalls() throws PebbleException, IOException {
    	// Build a pebble engine with one configured filter ("testfilter" - TestFilter.java)
        PebbleEngine pebble = new PebbleEngine.Builder()
        		.extension(new PebbleExtension())
        		.strictVariables(false).build();
        // Resets the counter of the filter
        TestFilter.counter = 0;
        /*
         * Runs the test scenario:
         * 	index-template with an import of the macro and a call to this macro (Call #1)
         * 	index-templates includes "include.peb"
         *  
         *  include.peb with an import of the macro and a call to this macro (Call #2)
         *  
         *  We track the number of macro-calls by using a small "Filter" (TestFilter) that just
         *  counts, how often it is called.
         */
        PebbleTemplate template = pebble.getTemplate("templates/macros/index.peb");
        Writer writer = new StringWriter();
        template.evaluate(writer);
        
        
        // We expect, that the TestFilter was called 2x
        assertEquals(2, TestFilter.getCounter());
    }

    @Test
    public void testInvalidMacro() throws IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().build();

        try {
            PebbleTemplate template = pebble.getTemplate("templates/macros/invalid.macro.peb");
            Writer writer = new StringWriter();
            template.evaluate(writer);
        } catch (PebbleException e) {
            assertNotNull(e.getLineNumber());
            assertEquals(e.getLineNumber().intValue(), 2);
        }
    }
}
