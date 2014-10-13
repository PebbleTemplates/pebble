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
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class CompilerTest extends AbstractTest {

	@Test
	public void testCompile() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		PebbleTemplate template = pebble.getTemplate("hello {{ foo }}");
		Map<String, Object> context = new HashMap<>();
		context.put("foo", "BAR");
		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("hello BAR", writer.toString());
	}

	/**
	 * There was an issue where one failed template would prevent future
	 * templates from being compiled.
	 * 
	 * @throws PebbleException
	 * @throws IOException
	 */
	@Test(timeout = 3000)
	public void testCompilationMutexIsAlwaysReleased() throws PebbleException, IOException {

		try {
			pebble.getTemplate("non-existing");
		} catch (Exception e) {

		}
		PebbleTemplate template = pebble.getTemplate("template.general.peb");
		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("test", writer.toString());
	}


}
