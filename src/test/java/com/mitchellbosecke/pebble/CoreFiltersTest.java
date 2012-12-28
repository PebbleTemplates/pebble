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

import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class CoreFiltersTest extends AbstractTest {

	@Test
	public void testLower() {
		PebbleTemplate template = pebble.loadTemplate("template.filter.lower.peb");
		assertEquals("template",template.render());
	}
	
	@Test
	public void testUpper() {
		PebbleTemplate template = pebble.loadTemplate("template.filter.upper.peb");
		assertEquals("TEMPLATE",template.render());
	}



}
