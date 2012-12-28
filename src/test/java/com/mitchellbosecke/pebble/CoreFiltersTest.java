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

import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class CoreFiltersTest extends AbstractTest {

	@Test
	public void testLower() {
		PebbleTemplate template = pebble.loadTemplate("template.filter.lower.peb");
		assertEquals("template", template.render());
	}

	@Test
	public void testUpper() {
		PebbleTemplate template = pebble.loadTemplate("template.filter.upper.peb");
		assertEquals("TEMPLATE", template.render());
	}

	@Test
	public void testDate() throws ParseException {
		PebbleTemplate template = pebble.loadTemplate("template.filter.date.peb");
		Map<String, Object> context = new HashMap<>();
		DateFormat format = new SimpleDateFormat("yyyy-MMMM-d");
		Date realDate = format.parse("2012-July-01");
		context.put("realDate", realDate);
		context.put("stringDate", format.format(realDate));
		context.put("format", "yyyy-MMMM-d");
		assertEquals("07/01/20122012/July/1", template.render(context));
	}

	@Test
	public void testUrlEncode() {
		PebbleTemplate template = pebble.loadTemplate("template.filter.url_encode.peb");
		assertEquals("The+string+%C3%BC%40foo-bar", template.render());
	}

	@Test
	public void testFormat() {
		PebbleTemplate template = pebble.loadTemplate("template.filter.format.peb");
		Map<String, Object> context = new HashMap<>();
		context.put("foo", "foo");
		assertEquals("I like foo and bar.", template.render(context));
	}

	@Test
	public void testNumberFormat() {
		PebbleTemplate template = pebble.loadTemplate("template.filter.number_format.peb");
		Map<String, Object> context = new HashMap<>();
		context.put("currencyFormat", "$#,###,###,##0.00");
		assertEquals("You owe me $10,000.24.", template.render(context));
	}

	@Test
	public void testAbbreviate() {
		PebbleTemplate template = pebble.loadTemplate("template.filter.abbreviate.peb");
		assertEquals("This is a tes...", template.render());
	}

	@Test
	public void testCapitalize() {
		PebbleTemplate template = pebble.loadTemplate("template.filter.capitalize.peb");
		assertEquals("This should be capitalized.", template.render());
	}

	@Test
	public void testTrim() {
		PebbleTemplate template = pebble.loadTemplate("template.filter.trim.peb");
		assertEquals("This should be trimmed.", template.render());
	}

	@Test
	public void testJsonEncode() {
		PebbleTemplate template = pebble.loadTemplate("template.filter.json_encode.peb");
		Map<String, Object> context = new HashMap<>();
		context.put("obj", new User("Alex"));
		assertEquals("{\"username\":\"Alex\"}", template.render(context));
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
