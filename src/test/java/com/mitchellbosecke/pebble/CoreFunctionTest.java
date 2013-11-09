/*******************************************************************************
 * Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class CoreFunctionTest extends AbstractTest {

	@Test
	public void testParentFunction() throws PebbleException {
		PebbleTemplate template = pebble.loadTemplate("function/template.child.peb");
		assertEquals("parent text\n\t\tparent head\n\tchild head\n", template.render());
	}

	@Test
	public void testBlockFunction() throws PebbleException {
		PebbleTemplate template = pebble.loadTemplate("function/template.block.peb");
		assertEquals("Default Title\nDefault Title", template.render());
	}

}
