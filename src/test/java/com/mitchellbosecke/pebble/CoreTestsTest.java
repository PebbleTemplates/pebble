/*******************************************************************************
 * Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class CoreTestsTest extends AbstractTest {

	@Test
	public void testEven() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);
		
		String source = "{% if 2 is even %}yes{% else %}no{% endif %}{% if 3 is even %}no{% else %}yes{% endif %}";
		PebbleTemplate template = pebble.loadTemplate(source);
		assertEquals("yesyes", template.render());
	}

	@Test
	public void testOdd() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);
		
		String source = "{% if 2 is odd %}no{% else %}yes{% endif %}{% if 3 is odd %}yes{% else %}no{% endif %}";
		PebbleTemplate template = pebble.loadTemplate(source);
		assertEquals("yesyes", template.render());
	}

	@Test
	public void testNull() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);
		
		String source = "{% if null is null %}yes{% endif %}{% if obj is null %}yes{% endif %}{% if 2 is null %}no{% else %}yes{% endif %}";
		PebbleTemplate template = pebble.loadTemplate(source);
		Map<String, Object> context = new HashMap<>();
		context.put("obj", null);
		assertEquals("yesyesyes", template.render(context));
	}

	@Test
	public void testEmpty() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);
		
		String source = "{% if null is empty() %}yes{% endif %}{% if '  ' is empty() %}yes{% endif %}{% if obj is empty() %}yes{% endif %}";
		PebbleTemplate template = pebble.loadTemplate(source);
		Map<String, Object> context = new HashMap<>();
		context.put("obj", new ArrayList<String>());
		assertEquals("yesyesyes", template.render());
	}

	@Test
	public void testIterables() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);
		
		String source = "{% if null is iterable() %}no{% else %}yes{% endif %}{% if obj1 is iterable() %}yes{% else %}no{% endif %}{% if obj2 is iterable() %}no{% else %}yes{% endif %}";
		PebbleTemplate template = pebble.loadTemplate(source);
		Map<String, Object> context = new HashMap<>();
		context.put("obj1", new ArrayList<String>());
		context.put("obj2", new HashMap<String, Object>());
		assertEquals("yesyesyes", template.render(context));
	}

	@Test
	public void testIsnt() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);
		
		String source = "{% if 2 is not odd %}yes{% else %}no{% endif %}{% if null is not iterable() %}yes{% else %}no{% endif %}";
		PebbleTemplate template = pebble.loadTemplate(source);
		assertEquals("yesyes", template.render());
	}

	@Test()
	public void testEqualToTest() throws PebbleException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);
		
		String source = "{% if 'test' is equalTo(obj2) %}yes{% endif %}{% if 'blue' is equalTo('red') %}no{% else %}yes{% endif %}";
		PebbleTemplate template = pebble.loadTemplate(source);
		Map<String, Object> context = new HashMap<>();
		context.put("obj2", new String("test"));
		assertEquals("yesyes", template.render(context));
	}
}
