package com.mitchellbosecke.pebble;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class CompilerTest extends AbstractTest {

	@Test
	public void testCompile() {
		PebbleTemplate template = pebble.loadTemplate("template.singleVariable.peb");
		Map<String, Object> context = new HashMap<>();
		context.put("test", "TEST");
		assertEquals(template.render(context), "hello TEST");
	}

	@Test
	public void testEscapeCharactersText() {
		PebbleTemplate template = pebble.loadTemplate("template.escapeCharacters.peb");
		Map<String, Object> context = new HashMap<>();
		template.render(context);
	}

}
