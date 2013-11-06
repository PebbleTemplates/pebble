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
	public void testLower() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		PebbleTemplate template = pebble.loadTemplate("{{ 'TEMPLATE' | lower }}");
		assertEquals("template", template.render());
	}

	@Test
	public void testUpper() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		PebbleTemplate template = pebble.loadTemplate("{{ 'template' | upper }}");
		assertEquals("TEMPLATE", template.render());
	}

	@Test
	public void testDate() throws ParseException, PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{{ realDate | date(\"MM/dd/yyyy\") }}{{ realDate | date(format) }}{{ stringDate | date(\"yyyy-MMMM-d\", \"yyyy/MMMM/d\") }}";

		PebbleTemplate template = pebble.loadTemplate(source);
		Map<String, Object> context = new HashMap<>();
		DateFormat format = new SimpleDateFormat("yyyy-MMMM-d");
		Date realDate = format.parse("2012-July-01");
		context.put("realDate", realDate);
		context.put("stringDate", format.format(realDate));
		context.put("format", "yyyy-MMMM-d");
		assertEquals("07/01/20122012-July-12012/July/1", template.render(context));
	}

	@Test
	public void testUrlEncode() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		PebbleTemplate template = pebble.loadTemplate("{{ 'The string ü@foo-bar' | urlencode }}");
		assertEquals("The+string+%C3%BC%40foo-bar", template.render());
	}

	@Test
	public void testFormat() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		PebbleTemplate template = pebble.loadTemplate("{{ 'I like %s and %s.' | format(foo, 'bar') }}");
		Map<String, Object> context = new HashMap<>();
		context.put("foo", "foo");
		assertEquals("I like foo and bar.", template.render(context));
	}

	@Test
	public void testNumberFormat() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		PebbleTemplate template = pebble.loadTemplate("You owe me {{ 10000.235166 | number(currencyFormat) }}.");
		Map<String, Object> context = new HashMap<>();
		context.put("currencyFormat", "$#,###,###,##0.00");
		assertEquals("You owe me $10,000.24.", template.render(context));
	}

	@Test
	public void testAbbreviate() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		PebbleTemplate template = pebble
				.loadTemplate("{{ 'This is a test of the abbreviate filter' | abbreviate(16) }}");
		assertEquals("This is a tes...", template.render());
	}

	@Test
	public void testCapitalize() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		PebbleTemplate template = pebble.loadTemplate("{{ 'this should be capitalized.' | capitalize }}");
		assertEquals("This should be capitalized.", template.render());
	}

	@Test
	public void testTrim() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		PebbleTemplate template = pebble.loadTemplate("{{ '        		This should be trimmed. 		' | trim }}");
		assertEquals("This should be trimmed.", template.render());
	}

	@Test
	public void testJsonEncode() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		PebbleTemplate template = pebble.loadTemplate("{{ obj | json }}");
		Map<String, Object> context = new HashMap<>();
		context.put("obj", new User("Alex"));
		assertEquals("{\"username\":\"Alex\"}", template.render(context));
	}

	@Test
	public void testDefault() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		PebbleTemplate template = pebble
				.loadTemplate("{{ obj|default('Hello') }} {{ null|default('Steve') }} {{ '  ' |default('Hello') }}");
		Map<String, Object> context = new HashMap<>();
		context.put("obj", null);
		assertEquals("Hello Steve Hello", template.render(context));
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
