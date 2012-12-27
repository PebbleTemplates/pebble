package com.mitchellbosecke.pebble;

import org.junit.Test;

public class PebbleEngineTest extends AbstractTest {

	@Test
	public void testLoad() throws Exception {
		// should be no exception
		pebble.loadTemplate("template.singleVariable.peb");
	}

}
