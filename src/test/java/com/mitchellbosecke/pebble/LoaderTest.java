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

import org.junit.Test;

public class LoaderTest extends AbstractTest {

	@Test
	public void testGetSource() {
		String source = loader.getSource("loader.test");
		assertEquals("Loader did not get source correctly.", "test", source);
	}
	

}
