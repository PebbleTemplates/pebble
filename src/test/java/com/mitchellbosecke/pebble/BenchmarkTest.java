/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

@Ignore
public class BenchmarkTest extends AbstractTest {

	@Test
	public void benchmarkEvaluations() throws PebbleException, IOException {
		Loader stringLoader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(stringLoader);

		PebbleTemplate template = pebble.getTemplate("hello {{ object.firstName }} {{ object.lastName }}");
		Map<String, Object> context = new HashMap<>();
		context.put("object", new SimpleObject());

		Writer writer = new StringWriter();
		int numberOfEvaluations = 1_000_000;
		
		for (int i = 0; i < numberOfEvaluations; i++) {
			template.evaluate(writer, context);
		}
	}
	
	/**
	 * Average on Mitchell's home computer: 47 seconds
	 * 
	 * @throws PebbleException
	 * @throws IOException
	 */
	@Test
	public void benchmarkCompilations() throws PebbleException, IOException {
		Loader stringLoader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(stringLoader);
		pebble.setTemplateCache(null);

		int numberOfCompilations = 1_000_000;
		
		@SuppressWarnings("unused")
		PebbleTemplate template = null;
		for (int i = 0; i < numberOfCompilations; i++) {
			template = pebble.getTemplate("hello {{ object.firstName }} {{ object.lastName }}");
		}
	}

	public class SimpleObject {
		public String firstName = "Steve";

		private String lastName = "Johnson";
		
		public String getFirstName(){
			return firstName;
		}

		public String getLastName() {
			return lastName;
		}
	}

}
