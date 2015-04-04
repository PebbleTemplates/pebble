/**
 * ****************************************************************************
 * This file is part of Pebble.
 * <p/>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p/>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 * ****************************************************************************
 */
package com.mitchellbosecke.pebble;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class CoreFunctionsTest extends AbstractTest {

    public static final String LINE_SEPARATOR = System.lineSeparator();

    @Test
    public void testBlockFunction() throws PebbleException, IOException {
        PebbleTemplate template = pebble.getTemplate("function/template.block.peb");

        Writer writer = new StringWriter();
        template.evaluate(writer);
        assertEquals("Default Title" + LINE_SEPARATOR + "Default Title", writer.toString());
    }

    @Test
    public void testParentFunction() throws PebbleException, IOException {
        PebbleTemplate template = pebble.getTemplate("function/template.child.peb");

        Writer writer = new StringWriter();
        template.evaluate(writer);
        assertEquals("parent text" + LINE_SEPARATOR + "\t\tparent head" + LINE_SEPARATOR + "\tchild head" + LINE_SEPARATOR, writer.toString());
    }

    /**
     * Issue occurred where parent block didn't have access to the context when
     * invoked via the parent() function.
     *
     * @throws PebbleException
     */
    @Test
    public void testParentBlockHasAccessToContext() throws PebbleException, IOException {
        PebbleTemplate template = pebble.getTemplate("function/template.childWithContext.peb");

        Writer writer = new StringWriter();
        template.evaluate(writer);
        assertEquals("bar", writer.toString());
    }

    @Test
    public void testParentThenMacro() throws PebbleException, IOException {
        PebbleTemplate template = pebble.getTemplate("function/template.childThenParentThenMacro.peb");

        Writer writer = new StringWriter();
        template.evaluate(writer);
        assertEquals("test", writer.toString());
    }

    @Test
    public void testMinFunction() throws PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);

        String source = "{{ min(8.0, 1, 4, 5, object.large) }}";
        PebbleTemplate template = pebble.getTemplate(source);

        Map<String, Object> context = new HashMap<>();
        context.put("object", new SimpleObject());

        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("1", writer.toString());
    }

    @Test
    public void testMaxFunction() throws PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);

        String source = "{{ max(8.0, 1, 4, 5, object.large) }}";
        PebbleTemplate template = pebble.getTemplate(source);

        Map<String, Object> context = new HashMap<>();
        context.put("object", new SimpleObject());

        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("20", writer.toString());
    }

    public class SimpleObject {
        public int small = 1;
        public int large = 20;
    }

}
