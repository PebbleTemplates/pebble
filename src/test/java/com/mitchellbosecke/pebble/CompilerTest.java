/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2012 Mitchell Bosecke.
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 
 * Unported License. To view a copy of this license, visit 
 * http://creativecommons.org/licenses/by-sa/3.0/
 ******************************************************************************/
package com.mitchellbosecke.pebble;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class CompilerTest extends AbstractTest {

	@Test
	public void testCompile() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);
		
		PebbleTemplate template = pebble.loadTemplate("hello {{ test }}");
		Map<String, Object> context = new HashMap<>();
		context.put("test", "TEST");
		assertEquals(template.render(context), "hello TEST");
	}

	@Test
	public void testEscapeCharactersText() throws PebbleException {
		PebbleTemplate template = pebble.loadTemplate("template.escapeCharactersInText.peb");
		Map<String, Object> context = new HashMap<>();
		template.render(context);
	}

}
