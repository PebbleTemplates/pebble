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
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.mitchellbosecke.pebble.error.AttributeNotFoundException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class GetAttributeTest extends AbstractTest {

	@Test
	public void testOneLayerAttributeNesting() throws PebbleException, IOException {
		Loader stringLoader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(stringLoader);

		PebbleTemplate template = pebble.compile("hello {{ object.name }}");
		Map<String, Object> context = new HashMap<>();
		context.put("object", new SimpleObject());

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("hello Steve", writer.toString());
	}

	@Test
	public void testAttributeCacheHitting() throws PebbleException, IOException {
		Loader stringLoader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(stringLoader);

		PebbleTemplate template = pebble.compile("hello {{ object.name }}{{ object.name }}");
		Map<String, Object> context = new HashMap<>();
		context.put("object", new SimpleObject());

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
	}

	@Test
	public void testMultiLayerAttributeNesting() throws PebbleException, IOException {
		Loader stringLoader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(stringLoader);

		PebbleTemplate template = pebble.compile("hello {{ object.simpleObject2.simpleObject.name }}");
		Map<String, Object> context = new HashMap<>();
		context.put("object", new SimpleObject3());

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("hello Steve", writer.toString());
	}

	@Test
	public void testHashmapAttribute() throws PebbleException, IOException {
		Loader stringLoader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(stringLoader);

		PebbleTemplate template = pebble.compile("hello {{ object.name }}");
		Map<String, Object> context = new HashMap<>();
		Map<String, String> map = new HashMap<>();
		map.put("name", "Steve");
		context.put("object", map);

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("hello Steve", writer.toString());
	}

	@Test
	public void testMethodAttribute() throws PebbleException, IOException {
		Loader stringLoader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(stringLoader);

		PebbleTemplate template = pebble.compile("hello {{ object.name }}");
		Map<String, Object> context = new HashMap<>();
		context.put("object", new SimpleObject4());

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("hello Steve", writer.toString());
	}

	@Test
	public void testGetMethodAttribute() throws PebbleException, IOException {
		Loader stringLoader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(stringLoader);

		PebbleTemplate template = pebble.compile("hello {{ object.name }}");
		Map<String, Object> context = new HashMap<>();
		context.put("object", new SimpleObject5());

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("hello Steve", writer.toString());
	}

	@Test
	public void testHasMethodAttribute() throws PebbleException, IOException {
		Loader stringLoader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(stringLoader);

		PebbleTemplate template = pebble.compile("hello {{ object.name }}");
		Map<String, Object> context = new HashMap<>();
		context.put("object", new SimpleObject9());

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("hello Steve", writer.toString());
	}

	@Test
	public void testIsMethodAttribute() throws PebbleException, IOException {
		Loader stringLoader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(stringLoader);

		PebbleTemplate template = pebble.compile("hello {{ object.name }}");
		Map<String, Object> context = new HashMap<>();
		context.put("object", new SimpleObject6());

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("hello Steve", writer.toString());
	}

	@Test
	public void testComplexNestedAttributes() throws PebbleException, IOException {
		Loader stringLoader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(stringLoader);

		String source = "hello {{ object.map.SimpleObject2.simpleObject.name }}. My name is {{ object.map.SimpleObject6.name }}.";
		PebbleTemplate template = pebble.compile(source);
		Map<String, Object> context = new HashMap<>();
		context.put("object", new ComplexObject());

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("hello Steve. My name is Steve.", writer.toString());
	}

	@Test(expected = NullPointerException.class)
	public void testNullObjectWithStrictVariables() throws PebbleException, IOException {
		Loader stringLoader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(stringLoader);
		pebble.setStrictVariables(false);

		PebbleTemplate template = pebble.compile("hello {{ object.name }}");

		Writer writer = new StringWriter();
		template.evaluate(writer);
	}

	@Test
	public void testNonExistingAttributeWithoutStrictVariables() throws PebbleException, IOException {
		Loader stringLoader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(stringLoader);
		pebble.setStrictVariables(false);

		PebbleTemplate template = pebble.compile("hello {{ object.name }}");
		Map<String, Object> context = new HashMap<>();
		context.put("object", new Object());

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("hello ", writer.toString());
	}

	@Test(expected = AttributeNotFoundException.class)
	public void testNonExistingAttributeWithStrictVariables() throws PebbleException, IOException {
		Loader stringLoader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(stringLoader);
		pebble.setStrictVariables(true);

		PebbleTemplate template = pebble.compile("hello {{ object.name }}");
		Map<String, Object> context = new HashMap<>();
		context.put("object", new Object());

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("hello ", writer.toString());
	}

	@Test
	public void testNullAttributeWithoutStrictVariables() throws PebbleException, IOException {
		Loader stringLoader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(stringLoader);

		PebbleTemplate template = pebble.compile("hello {{ object.name }}");
		Map<String, Object> context = new HashMap<>();
		context.put("object", new SimpleObject7());

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("hello ", writer.toString());

	}

	/**
	 * Should behave the same as it does with strictVariables = false.
	 * 
	 * @throws PebbleException
	 * @throws IOException
	 */
	@Test
	public void testNullAttributeWithStrictVariables() throws PebbleException, IOException {
		Loader stringLoader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(stringLoader);
		pebble.setStrictVariables(true);

		PebbleTemplate template = pebble.compile("hello {{ object.name }}");
		Map<String, Object> context = new HashMap<>();
		context.put("object", new SimpleObject7());

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("hello ", writer.toString());

	}

	@Test()
	public void testPrimitiveAttribute() throws PebbleException, IOException {
		Loader stringLoader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(stringLoader);

		PebbleTemplate template = pebble.compile("hello {{ object.name }}");
		Map<String, Object> context = new HashMap<>();
		context.put("object", new SimpleObject8());

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("hello true", writer.toString());
	}

	public class SimpleObject {
		public final String name = "Steve";
	}

	public class SimpleObject2 {
		public final SimpleObject simpleObject = new SimpleObject();
	}

	public class SimpleObject3 {
		public final SimpleObject2 simpleObject2 = new SimpleObject2();
	}

	public class SimpleObject4 {
		public String name() {
			return "Steve";
		}
	}

	public class SimpleObject5 {
		public String getName() {
			return "Steve";
		}
	}

	public class SimpleObject6 {
		public String isName() {
			return "Steve";
		}
	}

	public class SimpleObject7 {
		public String name = null;
	}

	public class SimpleObject8 {
		public boolean name = true;
	}

	public class SimpleObject9 {
		public String hasName() {
			return "Steve";
		}
	}

	public class SimpleObject10 {
		public String getName(String name) {
			return name;
		}
	}

	public class ComplexObject {
		public final Map<String, Object> map = new HashMap<>();

		{
			map.put("SimpleObject2", new SimpleObject2());
			map.put("SimpleObject6", new SimpleObject6());
		}
	}
}
