/*
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble;

import static org.junit.Assert.assertEquals;

import com.mitchellbosecke.pebble.attributes.AttributeResolver;
import com.mitchellbosecke.pebble.attributes.ResolvedAttribute;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class ExtendingPebbleTest {

  /**
   * Issue #51
   */
  @Test
  public void testFilterWithoutArgumentsCanAccessEvaluationContext()
      throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder()
        .loader(new StringLoader())
        .strictVariables(false)
        .extension(new CustomExtensionWithFilter())
        .build();

    PebbleTemplate template = pebble.getTemplate("{{ 'test' | noArgumentsButCanAccessContext }}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("success", writer.toString());
  }

  @Test
  public void testCustomAttributeResolverEvaluateFirst() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder()
        .loader(new StringLoader())
        .strictVariables(false)
        .extension(new CustomExtensionWithAttributeResolver())
        .build();

    PebbleTemplate template = pebble.getTemplate("hello {{ person.name }}");
    Map<String, Object> context = new HashMap<>();
    context.put("person", new SimplePerson());

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("hello customAttributeResolver", writer.toString());
  }

  private static final class CustomExtensionWithFilter extends AbstractExtension {

    @Override
    public Map<String, Filter> getFilters() {

      Map<String, Filter> filters = new HashMap<>();

      filters.put("noArgumentsButCanAccessContext", new Filter() {

        @Override
        public List<String> getArgumentNames() {
          return null;
        }

        @Override
        public String apply(Object input, Map<String, Object> args, PebbleTemplate self,
            EvaluationContext context, int lineNumber) {
          if (context != null && self != null) {
            return "success";
          } else {
            return "failure";
          }
        }

      });
      return filters;
    }
  }

  private static final class CustomExtensionWithAttributeResolver extends AbstractExtension {

    @Override
    public List<AttributeResolver> getAttributeResolver() {

      List<AttributeResolver> attributeResolvers = new ArrayList<>();
      attributeResolvers.add(
          (instance, attribute, argumentValues, args, isStrictVariables, filename, lineNumber) ->
              new ResolvedAttribute("customAttributeResolver"));
      return attributeResolvers;
    }
  }

  private static class SimplePerson {

    public final String name = "Bob";
  }
}
