/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import com.mitchellbosecke.pebble.utils.Pair;

public class CoreOperatorsTest extends AbstractTest {

	@Test
	public void testUnaryOperators() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if -2 == -+(5 - 3) %}yes{% else %}no{% endif %}";
		PebbleTemplate template = pebble.getTemplate(source);

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("yes", writer.toString());
	}

	@Test
	public void testNotUnaryOperator() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if not (true) %}yes{% else %}no{% endif %}";
		PebbleTemplate template = pebble.getTemplate(source);

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("no", writer.toString());
	}

	@Test
	public void testBinaryOperators() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{{ 8 + 5 * 4 - (6 + 10 / 2)  + 44 }}-{{ 10%3 }}";
		PebbleTemplate template = pebble.getTemplate(source);

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
	public void testBinaryOperatorOnAttribute() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{{ 1 + item.changeInt }} " + "{{ 1 - item.changeInt }} " + "{{ 2 * item.changeInt }} "
				+ "{{ 11 / item.changeInt }} " + "{{ 4 % item.changeInt }}";
		PebbleTemplate template = pebble.getTemplate(source);
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
	public void testUnaryOperatorOnAttribute() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if -5 > -item.changeInt %}yes{% else %}no{% endif %}";
		PebbleTemplate template = pebble.getTemplate(source);
		Map<String, Object> context = new HashMap<>();
		context.put("item", new Item());

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("no", writer.toString());
	}

	@Test
	public void testNotUnaryOperatorOnAttribute() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if not(item.truthy) %}yes{% else %}no{% endif %}";
		PebbleTemplate template = pebble.getTemplate(source);
		Map<String, Object> context = new HashMap<>();
		context.put("item", new Item());

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("no", writer.toString());
	}

	@Test
	public void testLogicOperatorOnAttributes() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if item.truthy and item.falsy %}yes{% else %}no{% endif %}"
				+ "{% if item.truthy or item.falsy %}yes{% else %}no{% endif %}";
		PebbleTemplate template = pebble.getTemplate(source);
		Map<String, Object> context = new HashMap<>();
		context.put("item", new Item());

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("noyes", writer.toString());
	}

	@Test
	public void testTernary() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{{ true ? 1 : 2 }}-{{ 1 + 4 == 5 ?(2-1) : 2 }}";
		PebbleTemplate template = pebble.getTemplate(source);

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("1-1", writer.toString());
	}

	@Test
	public void testComparisons() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if 3 > 2 %}yes{% endif %}{% if 2 > 3 %}no{% endif %}{% if 2 < 3 %}yes{% endif %}{% if 3 >= 3 %}yes{% endif %}{% if 100 <= 100 %}yes{% endif %}{% if 2 == 2 %}yes{% endif %}";
		PebbleTemplate template = pebble.getTemplate(source);

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("yesyesyesyesyes", writer.toString());
	}

	@Test
	public void testComparisonsOnDifferingOperands() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if 3 > 2.0 %}yes{% endif %}";
		PebbleTemplate template = pebble.getTemplate(source);

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("yes", writer.toString());
	}

	@Test()
	public void testEqualsOperator() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if 'test' equals obj2 %}yes{% endif %}{% if 'blue' equals 'red' %}no{% else %}yes{% endif %}";
		PebbleTemplate template = pebble.getTemplate(source);
		Map<String, Object> context = new HashMap<>();
		context.put("obj2", new String("test"));

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("yesyes", writer.toString());
	}

	@Test()
	public void testEqualsOperatorWithNulls() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if null equals null %}yes{% endif %}{% if null equals obj %}yes{% else %}no{% endif %}";
		PebbleTemplate template = pebble.getTemplate(source);
		Map<String, Object> context = new HashMap<>();
		context.put("obj", null);

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("yesyes", writer.toString());
	}

	@Test()
	public void testNotEqualsOperator() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if 'Mitchell' != name %}no{% else %}yes{% endif %}";
		PebbleTemplate template = pebble.getTemplate(source);
		Map<String, Object> context = new HashMap<>();
		context.put("name", "Mitchell");

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("yes", writer.toString());
	}

	@Test()
	public void testEqualsOperatorWithPrimitives() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if 1 equals 1 %}yes{% endif %}{% if 3 equals item.changeInt %}yes{% else %}no{% endif %}";
		PebbleTemplate template = pebble.getTemplate(source);
		Map<String, Object> context = new HashMap<>();
		context.put("item", new Item());

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("yesyes", writer.toString());
	}

	/**
	 * There was an bug where two Number objects (Integer, Double etc.) were
	 * compared for equality using ==. This was fixed to use equals().
	 *
	 * @see https://github.com/mbosecke/pebble/issues/46
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test()
	public void testEqualsOperatorWithNumberObjects() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if (v == 1) %}num1{% elseif (v == 999999) %}num999999{% else %}no{% endif %}";
		PebbleTemplate template = pebble.getTemplate(source);

		// Test Integer, Long, Float and Double.
		List<Pair<Number, String>> tests = new ArrayList<>();
		tests.add(new Pair(1, "num1"));
		tests.add(new Pair(999999, "num999999"));
		tests.add(new Pair(1l, "num1"));
		tests.add(new Pair(999999l, "num999999"));
		tests.add(new Pair(1f, "num1"));
		tests.add(new Pair(999999f, "num999999"));
		tests.add(new Pair(1d, "num1"));
		tests.add(new Pair(999999d, "num999999"));

		for (Pair<Number, String> test : tests) {
			Map<String, Object> context = new HashMap<>();
			context.put("v", test.getLeft());

			Writer writer = new StringWriter();
			template.evaluate(writer, context);
			assertEquals(test.getRight(), writer.toString());
		}
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
	public void testComparisonWithAttributeOperand() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if item.change < 2.0 %}yes{% else %}no{% endif %}"
				+ "{% if item.change <= 2.0 %}yes{% else %}no{% endif %}"
				+ "{% if item.change > 2.0 %}yes{% else %}no{% endif %}"
				+ "{% if item.change >= 2.0 %}yes{% else %}no{% endif %}";
		PebbleTemplate template = pebble.getTemplate(source);
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
	
	@Test()
	public void testIsOperatorPrecedence() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if 1 + 2 is odd %} true {% else %} false {% endif %}";
		PebbleTemplate template = pebble.getTemplate(source);

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals(" true ", writer.toString());
	}
	
	@Test()
	public void testIsOperatorPrecedenceWithAnd() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if 3 is odd and 5 is odd %} true {% else %} false {% endif %}";
		PebbleTemplate template = pebble.getTemplate(source);

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals(" true ", writer.toString());
	}
}
