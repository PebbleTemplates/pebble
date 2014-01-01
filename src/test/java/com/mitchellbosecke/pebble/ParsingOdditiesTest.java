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

		PebbleTemplate template = pebble
				.loadTemplate("{{ _self.input(1 + 1) }}{% macro input(value) %}{{value}}{% endmacro %}");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("2", writer.toString());
	}

	@Test
	public void testStringConstantWithLinebreak() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		PebbleTemplate template = pebble.loadTemplate("{{ 'test\ntest' }}");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("test\ntest", writer.toString());
	}

	@Test(expected = SyntaxException.class)
	public void testStringWithDifferentQuotationMarks() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		PebbleTemplate template = pebble.loadTemplate("{{'test\"}}");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("test", writer.toString());
	}

	@Test
	public void testSingleQuoteWithinDoubleQuotes() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		PebbleTemplate template = pebble.loadTemplate("{{\"te'st\"}}");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("te'st", writer.toString());

		template = pebble.loadTemplate("{{\"te\\'st\"}}");
		writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("te\\'st", writer.toString());

		template = pebble.loadTemplate("{{'te\\'st'}}");
		writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("te'st", writer.toString());
	}

}
