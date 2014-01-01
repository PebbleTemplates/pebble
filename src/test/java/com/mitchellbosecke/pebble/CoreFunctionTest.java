/*******************************************************************************
 * Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;
import java.io.Writer;

import org.junit.Test;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class CoreFunctionTest extends AbstractTest {

	@Test
	public void testParentFunction() throws PebbleException {
		PebbleTemplate template = pebble.compile("function/template.child.peb");
		
		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("parent text\n\t\tparent head\n\tchild head\n", writer.toString());
	}

	@Test
	public void testBlockFunction() throws PebbleException {
		PebbleTemplate template = pebble.compile("function/template.block.peb");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("Default Title\nDefault Title", writer.toString());
	}

}
