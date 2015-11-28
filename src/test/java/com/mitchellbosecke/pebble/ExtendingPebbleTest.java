/*******************************************************************************
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ExtendingPebbleTest extends AbstractTest {

    private final class CustomExtension extends AbstractExtension {

        @Override
        public Map<String, Filter> getFilters() {

            Map<String, Filter> filters = new HashMap<>();

            filters.put("noArgumentsButCanAccessContext", new Filter() {

                @Override
                public List<String> getArgumentNames() {
                    return null;
                }

                @Override
                public String apply(Object input, Map<String, Object> args) {
                    EvaluationContext context = (EvaluationContext) args.get("_context");
                    PebbleTemplateImpl pebbleTemplate = (PebbleTemplateImpl) args.get("_self");
                    if (context != null && pebbleTemplate != null) {
                        return "success";
                    } else {
                        return "failure";
                    }
                }

            });
            return filters;
        }
    }

    /**
     * Issue #51
     */
    @Test
    public void testFilterWithoutArgumentsCanAccessEvaluationContext() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false)
                .extension(new CustomExtension()).build();

        PebbleTemplate template = pebble.getTemplate("{{ 'test' | noArgumentsButCanAccessContext }}");

        Writer writer = new StringWriter();
        template.evaluate(writer);
        assertEquals("success", writer.toString());
    }
}
