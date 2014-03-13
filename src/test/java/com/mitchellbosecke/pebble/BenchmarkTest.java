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
	public void benchmark() throws PebbleException, IOException {
		Loader stringLoader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(stringLoader);

		PebbleTemplate template = pebble.getTemplate("hello {{ object.firstName }} {{ object.lastName }}");
		Map<String, Object> context = new HashMap<>();
		context.put("object", new SimpleObject());

		Writer writer = new StringWriter();
		int numberOfEvaluations = 1000_000;
		
		for (int i = 0; i < numberOfEvaluations; i++) {
			template.evaluate(writer, context);
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
