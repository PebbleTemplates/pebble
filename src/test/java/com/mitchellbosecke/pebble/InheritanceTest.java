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

public class InheritanceTest extends AbstractTest {

	@Test
	public void testSimpleInheritance() throws PebbleException {
		PebbleTemplate template = pebble.loadTemplate("template.parent.peb");
		assertEquals("GRANDFATHER TEXT ABOVE HEAD\n" + "\n" + "\tPARENT HEAD\n"
				+ "\nGRANDFATHER TEXT BELOW HEAD AND ABOVE FOOT\n\n" + "\tGRANDFATHER FOOT\n\n"
				+ "GRANDFATHER TEXT BELOW FOOT", template.render());
	}

}
