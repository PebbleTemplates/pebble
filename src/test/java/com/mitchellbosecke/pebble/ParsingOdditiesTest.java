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

import org.junit.Test;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.error.SyntaxException;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class ParsingOdditiesTest extends AbstractTest {

	@Test
	public void testExpressionInArguments() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		PebbleTemplate template = pebble.loadTemplate("{{ _self.input(1 + 1) }}{% macro input(value) %}{{value}}{% endmacro %}");
		assertEquals("2", template.render());
	}

	@Test
	public void testStringConstantWithLinebreak() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		PebbleTemplate template = pebble.loadTemplate("{{ 'test\ntest' }}");
		assertEquals("test\ntest", template.render());
	}


	@Test(expected = SyntaxException.class)
	public void testStringWithDifferentQuotationMarks() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		PebbleTemplate template = pebble.loadTemplate("{{'test\"}}");
		assertEquals("test", template.render()); 
	}
	
	@Test
	public void testSingleQuoteWithinDoubleQuotes() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		PebbleTemplate template = pebble.loadTemplate("{{\"te'st\"}}");
		assertEquals("te'st", template.render());
		
		template = pebble.loadTemplate("{{\"te\\'st\"}}");
		assertEquals("te\\'st", template.render());
		
		template = pebble.loadTemplate("{{'te\\'st'}}");
		assertEquals("te'st", template.render());
	}

}
