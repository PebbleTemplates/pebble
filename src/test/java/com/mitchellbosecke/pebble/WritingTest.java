/*******************************************************************************
 * Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.junit.Test;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class WritingTest extends AbstractTest {

	/**
	 * There was an issue where the pebble engine was closing the provided
	 * writer. This is wrong.
	 * 
	 * @throws PebbleException
	 * @throws IOException
	 */
	@Test
	public void testMultipleEvaluationsWithOneWriter() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		PebbleTemplate template1 = pebble.compile("first");
		PebbleTemplate template2 = pebble.compile("second");

		Writer writer = new UncloseableWriter();
		template1.evaluate(writer);
		template2.evaluate(writer);

		assertEquals("firstsecond", writer.toString());
	}
	
	public class UncloseableWriter extends StringWriter {
		
		@Override
		public void close(){
			throw new RuntimeException("Can not close this writer.");
		}
	}

}
