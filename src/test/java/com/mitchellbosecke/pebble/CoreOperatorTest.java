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
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class CoreOperatorTest extends AbstractTest {

	@Test
	public void testUnaryOperators() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if -2 == -+(5 - 3) %}yes{% else %}no{% endif %}";
		PebbleTemplate template = pebble.loadTemplate(source);

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("yes", writer.toString());
	}

	@Test
	public void testNotUnaryOperator() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if not (true) %}yes{% else %}no{% endif %}";
		PebbleTemplate template = pebble.loadTemplate(source);

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("no", writer.toString());
	}

	@Test
	public void testBinaryOperators() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{{ 8 + 5 * 4 - (6 + 10 / 2)  + 44 }}-{{ 10%3 }}";
		PebbleTemplate template = pebble.loadTemplate(source);

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("61-1", writer.toString());
	}

	/**
	 * Problem existed where getAttribute would return an Object type which was
	 * an invalid operand for java's algebraic operators.
	 * 
	 * @throws PebbleException
	 */
	@Test
	public void testBinaryOperatorOnAttribute() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{{ 1 + item.changeInt }} " + "{{ 1 - item.changeInt }} " + "{{ 2 * item.changeInt }} "
				+ "{{ 11 / item.changeInt }} " + "{{ 4 % item.changeInt }}";
		PebbleTemplate template = pebble.loadTemplate(source);
		Map<String, Object> context = new HashMap<>();
		context.put("item", new Item());

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("4 -2 6 3 1", writer.toString());
	}

	/**
	 * Problem existed where getAttribute would return an Object type which was
	 * an invalid operand for java's algebraic operators.
	 * 
	 * @throws PebbleException
	 */
	@Test
	public void testUnaryOperatorOnAttribute() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if -5 > -item.changeInt %}yes{% else %}no{% endif %}";
		PebbleTemplate template = pebble.loadTemplate(source);
		Map<String, Object> context = new HashMap<>();
		context.put("item", new Item());

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("no", writer.toString());
	}

	@Test
	public void testNotUnaryOperatorOnAttribute() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if not(item.truthy) %}yes{% else %}no{% endif %}";
		PebbleTemplate template = pebble.loadTemplate(source);
		Map<String, Object> context = new HashMap<>();
		context.put("item", new Item());

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("no", writer.toString());
	}
	
	@Test
	public void testLogicOperatorOnAttributes() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if item.truthy and item.falsey %}yes{% else %}no{% endif %}"
				+ "{% if item.truthy or item.falsey %}yes{% else %}no{% endif %}";
		PebbleTemplate template = pebble.loadTemplate(source);
		Map<String, Object> context = new HashMap<>();
		context.put("item", new Item());

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("noyes", writer.toString());
	}


	@Test
	public void testTernary() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{{ true ? 1 : 2 }}-{{ 1 + 4 == 5 ?(2-1) : 2 }}";
		PebbleTemplate template = pebble.loadTemplate(source);

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("1-1", writer.toString());
	}

	@Test
	public void testComparisons() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if 3 > 2 %}yes{% endif %}{% if 2 > 3 %}no{% endif %}{% if 2 < 3 %}yes{% endif %}{% if 3 >= 3 %}yes{% endif %}{% if 100 <= 100 %}yes{% endif %}{% if 2 == 2 %}yes{% endif %}";
		PebbleTemplate template = pebble.loadTemplate(source);

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("yesyesyesyesyes", writer.toString());
	}
	
	@Test
	public void testComparisonsOnDifferingOperands() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if 3 > 2.0 %}yes{% endif %}";
		PebbleTemplate template = pebble.loadTemplate(source);

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("yes", writer.toString());
	}

	@Test()
	public void testEqualsOperator() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if 'test' equals obj2 %}yes{% endif %}{% if 'blue' equals 'red' %}no{% else %}yes{% endif %}";
		PebbleTemplate template = pebble.loadTemplate(source);
		Map<String, Object> context = new HashMap<>();
		context.put("obj2", new String("test"));

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("yesyes", writer.toString());
	}

	@Test()
	public void testEqualsOperatorWithNulls() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if null equals null %}yes{% endif %}{% if null equals obj %}yes{% else %}no{% endif %}";
		PebbleTemplate template = pebble.loadTemplate(source);
		Map<String, Object> context = new HashMap<>();
		context.put("obj", null);

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("yesyes", writer.toString());
	}

	@Test()
	public void testNotEqualsOperator() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if 'Mitchell' != name %}no{% else %}yes{% endif %}";
		PebbleTemplate template = pebble.loadTemplate(source);
		Map<String, Object> context = new HashMap<>();
		context.put("name", "Mitchell");

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("yes", writer.toString());
	}

	@Test()
	public void testEqualsOperatorWithPrimitives() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if 1 equals 1 %}yes{% endif %}{% if 3 equals item.changeInt %}yes{% else %}no{% endif %}";
		PebbleTemplate template = pebble.loadTemplate(source);
		Map<String, Object> context = new HashMap<>();
		context.put("item", new Item());

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("yesyes", writer.toString());
	}

	/**
	 * There was an issue where if one of the comparison operands came from a
	 * variable object, the template could not be compiled. This is because the
	 * getAttribute() method of the AbstractPebbleTemplate returns Objects and
	 * Objects can not be compared to primitives.
	 * 
	 * @throws PebbleException
	 */
	@Test()
	public void testComparisonWithAttributeOperand() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if item.change < 2.0 %}yes{% else %}no{% endif %}"
				+ "{% if item.change <= 2.0 %}yes{% else %}no{% endif %}"
				+ "{% if item.change > 2.0 %}yes{% else %}no{% endif %}"
				+ "{% if item.change >= 2.0 %}yes{% else %}no{% endif %}";
		PebbleTemplate template = pebble.loadTemplate(source);
		Map<String, Object> context = new HashMap<>();
		context.put("item", new Item());

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("yesyesnono", writer.toString());
	}

	public class Item {
		public double change = 1.234;
		public Integer changeInt = 3;
		public boolean truthy = true;
		public Boolean falsy = false;
	}
}
