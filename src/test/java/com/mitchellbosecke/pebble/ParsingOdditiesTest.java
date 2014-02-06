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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class ParsingOdditiesTest extends AbstractTest {

	@Test
	public void testExpressionInArguments() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		PebbleTemplate template = pebble.compile("{{ input(1 + 1) }}{% macro input(value) %}{{value}}{% endmacro %}");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("2", writer.toString());
	}

	@Test
	public void testPositionalAndNamedArguments() throws PebbleException, IOException, ParseException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{{ stringDate | date('yyyy/MMMM/d', existingFormat='yyyy-MMMM-d') }}";

		PebbleTemplate template = pebble.compile(source);
		Map<String, Object> context = new HashMap<>();
		DateFormat format = new SimpleDateFormat("yyyy-MMMM-d");
		Date realDate = format.parse("2012-July-01");
		context.put("stringDate", format.format(realDate));

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("2012/July/1", writer.toString());
	}
	
	@Test(expected=PebbleException.class)
	public void testIncorrectlyNamedArgument() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		PebbleTemplate template = pebble.compile("{{ 'This is a test of the abbreviate filter' | abbreviate(WRONG=16) }}");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("This is a tes...", writer.toString());
	}

	@Test
	public void testStringConstantWithLinebreak() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		PebbleTemplate template = pebble.compile("{{ 'test\ntest' }}");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("test\ntest", writer.toString());
	}

	@Test(expected = ParserException.class)
	public void testStringWithDifferentQuotationMarks() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		PebbleTemplate template = pebble.compile("{{'test\"}}");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("test", writer.toString());
	}

	@Test
	public void testSingleQuoteWithinDoubleQuotes() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		PebbleTemplate template = pebble.compile("{{\"te'st\"}}");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("te'st", writer.toString());

		template = pebble.compile("{{\"te\\'st\"}}");
		writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("te\\'st", writer.toString());

		template = pebble.compile("{{'te\\'st'}}");
		writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("te'st", writer.toString());
	}

}
