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

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class InheritanceTest extends AbstractTest {

	@Test
	public void testSimpleInheritance() {
		PebbleTemplate template = pebble.loadTemplate("inheritance/template.parent.peb");
		Map<String, Object> context = new HashMap<>();
		template.render(context);
	}

}
