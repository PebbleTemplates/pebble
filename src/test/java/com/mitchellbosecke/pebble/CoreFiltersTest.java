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

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class CoreFiltersTest extends AbstractTest {

	@Test
	public void testLower() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		PebbleTemplate template = pebble.compile("{{ 'TEMPLATE' | lower }}");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("template", writer.toString());
	}

	@Test
	public void testUpper() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		PebbleTemplate template = pebble.compile("{{ 'template' | upper }}");
		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("TEMPLATE", writer.toString());
	}

	@Test
	public void testDate() throws ParseException, PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{{ realDate | date(\"MM/dd/yyyy\") }}{{ realDate | date(format) }}{{ stringDate | date(\"yyyy-MMMM-d\", \"yyyy/MMMM/d\") }}";

		PebbleTemplate template = pebble.compile(source);
		Map<String, Object> context = new HashMap<>();
		DateFormat format = new SimpleDateFormat("yyyy-MMMM-d");
		Date realDate = format.parse("2012-July-01");
		context.put("realDate", realDate);
		context.put("stringDate", format.format(realDate));
		context.put("format", "yyyy-MMMM-d");
		
		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("07/01/20122012-July-12012/July/1", writer.toString());
	}

	@Test
	public void testUrlEncode() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		PebbleTemplate template = pebble.compile("{{ 'The string ü@foo-bar' | urlencode }}");
		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("The+string+%C3%BC%40foo-bar", writer.toString());
	}

	@Test
	public void testFormat() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		PebbleTemplate template = pebble.compile("{{ 'I like %s and %s.' | format(foo, 'bar') }}");
		Map<String, Object> context = new HashMap<>();
		context.put("foo", "foo");
		
		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("I like foo and bar.", writer.toString());
	}

	@Test
	public void testNumberFilterWithFormat() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		PebbleTemplate template = pebble.compile("You owe me {{ 10000.235166 | number(currencyFormat) }}.");
		Map<String, Object> context = new HashMap<>();
		context.put("currencyFormat", "$#,###,###,##0.00");
		
		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("You owe me $10,000.24.", writer.toString());
	}
	
	@Test
	public void testNumberFilterWithLocale() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		PebbleTemplate template = pebble.compile("{{ 1000000 | number }}");
		
		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("1,000,000", writer.toString());
	}

	@Test
	public void testAbbreviate() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		PebbleTemplate template = pebble
				.compile("{{ 'This is a test of the abbreviate filter' | abbreviate(16) }}");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("This is a tes...", writer.toString());
	}

	@Test
	public void testCapitalize() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		PebbleTemplate template = pebble.compile("{{ 'this should be capitalized.' | capitalize }}");
		
		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("This should be capitalized.", writer.toString());
	}

	@Test
	public void testTrim() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		PebbleTemplate template = pebble.compile("{{ '        		This should be trimmed. 		' | trim }}");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("This should be trimmed.", writer.toString());
	}

	@Test
	public void testJsonEncode() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		PebbleTemplate template = pebble.compile("{{ obj | json }}");
		Map<String, Object> context = new HashMap<>();
		context.put("obj", new User("Alex"));

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("{\"username\":\"Alex\"}", writer.toString());
	}

	@Test
	public void testDefault() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		PebbleTemplate template = pebble
				.compile("{{ obj|default('Hello') }} {{ null|default('Steve') }} {{ '  ' |default('Hello') }}");
		Map<String, Object> context = new HashMap<>();
		context.put("obj", null);

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("Hello Steve Hello", writer.toString());
	}

	public class User {
		private final String username;

		public User(String username) {
			this.username = username;
		}

		public String getUsername() {
			return username;
		}
	}

}
