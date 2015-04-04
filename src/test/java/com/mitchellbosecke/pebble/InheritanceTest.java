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

public class InheritanceTest extends AbstractTest {

	public static final String LINE_SEPARATOR = System.lineSeparator();
	
	@Test
	public void testSimpleInheritance() throws PebbleException, IOException {
		PebbleTemplate template = pebble.getTemplate("template.parent.peb");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("GRANDFATHER TEXT ABOVE HEAD" + LINE_SEPARATOR + LINE_SEPARATOR + "\tPARENT HEAD" + LINE_SEPARATOR 
				+ LINE_SEPARATOR + "GRANDFATHER TEXT BELOW HEAD AND ABOVE FOOT" + LINE_SEPARATOR + LINE_SEPARATOR + "\tGRANDFATHER FOOT" + LINE_SEPARATOR + LINE_SEPARATOR 
				+ "GRANDFATHER TEXT BELOW FOOT", writer.toString());
	}

	@Test
	public void testMultiLevelInheritance() throws PebbleException, IOException {
		PebbleTemplate template = pebble.getTemplate("template.child.peb");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("GRANDFATHER TEXT ABOVE HEAD" + LINE_SEPARATOR + LINE_SEPARATOR + "\tCHILD HEAD" + LINE_SEPARATOR 
				+ LINE_SEPARATOR + "GRANDFATHER TEXT BELOW HEAD AND ABOVE FOOT" + LINE_SEPARATOR + LINE_SEPARATOR + "\tGRANDFATHER FOOT" + LINE_SEPARATOR + LINE_SEPARATOR 
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
