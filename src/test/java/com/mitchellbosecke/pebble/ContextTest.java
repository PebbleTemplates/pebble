/*******************************************************************************
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.error.RootAttributeNotFoundException;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ContextTest extends AbstractTest {

    @SuppressWarnings("serial")
    @Test
    public void testLazyMap() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(true).build();

        PebbleTemplate template = pebble.getTemplate("{{ eager_key }} {{ lazy_key }}");
        Writer writer = new StringWriter();
        template.evaluate(writer, new HashMap<String, Object>() {

            {
                put("eager_key", "eager_value");
            }

            @Override
            public Object get(final Object key) {
                if ("lazy_key".equals(key)) {
                    return "lazy_value";
                }
                return super.get(key);
            }

            @Override
            public boolean containsKey(Object key) {
                if ("lazy_key".equals(key)) {
                    return true;
                }
                return super.containsKey(key);
            }
        });
        assertEquals("eager_value lazy_value", writer.toString());
    }

    @Test
    public void testMissingContextVariableWithoutStrictVariables() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

        PebbleTemplate template = pebble.getTemplate("{{ foo }}");
        Writer writer = new StringWriter();
        template.evaluate(writer);
        assertEquals("", writer.toString());
    }

    @Test(expected = RootAttributeNotFoundException.class)
    public void testMissingContextVariableWithStrictVariables() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(true).build();

        PebbleTemplate template = pebble.getTemplate("{{ foo }}");
        Writer writer = new StringWriter();
        template.evaluate(writer);
    }

    @Test
    public void testExistingButNullContextVariableWithStrictVariables() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(true).build();

        PebbleTemplate template = pebble.getTemplate("{% if foo == null %}YES{% endif %}");

        Map<String, Object> context = new HashMap<>();
        context.put("foo", null);

        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("YES", writer.toString());
    }

}
