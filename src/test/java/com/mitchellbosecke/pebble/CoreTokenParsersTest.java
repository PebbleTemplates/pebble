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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class CoreTokenParsersTest extends AbstractTest {

	@Test
	public void testBlock() throws PebbleException {
		PebbleTemplate template = pebble.loadTemplate("template.grandfather.peb");
		template.render(null);
	}

	@Test
	public void testIf() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);
		
		String source = "{% if false or steve == true  %}yes{% else %}p>no{% endif %}";
		PebbleTemplate template = pebble.loadTemplate(source);
		Map<String, Object> context = new HashMap<>();
		context.put("yes", true);
		template.render(context);
	}

	@Test
	public void testFor() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);
		
		String source = "{% for user in users %}{% if loop.index == 0 %}Length: {{ loop.length }}{% endif %}{{ loop.index }}{{ user.username }}{% endfor %}";
		PebbleTemplate template = pebble.loadTemplate(source);
		Map<String, Object> context = new HashMap<>();
		List<User> users = new ArrayList<>();
		users.add(new User("Alex"));
		users.add(new User("Bob"));
		context.put("users", users);
		assertEquals("Length: 20Alex1Bob", template.render(context));
	}

	@Test
	public void testForElse() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);
		
		String source = "{% for user in users %}{{ user.username }}{% else %}yes{% endfor %}";
		PebbleTemplate template = pebble.loadTemplate(source);
		Map<String, Object> context = new HashMap<>();
		List<User> users = new ArrayList<>();
		context.put("users", users);
		assertEquals("yes", template.render(context));
	}

	@Test
	public void testMacro() throws PebbleException {
		PebbleTemplate template = pebble.loadTemplate("template.macro1.peb");
		assertEquals("	<input name=\"company\" value=\"forcorp\" type=\"text\" />\n"
				+ "	<input name=\"company\" value=\"forcorp\" type=\"text\" data-overload=\"overloaded\"/>\n",
				template.render(new HashMap<String, Object>()));
	}

	@Test
	public void testMacroFromAnotherFile() throws PebbleException {
		PebbleTemplate template = pebble.loadTemplate("template.macro2.peb");
		assertEquals("	<input name=\"company\" value=\"forcorp\" type=\"text\" />\n", template.render());
	}

	@Test
	public void testInclude() throws PebbleException {
		PebbleTemplate template = pebble.loadTemplate("template.include1.peb");
		assertEquals("TEMPLATE2\nTEMPLATE1\nTEMPLATE2\n", template.render());
	}

	@Test
	public void testSet() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);
		
		String source = "{% set name = 'alex'  %}{{ name }}";
		PebbleTemplate template = pebble.loadTemplate(source);
		Map<String, Object> context = new HashMap<>();
		context.put("name", "steve");
		assertEquals("alex", template.render(context));
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
