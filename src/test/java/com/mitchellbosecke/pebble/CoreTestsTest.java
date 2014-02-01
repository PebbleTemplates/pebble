/*******************************************************************************
 * Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class CoreTestsTest extends AbstractTest {

	@Test
	public void testEven() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if 2 is even %}yes{% else %}no{% endif %}{% if 3 is even %}no{% else %}yes{% endif %}";
		PebbleTemplate template = pebble.compile(source);

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("yesyes", writer.toString());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testNullEven() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if null is even %}yes{% else %}no{% endif %}";
		PebbleTemplate template = pebble.compile(source);

		Writer writer = new StringWriter();
		template.evaluate(writer);
	}

	@Test
	public void testOdd() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if 2 is odd %}no{% else %}yes{% endif %}{% if 3 is odd %}yes{% else %}no{% endif %}";
		PebbleTemplate template = pebble.compile(source);

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("yesyes", writer.toString());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testNullOdd() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if null is odd %}yes{% else %}no{% endif %}";
		PebbleTemplate template = pebble.compile(source);

		Writer writer = new StringWriter();
		template.evaluate(writer);
	}

	@Test
	public void testNull() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if null is null %}yes{% endif %}{% if obj is null %}yes{% endif %}{% if 2 is null %}no{% else %}yes{% endif %}";
		PebbleTemplate template = pebble.compile(source);
		Map<String, Object> context = new HashMap<>();
		context.put("obj", null);

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("yesyesyes", writer.toString());
	}

	@Test
	public void testEmpty() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if null is empty() %}yes{% endif %}{% if '  ' is empty() %}yes{% endif %}{% if obj is empty() %}yes{% endif %}";
		PebbleTemplate template = pebble.compile(source);
		Map<String, Object> context = new HashMap<>();
		context.put("obj", new ArrayList<String>());

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("yesyesyes", writer.toString());
	}

	@Test
	public void testIterables() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if null is iterable() %}no{% else %}yes{% endif %}{% if obj1 is iterable() %}yes{% else %}no{% endif %}{% if obj2 is iterable() %}no{% else %}yes{% endif %}";
		PebbleTemplate template = pebble.compile(source);
		Map<String, Object> context = new HashMap<>();
		context.put("obj1", new ArrayList<String>());
		context.put("obj2", new HashMap<String, Object>());

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("yesyesyes", writer.toString());
	} 

	@Test
	public void testIsnt() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if 2 is not odd %}yes{% else %}no{% endif %}{% if null is not iterable() %}yes{% else %}no{% endif %}";
		PebbleTemplate template = pebble.compile(source);

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("yesyes", writer.toString());
	}
}
