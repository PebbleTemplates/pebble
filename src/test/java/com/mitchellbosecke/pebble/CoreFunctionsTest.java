/*******************************************************************************
 * Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class CoreFunctionsTest extends AbstractTest {

	@Test
	public void testParentFunction() throws PebbleException {
		PebbleTemplate template = pebble.compile("function/template.child.peb");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("parent text\n\t\tparent head\n\tchild head\n", writer.toString());
	}

	/**
	 * Issue occurred where parent block didn't have access to the context when
	 * invoked via the parent() function.
	 * 
	 * @throws PebbleException
	 */
	@Test
	public void testParentBlockHasAccessToContext() throws PebbleException {
		PebbleTemplate template = pebble.compile("function/template.childWithContext.peb");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("bar", writer.toString());
	}

	@Test
	public void testBlockFunction() throws PebbleException {
		PebbleTemplate template = pebble.compile("function/template.block.peb");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("Default Title\nDefault Title", writer.toString());
	}

	@Test
	public void testSourceFunction() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{{ source() }}";
		PebbleTemplate template = pebble.compile(source);

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals(source, writer.toString());
	}

	@Test
	public void testMinFunction() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{{ min(8.0, 1, 4, 5, object.large) }}";
		PebbleTemplate template = pebble.compile(source);

		Map<String, Object> context = new HashMap<>();
		context.put("object", new SimpleObject());

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("1", writer.toString());
	}

	@Test
	public void testMaxFunction() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{{ max(8.0, 1, 4, 5, object.large) }}";
		PebbleTemplate template = pebble.compile(source);

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
