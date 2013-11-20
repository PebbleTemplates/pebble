package com.mitchellbosecke.pebble;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.mitchellbosecke.pebble.error.AttributeNotFoundException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class EngineTest extends AbstractTest{

	@Test(expected = RuntimeException.class)
	public void strictVariablesNonExisting() throws PebbleException{
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);
		pebble.setStrictVariables(true);

		PebbleTemplate template = pebble
				.loadTemplate("{{ nonExisting }}");
		template.render();
	}
	
	@Test
	public void nonStrictVariablesNonExisting() throws PebbleException{
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);
		pebble.setStrictVariables(false);

		PebbleTemplate template = pebble
				.loadTemplate("{{ nonExisting }}");
		assertEquals("", template.render());
	}
	
	@Test
	public void strictVariablesExistingButNull() throws PebbleException{
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);
		pebble.setStrictVariables(true);

		PebbleTemplate template = pebble
				.loadTemplate("{{ existingButNull }}");
		Map<String, Object> context = new HashMap<>();
		context.put("existingButNull", null);
		assertEquals("", template.render(context));
	}
	
	@Test(expected = AttributeNotFoundException.class)
	public void strictVariablesMissingAttribute() throws PebbleException{
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);
		pebble.setStrictVariables(true);

		PebbleTemplate template = pebble
				.loadTemplate("{{ dave.username }}");
		Map<String, Object> context = new HashMap<>();
		context.put("dave", new User());
		assertEquals("", template.render(context));
	}
	
	@Test
	public void nonStrictVariablesMissingAttribute() throws PebbleException{
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);
		pebble.setStrictVariables(false);

		PebbleTemplate template = pebble
				.loadTemplate("{{ dave.username }}");
		Map<String, Object> context = new HashMap<>();
		context.put("dave", new User());
		assertEquals("", template.render(context));
	}
	
	private class User {
		
	}
}
