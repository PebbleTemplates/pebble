package com.mitchellbosecke.pebble;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.mitchellbosecke.pebble.error.AttributeNotFoundException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.loader.PebbleDefaultLoader;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class EngineTest extends AbstractTest {

	@Test(expected = RuntimeException.class)
	public void strictVariablesNonExisting() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);
		pebble.setStrictVariables(true);

		PebbleTemplate template = pebble.loadTemplate("{{ nonExisting }}");

		Writer writer = new StringWriter();
		template.evaluate(writer);
	}

	@Test
	public void nonStrictVariablesNonExisting() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);
		pebble.setStrictVariables(false);

		PebbleTemplate template = pebble.loadTemplate("{{ nonExisting }}");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("", writer.toString());
	}

	@Test
	public void strictVariablesExistingButNull() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);
		pebble.setStrictVariables(true);

		PebbleTemplate template = pebble.loadTemplate("{{ existingButNull }}");
		Map<String, Object> context = new HashMap<>();
		context.put("existingButNull", null);

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("", writer.toString());
	}

	@Test(expected = AttributeNotFoundException.class)
	public void strictVariablesMissingAttribute() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);
		pebble.setStrictVariables(true);

		PebbleTemplate template = pebble.loadTemplate("{{ dave.username }}");
		Map<String, Object> context = new HashMap<>();
		context.put("dave", new User());

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("", writer.toString());
	}

	@Test
	public void nonStrictVariablesMissingAttribute() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);
		pebble.setStrictVariables(false);

		PebbleTemplate template = pebble.loadTemplate("{{ dave.username }}");
		Map<String, Object> context = new HashMap<>();
		context.put("dave", new User());

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("", writer.toString());
	}

	/**
	 * There was once an issue where the cache was unable to differentiate
	 * between templates of the same name but under different directories.
	 * 
	 * @throws PebbleException
	 */
	@Test
	public void templatesWithSameNameOverridingCache() throws PebbleException {
		Loader loader = new PebbleDefaultLoader();
		PebbleEngine engine = new PebbleEngine(loader);
		engine.setCacheTemplates(true);

		PebbleTemplate cache1 = engine.loadTemplate("templates/cache/cache1/template.cache.peb");
		PebbleTemplate cache2 = engine.loadTemplate("templates/cache/cache2/template.cache.peb");
		
		Writer writer1 = new StringWriter();
		Writer writer2 = new StringWriter();
		
		cache1.evaluate(writer1);
		cache2.evaluate(writer2);

		String cache1Output = writer1.toString();
		String cache2Output = writer2.toString();

		assertFalse(cache1Output.equals(cache2Output));

	}

	/**
	 * An issue occurred where the engine would mistake the existence of the
	 * template in it's cache with the existence of the templates bytecode in
	 * the file managers cache. This lead to compilation issues.
	 * 
	 * It occurred when rendering two templates that share the same parent
	 * template.
	 * 
	 * @throws PebbleException
	 */
	@Test
	public void templateCachedButBytecodeCleared() throws PebbleException {
		PebbleTemplate template1 = pebble.loadTemplate("template.parent.peb");
		PebbleTemplate template2 = pebble.loadTemplate("template.parent2.peb");

		Writer writer1 = new StringWriter();
		Writer writer2 = new StringWriter();
		
		template1.evaluate(writer1);
		template2.evaluate(writer2);
		
		assertEquals("GRANDFATHER TEXT ABOVE HEAD\n" + "\n" + "\tPARENT HEAD\n"
				+ "\nGRANDFATHER TEXT BELOW HEAD AND ABOVE FOOT\n\n" + "\tGRANDFATHER FOOT\n\n"
				+ "GRANDFATHER TEXT BELOW FOOT", writer1.toString());
		assertEquals("GRANDFATHER TEXT ABOVE HEAD\n" + "\n" + "\tPARENT HEAD\n"
				+ "\nGRANDFATHER TEXT BELOW HEAD AND ABOVE FOOT\n\n" + "\tGRANDFATHER FOOT\n\n"
				+ "GRANDFATHER TEXT BELOW FOOT", writer2.toString());
	}

	private class User {

	}
}
