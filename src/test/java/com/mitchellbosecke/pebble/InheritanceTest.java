package com.mitchellbosecke.pebble;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class InheritanceTest extends AbstractTest {

	@Test
	public void testSimpleInheritance() {
		PebbleTemplate template = pebble.loadTemplate("template.parent.peb");
		Map<String, Object> context = new HashMap<>();
		template.render(context);
	}

}
