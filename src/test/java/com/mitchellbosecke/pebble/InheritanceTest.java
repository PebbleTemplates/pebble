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
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class InheritanceTest extends AbstractTest {

	@Test
	public void testSimpleInheritance() throws PebbleException, IOException {
		PebbleTemplate template = pebble.getTemplate("template.parent.peb");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("GRANDFATHER TEXT ABOVE HEAD\n" + "\n" + "\tPARENT HEAD\n"
				+ "\nGRANDFATHER TEXT BELOW HEAD AND ABOVE FOOT\n\n" + "\tGRANDFATHER FOOT\n\n"
				+ "GRANDFATHER TEXT BELOW FOOT", writer.toString());
	}

	@Test
	public void testMultiLevelInheritance() throws PebbleException, IOException {
		PebbleTemplate template = pebble.getTemplate("template.child.peb");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("GRANDFATHER TEXT ABOVE HEAD\n" + "\n" + "\tCHILD HEAD\n"
				+ "\nGRANDFATHER TEXT BELOW HEAD AND ABOVE FOOT\n\n" + "\tGRANDFATHER FOOT\n\n"
				+ "GRANDFATHER TEXT BELOW FOOT", writer.toString());
	}
	
	@Test
	public void testDynamicInheritance() throws PebbleException, IOException {
		PebbleTemplate template = pebble.getTemplate("template.dynamicChild.peb");
		Map<String, Object> context = new HashMap<>();
		context.put("extendNumberOne", true);

		Writer writer1 = new StringWriter();
		template.evaluate(writer1, context);
		assertEquals("ONE", writer1.toString());
		
		Writer writer2 = new StringWriter();
		context.put("extendNumberOne", false);
		template.evaluate(writer2, context);
		assertEquals("TWO", writer2.toString());
	}
	
	@Test
	public void testNullParent() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);
		PebbleTemplate template = pebble
				.getTemplate("{% extends null %}success");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("success", writer.toString());
	}

}
