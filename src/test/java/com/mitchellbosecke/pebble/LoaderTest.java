package com.mitchellbosecke.pebble;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;

import org.junit.Test;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.ClassLoaderLoader;
import com.mitchellbosecke.pebble.loader.FileLoader;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class LoaderTest extends AbstractTest {

	@Test
	public void testClassLoaderLoader() throws PebbleException, IOException {
		Loader loader = new ClassLoaderLoader();
		loader.setPrefix("templates");
		PebbleEngine engine = new PebbleEngine(loader);
		PebbleTemplate template1 = engine.compile("template.loaderTest.peb");
		Writer writer1 = new StringWriter();
		template1.evaluate(writer1);
		assertEquals("SUCCESS", writer1.toString());

	}

	@Test
	public void testClassLoaderLoaderWithNestedTemplate() throws PebbleException, IOException {
		Loader loader = new ClassLoaderLoader();
		loader.setPrefix("templates");
		PebbleEngine engine = new PebbleEngine(loader);
		PebbleTemplate template1 = engine.compile("loader/template.loaderTest.peb");
		Writer writer1 = new StringWriter();
		template1.evaluate(writer1);
		assertEquals("SUCCESS", writer1.toString());

	}

	@Test
	public void testFileLoader() throws PebbleException, IOException {
		Loader loader = new FileLoader();
		PebbleEngine engine = new PebbleEngine(loader);
		URL url = getClass().getResource("/templates/template.loaderTest.peb");
		PebbleTemplate template1 = engine.compile(url.getPath());
		Writer writer1 = new StringWriter();
		template1.evaluate(writer1);
		assertEquals("SUCCESS", writer1.toString());

	}
}
