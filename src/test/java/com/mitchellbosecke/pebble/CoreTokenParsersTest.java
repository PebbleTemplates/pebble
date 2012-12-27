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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class CoreTokenParsersTest extends AbstractTest {

	@Test
	public void testBlock() {
		PebbleTemplate template = pebble.loadTemplate("template.grandfather.peb");
		template.render(null);
	}

	@Test
	public void testIf() {
		PebbleTemplate template = pebble.loadTemplate("template.if.peb");
		Map<String, Object> context = new HashMap<>();
		context.put("steve", true);
		template.render(context);
	}
	
	@Test
	public void testFor() {
		PebbleTemplate template = pebble.loadTemplate("template.for.peb");
		Map<String, Object> context = new HashMap<>();
		List<User> users = new ArrayList<>();
		users.add(new User("Alex"));
		users.add(new User("Bob"));
		users.add(new User("Steve"));
		users.add(new User("Sarah"));
		users.add(new User("Max"));
		context.put("users", users);
		template.render(context);
	}
	
	@Test
	public void testMacro(){
		PebbleTemplate template = pebble.loadTemplate("template.macro.peb");
		template.render(new HashMap<String,Object>());
	}
	
	
	
	public class User{
		private final String username;
		
		public User(String username){
			this.username = username;
		}
		
		public String getUsername(){
			return username;
		}
	}

}
