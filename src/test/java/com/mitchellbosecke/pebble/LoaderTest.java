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
