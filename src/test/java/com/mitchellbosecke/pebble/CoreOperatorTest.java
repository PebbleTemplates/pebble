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

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class CoreOperatorTest extends AbstractTest {

	@Test
	public void testUnary() throws PebbleException {
		PebbleTemplate template = pebble.loadTemplate("template.operators.unary.peb");
		assertEquals("yes\nyes\n", template.render());
	}

	@Test
	public void testBinary() throws PebbleException {
		PebbleTemplate template = pebble.loadTemplate("template.operators.binary.peb");
		assertEquals("61\n1", template.render());
	}

	@Test
	public void testTernary() throws PebbleException {
		PebbleTemplate template = pebble.loadTemplate("template.operators.ternary.peb");
		assertEquals("11", template.render());
	}

	@Test
	public void testComparisons() throws PebbleException {
		PebbleTemplate template = pebble.loadTemplate("template.operators.comparisons.peb");
		assertEquals("three is greater than two\n" + "two is less than three\n"
				+ "three is greater than or equal to three\n" + "hundred is less than or equal to hundred\n"
				+ "two equals two\n", template.render());
	}

}
