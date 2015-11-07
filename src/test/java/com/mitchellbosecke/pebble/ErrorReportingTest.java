/*******************************************************************************
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.Test;

import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.error.RootAttributeNotFoundException;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class ErrorReportingTest extends AbstractTest {

    @Test(expected = ParserException.class)
    public void testLineNumberErrorReportingWithUnixNewlines() throws PebbleException, IOException {
        Loader<?> loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);
        try {
            @SuppressWarnings("unused")
            PebbleTemplate template = pebble.getTemplate("test\n\n\ntest\ntest\ntest\n{% error %}\ntest");
        } catch (ParserException ex) {
            String message = ex.getMessage();
            System.out.println(message.substring(message.length() - 3, message.length()));
            assertEquals(":7)", message.substring(message.length() - 3, message.length()));
            throw ex;
        }
    }

    @Test(expected = ParserException.class)
    public void testLineNumberErrorReportingWithWindowsNewlines() throws PebbleException, IOException {
        Loader<?> loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);
        try {
            @SuppressWarnings("unused")
            PebbleTemplate template = pebble.getTemplate("test\r\n\r\ntest\r\ntest\r\ntest\r\n{% error %}\r\ntest");
        } catch (ParserException ex) {
            String message = ex.getMessage();
            assertEquals(":6)", message.substring(message.length() - 3, message.length()));
            throw ex;
        }
    }

    @Test(expected = PebbleException.class)
    public void testLineNumberErrorReportingDuringEvaluation() throws PebbleException, IOException {
        try {
            PebbleTemplate template = pebble.getTemplate("template.errorReporting.peb");
            template.evaluate(new StringWriter());
        } catch (PebbleException ex) {
            String message = ex.getMessage();
            assertEquals(":8)", message.substring(message.length() - 3, message.length()));
            throw ex;
        }
    }

    @Test(expected = RootAttributeNotFoundException.class)
    public void testMissingRootPropertyInStrictMode() throws PebbleException, IOException {
        try {
            pebble.setStrictVariables(true);
            PebbleTemplate template = pebble.getTemplate("template.errorReportingMissingRootProperty.peb");
            template.evaluate(new StringWriter());
        } catch (PebbleException ex) {
            String message = ex.getMessage();
            assertEquals("root", ((RootAttributeNotFoundException) ex).getAttributeName());
            assertEquals(message,
                    "Root attribute [root] does not exist or can not be accessed and strict variables is set to true. (?:?)");
            throw ex;
        } finally {
            pebble.setStrictVariables(false);
        }
    }

}
