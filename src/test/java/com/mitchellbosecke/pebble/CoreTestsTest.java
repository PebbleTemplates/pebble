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
import java.util.Map;

import org.junit.Test;

import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class CoreTestsTest extends AbstractTest {

	@Test
	public void testEven() {
		PebbleTemplate template = pebble.loadTemplate("template.test.even.peb");
		assertEquals(" two is even.  three is odd. ", template.render());
	}
	
	@Test
	public void testOdd() {
		PebbleTemplate template = pebble.loadTemplate("template.test.odd.peb");
		assertEquals(" two is even.  three is odd. ", template.render());
	}
	
	@Test
	public void testNull() {
		PebbleTemplate template = pebble.loadTemplate("template.test.null.peb");
		Map<String,Object> context = new HashMap<>();
		context.put("obj", null);
		assertEquals(" null is null.  obj is null. ", template.render(context));
	}
	
	@Test
	public void testEmpty() {
		PebbleTemplate template = pebble.loadTemplate("template.test.empty.peb");
		Map<String,Object> context = new HashMap<>();
		context.put("obj", new ArrayList<String>());
		assertEquals(" null is empty.  blank is empty.  obj is empty. ", template.render());
	}
	
	@Test
	public void testIterables() {
		PebbleTemplate template = pebble.loadTemplate("template.test.iterable.peb");
		Map<String,Object> context = new HashMap<>();
		context.put("obj1", new ArrayList<String>());
		context.put("obj2", new HashMap<String, Object>());
		assertEquals(" null is not iterable.  one is iterable.  two is not iterable. ", template.render(context));
	}
	
	@Test
	public void testIsnt() {
		PebbleTemplate template = pebble.loadTemplate("template.test.isnt.peb");
		assertEquals(" two isnt odd.  null isnt iterable. ", template.render());
	}
	
	@Test()
	public void testEqualsTest() {
		PebbleTemplate template = pebble.loadTemplate("template.test.equals.peb");
		Map<String,Object> context = new HashMap<>();
		context.put("obj1", new String("test"));
		context.put("obj2", new String("test"));
		assertEquals("true\n\nfalse\n", template.render(context));
	}
}
