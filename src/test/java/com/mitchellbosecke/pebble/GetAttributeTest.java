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

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class GetAttributeTest extends AbstractTest {

	@Test
	public void testOneLayerAttributeNesting() {
		PebbleTemplate template = pebble.loadTemplate("template.singleAttribute.peb");
		Map<String,Object> model = new HashMap<>();
		model.put("object", new SimpleObject());
		assertEquals(template.render(model), "hello Steve");
	}
	
	@Test
	public void testMultiLayerAttributeNesting() {
		PebbleTemplate template = pebble.loadTemplate("template.nestedAttributes.peb");
		Map<String,Object> model = new HashMap<>();
		model.put("object", new SimpleObject3());
		assertEquals(template.render(model), "hello Steve");
	}
	
	@Test
	public void testHashmapAttribute() {
		PebbleTemplate template = pebble.loadTemplate("template.singleAttribute.peb");
		Map<String,Object> model = new HashMap<>();
		Map<String,String> map = new HashMap<>();
		map.put("name", "Steve");
		model.put("object", map);
		assertEquals("hello Steve", template.render(model));
	}
	
	@Test
	public void testMethodAttribute() {
		PebbleTemplate template = pebble.loadTemplate("template.singleAttribute.peb");
		Map<String,Object> model = new HashMap<>();
		model.put("object", new SimpleObject4());
		assertEquals("hello Steve", template.render(model));
	}
	
	@Test
	public void testGetMethodAttribute() {
		PebbleTemplate template = pebble.loadTemplate("template.singleAttribute.peb");
		Map<String,Object> model = new HashMap<>();
		model.put("object", new SimpleObject5());
		assertEquals("hello Steve", template.render(model));
	}
	
	@Test
	public void testIsMethodAttribute() {
		PebbleTemplate template = pebble.loadTemplate("template.singleAttribute.peb");
		Map<String,Object> model = new HashMap<>();
		model.put("object", new SimpleObject6());
		assertEquals("hello Steve", template.render(model));
	}
	
	@Test
	public void testComplexNestedAttributes() {
		PebbleTemplate template = pebble.loadTemplate("template.complexAttributes.peb");
		Map<String,Object> model = new HashMap<>();
		model.put("object", new ComplexObject());
		assertEquals("hello Steve. My name is Steve.", template.render(model));
	}
	
	@Test(expected = PebbleException.class)
	public void testNullObject() {
		PebbleTemplate template = pebble.loadTemplate("template.singleAttribute.peb");
		Map<String,Object> model = new HashMap<>();
		template.render(model);
	}
	
	@Test(expected = PebbleException.class)
	public void testNonExistingAttribute() {
		PebbleTemplate template = pebble.loadTemplate("template.singleAttribute.peb");
		Map<String,Object> model = new HashMap<>();
		model.put("object", new Object());
		template.render(model);
	}
	
	@Test()
	public void testNullAttribute() {
		PebbleTemplate template = pebble.loadTemplate("template.singleAttribute.peb");
		Map<String,Object> model = new HashMap<>();
		model.put("object", new SimpleObject7());
		assertEquals("hello null", template.render(model));
	}
	
	@Test()
	public void testPrimitiveAttribute() {
		PebbleTemplate template = pebble.loadTemplate("template.singleAttribute.peb");
		Map<String,Object> model = new HashMap<>();
		model.put("object", new SimpleObject8());
		assertEquals("hello true", template.render(model));
	}
	
	
	public class SimpleObject{
		public final String name = "Steve";
	}
	
	public class SimpleObject2{
		public final SimpleObject simpleObject = new SimpleObject();
	}
	
	public class SimpleObject3{
		public final SimpleObject2 simpleObject2 = new SimpleObject2();
	}
	
	public class SimpleObject4{
		public String name(){ return "Steve"; }
	}
	
	public class SimpleObject5{
		public String getName(){ return "Steve"; }
	}
	
	public class SimpleObject6{
		public String isName(){ return "Steve"; }
	}
	
	public class SimpleObject7{
		public String name = null;
	}
	
	public class SimpleObject8{
		public boolean name = true;
	}
	
	public class ComplexObject{
		public final Map<String,Object> map = new HashMap<>();
		
		{
			map.put("SimpleObject2", new SimpleObject2());
			map.put("SimpleObject6", new SimpleObject6());
		}
	}
}
