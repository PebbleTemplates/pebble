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

public class OperatorTest extends AbstractTest {

	@Test
	public void testBinary() {
		PebbleTemplate template = pebble.loadTemplate("template.math.peb");
		assertEquals("61\n1", template.render());
	}

	@Test
	public void testLogic() {
		PebbleTemplate template = pebble.loadTemplate("template.math2.peb");
		assertEquals("three is greater than two\n" + "two is less than three\n"
				+ "three is greater than or equal to three\n" + "hundred is less than or equal to hundred\n",
				template.render());
	}

	@Test
	public void testUnary() {
		PebbleTemplate template = pebble.loadTemplate("template.math.unary.peb");
		assertEquals("yes\nyes\n", template.render());
	}

}
