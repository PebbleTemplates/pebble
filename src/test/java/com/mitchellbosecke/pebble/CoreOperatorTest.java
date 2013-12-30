/*******************************************************************************
 * Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble;

import static org.junit.Assert.assertEquals;

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
		assertEquals("yes", template.render());
	}

	@Test
	public void testBinaryOperators() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);
		
		String source = "{{ 8 + 5 * 4 - (6 + 10 / 2)  + 44 }}-{{ 10%3 }}";
		PebbleTemplate template = pebble.loadTemplate(source);
		assertEquals("61-1", template.render());
	}

	@Test
	public void testTernary() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);
		
		String source = "{{ true ? 1 : 2 }}-{{ 1 + 4 == 5 ?(2-1) : 2 }}";
		PebbleTemplate template = pebble.loadTemplate(source);
		assertEquals("1-1", template.render());
	}

	@Test
	public void testComparisons() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if 3 > 2 %}yes{% endif %}{% if 2 > 3 %}no{% endif %}{% if 2 < 3 %}yes{% endif %}{% if 3 >= 3 %}yes{% endif %}{% if 100 <= 100 %}yes{% endif %}{% if 2 == 2 %}yes{% endif %}";
		PebbleTemplate template = pebble.loadTemplate(source);
		assertEquals("yesyesyesyesyes", template.render());
	}
	
	@Test()
	public void testEqualsOperator() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);
		
		String source = "{% if 'test' equals obj2 %}yes{% endif %}{% if 'blue' equals 'red' %}no{% else %}yes{% endif %}";
		PebbleTemplate template = pebble.loadTemplate(source);
		Map<String, Object> context = new HashMap<>();
		context.put("obj2", new String("test"));
		assertEquals("yesyes", template.render(context));
	}

	@Test()
	public void testEqualsOperatorWithNulls() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);
		
		String source = "{% if null equals null %}yes{% endif %}{% if null equals obj %}yes{% else %}no{% endif %}";
		PebbleTemplate template = pebble.loadTemplate(source);
		Map<String, Object> context = new HashMap<>();
		context.put("obj", null);
		assertEquals("yesyes", template.render(context));
	}
}
