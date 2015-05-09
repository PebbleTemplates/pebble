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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class MapSyntaxTest extends AbstractTest {
	
	@Test
	public void testMapSyntax() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{{ {} }}";
		PebbleTemplate template = pebble.getTemplate(source);
		
		Writer writer = new StringWriter();
		template.evaluate(writer, new HashMap<String, Object>());
		assertEquals("{}", writer.toString());
	}
	
	@Test
	public void testSimpleMap() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{{ {'key':'value'} }}";
		PebbleTemplate template = pebble.getTemplate(source);
		
		Writer writer = new StringWriter();
		template.evaluate(writer, new HashMap<String, Object>());
		assertEquals("{key=value}", writer.toString());
	}
	
	@Test
	public void test2ElementMap() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{{ {'key1':'value1','key2':'value2'} }}";
		PebbleTemplate template = pebble.getTemplate(source);
		
		Writer writer = new StringWriter();
		template.evaluate(writer, new HashMap<String, Object>());
		assertEquals("{key1=value1, key2=value2}", writer.toString());
	}
	@Test
	public void test2ElementMap2() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{{ {'key1' :  'value1'   ,    'key2'      :       'value2' } }}";
		PebbleTemplate template = pebble.getTemplate(source);
		
		Writer writer = new StringWriter();
		template.evaluate(writer, new HashMap<String, Object>());
		assertEquals("{key1=value1, key2=value2}", writer.toString());
	}
	@Test
	public void testNElementMap() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{{ {'key1':'value1','key2':'value2','key3':'value3','key4':'value4','key5':'value5'} }}";
		PebbleTemplate template = pebble.getTemplate(source);
		
		Writer writer = new StringWriter();
		template.evaluate(writer, new HashMap<String, Object>());
		assertEquals("{key1=value1, key2=value2, key5=value5, key3=value3, key4=value4}", writer.toString());
	}
	
	@Test(expected=ParserException.class)
	public void testIncompleteMapSyntax() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{{ {,} }}";
		PebbleTemplate template = pebble.getTemplate(source);
		
		Writer writer = new StringWriter();
		template.evaluate(writer, new HashMap<String, Object>());
	}
	@Test(expected=ParserException.class)
	public void testIncompleteMapSyntax2() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{{ {'key'} }}";
		PebbleTemplate template = pebble.getTemplate(source);
		
		Writer writer = new StringWriter();
		template.evaluate(writer, new HashMap<String, Object>());
	}
	@Test(expected=ParserException.class)
	public void testIncompleteMapSyntax3() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{{ {'key':} }}";
		PebbleTemplate template = pebble.getTemplate(source);
		
		Writer writer = new StringWriter();
		template.evaluate(writer, new HashMap<String, Object>());
	}
	@Test(expected=ParserException.class)
	public void testIncompleteMapSyntax4() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{{ {:'value'} }}";
		PebbleTemplate template = pebble.getTemplate(source);
		
		Writer writer = new StringWriter();
		template.evaluate(writer, new HashMap<String, Object>());
	}
	@Test(expected=ParserException.class)
	public void testIncompleteMapSyntax5() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{{ {'key':'value',} }}";
		PebbleTemplate template = pebble.getTemplate(source);
		
		Writer writer = new StringWriter();
		template.evaluate(writer, new HashMap<String, Object>());
	}

	@SuppressWarnings("serial")
	@Test
	public void testMapWithExpressions() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{{ {1:'one', 'two':2, three:'three', numbers['four']:4} }}";
		PebbleTemplate template = pebble.getTemplate(source);

		Map<String, Object> context = new HashMap<>();
		context.put("three", "3");
		context.put("numbers", new HashMap<String, Object>() {
			{
				put("four", "4");
			}
		});

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("{1=one, 3=three, 4=4, two=2}", writer.toString());
	}
	@SuppressWarnings({"serial","unused"})
	@Test
	public void testMapWithComplexExpressions() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{{ {'one' + 'plus':'oneplus', 2 - 1:3, three.number:(2+1), 0:numbers['four'][0], numbers ['five'] .value:'five'} }}";
		PebbleTemplate template = pebble.getTemplate(source);

		Map<String, Object> context = new HashMap<>();
		context.put("three", new Object() { public Integer number = 3; });
		context.put("numbers", new HashMap<String, Object>() {
			{
				put("four", new String[] {"4"});
				put("five", new Object() { private String value = "five"; public String getValue() { return this.value; } });
			}
		});

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("{0=4, 1=3, 3=3, oneplus=oneplus, five=five}", writer.toString());
	}
	
	@Test
	public void testSetCommand() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% set map = {'key'+1:'value'+'1'} %}{{ map }}";
		PebbleTemplate template = pebble.getTemplate(source);
		
		Writer writer = new StringWriter();
		template.evaluate(writer, new HashMap<String, Object>());
		assertEquals("{key1=value1}", writer.toString());
	}
	
	
	// this tests use string 'contains' semantics because entry order can't be trusted
	@Test
	public void testForTag() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% set names = {'Bob':'Marley','Maria':'Callas','John':'Cobra'} %}{% for name in names %}{{ name.key + '-' + name.value }}{% endfor %}";
		PebbleTemplate template = pebble.getTemplate(source);
		
		Writer writer = new StringWriter();
		template.evaluate(writer, new HashMap<String, Object>());
		String result = writer.toString();
		assertTrue(result.indexOf("Bob-Marley") > -1);
		assertTrue(result.indexOf("Maria-Callas") > -1);
		assertTrue(result.indexOf("John-Cobra") > -1);
	}
	@Test
	public void testForTag2() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% for name in {'Bob':'Marley','Maria':'Callas','John':'Cobra'} %}{{ name.key + '-' + name.value }}{% endfor %}";
		PebbleTemplate template = pebble.getTemplate(source);
		
		Writer writer = new StringWriter();
		template.evaluate(writer, new HashMap<String, Object>());
		String result = writer.toString();
		assertTrue(result.indexOf("Bob-Marley") > -1);
		assertTrue(result.indexOf("Maria-Callas") > -1);
		assertTrue(result.indexOf("John-Cobra") > -1);
	}
	@Test
	public void testForElseTag() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% for name in {} %}{{ name }}{% else %}{{ 'no name' }}{% endfor %}";
		PebbleTemplate template = pebble.getTemplate(source);
		
		Writer writer = new StringWriter();
		template.evaluate(writer, new HashMap<String, Object>());
		assertEquals("no name", writer.toString());
	}
	
	@Test
	public void testIfTag() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if {'Bob':'Marley','Maria':'Callas','John':'Cobra'} is null %}{{ 'it is' }}{% else %}{{ 'it is not' }}{% endif %}";
		PebbleTemplate template = pebble.getTemplate(source);
		
		Writer writer = new StringWriter();
		template.evaluate(writer, new HashMap<String, Object>());
		assertEquals("it is not", writer.toString());
	}
	
	@Test
	public void testMacroTag() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% macro print(name) %}{{ name }}{% endmacro %}{{ print({'Bob':'Marley'}) }}";
		PebbleTemplate template = pebble.getTemplate(source);
		
		Writer writer = new StringWriter();
		template.evaluate(writer, new HashMap<String, Object>());
		assertEquals("{Bob=Marley}", writer.toString());
	}
	@Test
	public void testMacroTagNamedArguments() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% macro print(name) %}{{ name }}{% endmacro %}{{ print(name={'Bob':'Marley'}) }}";
		PebbleTemplate template = pebble.getTemplate(source);
		
		Writer writer = new StringWriter();
		template.evaluate(writer, new HashMap<String, Object>());
		assertEquals("{Bob=Marley}", writer.toString());
	}

	// no operator overloading for maps
	@Test(expected=RuntimeException.class)
	public void testAdditionOverloading() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% set map = {'Bob':'Marley'} + 1 %}{{ map }}";
		PebbleTemplate template = pebble.getTemplate(source);
		
		Writer writer = new StringWriter();
		template.evaluate(writer, new HashMap<String, Object>());
	}
	@Test(expected=RuntimeException.class)
	public void testSubtractionOverloading() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% set map = {'Bob':'Marley'} - 1 %}{{ map }}";
		PebbleTemplate template = pebble.getTemplate(source);
		
		Writer writer = new StringWriter();
		template.evaluate(writer, new HashMap<String, Object>());
	}
	
	@Test
	public void testEmptyTest() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if {'John':'Cobra'} is empty %}{{ 'true' }}{% else %}{{ 'false' }}{% endif %}";
		PebbleTemplate template = pebble.getTemplate(source);
		
		Writer writer = new StringWriter();
		template.evaluate(writer, new HashMap<String, Object>());
		assertEquals("false", writer.toString());
	}
	@Test
	public void testEmptyTest2() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if {} is empty %}{{ 'true' }}{% else %}{{ 'false' }}{% endif %}";
		PebbleTemplate template = pebble.getTemplate(source);
		
		Writer writer = new StringWriter();
		template.evaluate(writer, new HashMap<String, Object>());
		assertEquals("true", writer.toString());
	}
	
	@Test
	public void testIterableTest() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if {} is iterable %}true{% else %}false{% endif %}";
		PebbleTemplate template = pebble.getTemplate(source);
		
		Writer writer = new StringWriter();
		template.evaluate(writer, new HashMap<String, Object>());
		assertEquals("false", writer.toString());
	}
	
	@Test
	public void testMapTest() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if {} is map %}true{% else %}false{% endif %}";
		PebbleTemplate template = pebble.getTemplate(source);
		
		Writer writer = new StringWriter();
		template.evaluate(writer, new HashMap<String, Object>());
		assertEquals("true", writer.toString());
	}
	
	@Test
	public void testContainsOperator() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if {'Bob':'Marley','Maria':'Callas','John':'Cobra'} contains 'Maria' %}true{% endif %}";
		PebbleTemplate template = pebble.getTemplate(source);
		
		Writer writer = new StringWriter();
		template.evaluate(writer, new HashMap<String, Object>());
		assertEquals("true", writer.toString());
	}
	@Test
	public void testContainsOperator2() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if not {'Bob':'Marley','Maria':'Callas','John':'Cobra'} contains 'Freddie' %}true{% else %}false{% endif %}";
		PebbleTemplate template = pebble.getTemplate(source);
		
		Writer writer = new StringWriter();
		template.evaluate(writer, new HashMap<String, Object>());
		assertEquals("true", writer.toString());
	}
	@Test
	public void testContainsOperator3() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% if {'Bob':'Marley','Maria':'Callas','John':'Cobra'} contains 'John' and not {'Freddie':'Mercury'} contains 'Bob' %}true{% else %}false{% endif %}";
		PebbleTemplate template = pebble.getTemplate(source);
		
		Writer writer = new StringWriter();
		template.evaluate(writer, new HashMap<String, Object>());
		assertEquals("true", writer.toString());
	}
	
	@Test
	public void testNestedMaps() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{{ { 1 : {}, 2 : { 1 : 1 }, { 3 : 3} : 3, 4 : { 4 : { 4 : 4 } } } }}";
		PebbleTemplate template = pebble.getTemplate(source);
		
		Writer writer = new StringWriter();
		template.evaluate(writer, new HashMap<String, Object>());
		assertEquals("{{3=3}=3, 1={}, 2={1=1}, 4={4={4=4}}}", writer.toString());
	}
	
	@Test
	public void testNestedArrayInMap() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{{ {'array':[]} }}";
		PebbleTemplate template = pebble.getTemplate(source);
		
		Writer writer = new StringWriter();
		template.evaluate(writer, new HashMap<String, Object>());
		assertEquals("{array=[]}", writer.toString());
	}
	
	
	
	
	// brace syntax regression tests
	
	@Test
	public void testBraceSyntax() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);
		pebble.setStrictVariables(false);

		String source = "{% set var = true %}{{ 'hi' }}{# comment #}";
		PebbleTemplate template = pebble.getTemplate(source);

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("hi", writer.toString());
	}

}
