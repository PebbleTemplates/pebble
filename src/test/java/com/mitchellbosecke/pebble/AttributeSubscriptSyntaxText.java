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

public class AttributeSubscriptSyntaxText extends AbstractTest {
	@Test
	public void testAccessingValueWithSubscript() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);
		pebble.setStrictVariables(false);

		String source = "{{ person['first-name'] }}";
		PebbleTemplate template = pebble.getTemplate(source);

		Map<String, Object> context = new HashMap<>();
		context.put("person", new HashMap<String, Object>() {
			{
				put("first-name", "Bob");
			}
		});

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("Bob", writer.toString());
	}

	@Test
	public void testAccessingNestedValuesWithSubscript() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);
		pebble.setStrictVariables(false);

		String source = "{{ person['name']['first'] }}";
		PebbleTemplate template = pebble.getTemplate(source);

		Map<String, Object> context = new HashMap<>();
		context.put("person", new HashMap<String, Object>() {
			{
				put("name", new HashMap<String, Object>() {
					{
						put("first", "Bob");
					}
				});
			}
		});

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("Bob", writer.toString());
	}

	@Test
	public void testMixAndMatchingAttributeSyntax() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);
		pebble.setStrictVariables(false);

		String source = "{{ person['name'].first }}";
		PebbleTemplate template = pebble.getTemplate(source);

		Map<String, Object> context = new HashMap<>();
		context.put("person", new HashMap<String, Object>() {
			{
				put("name", new HashMap<String, Object>() {
					{
						put("first", "Bob");
					}
				});
			}
		});

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("Bob", writer.toString());


		source = "{{ person.name['first'] }}";
		template = pebble.getTemplate(source);

		writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("Bob", writer.toString());
	}
}
