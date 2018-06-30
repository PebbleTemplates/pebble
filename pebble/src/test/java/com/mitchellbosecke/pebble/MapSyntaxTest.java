/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.TestingExtension;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class MapSyntaxTest {

  @Rule
  public final ExpectedException thrown = ExpectedException.none();

  @Test
  public void testMapSyntax() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ {} }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("{}", writer.toString());
  }

  @Test
  public void testSimpleMap() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ {'key':'value'} }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("{key=value}", writer.toString());
  }

  @Test
  public void test2ElementMap() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .extension(new TestingExtension())
        .strictVariables(false).build();

    String source = "{{ {'key1':'value1','key2':'value2'} | mapToString }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("{key1=value1, key2=value2}", writer.toString());
  }

  @Test
  public void test2ElementMap2() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .extension(new TestingExtension())
        .strictVariables(false).build();

    String source = "{{ {'key1' :  'value1'   ,    'key2'      :       'value2' } | mapToString }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("{key1=value1, key2=value2}", writer.toString());
  }

  @Test
  public void testNElementMap() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .extension(new TestingExtension())
        .strictVariables(false).build();

    String source = "{{ {'key1':'value1','key2':'value2','key3':'value3','key4':'value4','key5':'value5'} | mapToString }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("{key1=value1, key2=value2, key3=value3, key4=value4, key5=value5}",
        writer.toString());
  }

  @Test
  public void testIncompleteMapSyntax() throws PebbleException {
    //Arrange
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ {,} }}";

    this.thrown.expect(ParserException.class);

    //Act + Assert
    pebble.getTemplate(source);
  }

  @Test
  public void testIncompleteMapSyntax2() throws PebbleException {
    //Arrange
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ {'key'} }}";

    this.thrown.expect(ParserException.class);

    //Act + Assert
    pebble.getTemplate(source);
  }

  @Test
  public void testIncompleteMapSyntax3() throws PebbleException {
    //Arrange
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ {'key':} }}";

    this.thrown.expect(ParserException.class);

    //Act + Assert
    pebble.getTemplate(source);
  }

  @Test
  public void testIncompleteMapSyntax4() throws PebbleException {
    //Arrange
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ {:'value'} }}";

    this.thrown.expect(ParserException.class);

    //Act + Assert
    pebble.getTemplate(source);
  }

  @Test
  public void testIncompleteMapSyntax5() throws PebbleException {
    //Arrange
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ {'key':'value',} }}";

    this.thrown.expect(ParserException.class);

    //Act + Assert
    pebble.getTemplate(source);
  }

  @SuppressWarnings("serial")
  @Test
  public void testMapWithExpressions() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .extension(new TestingExtension())
        .strictVariables(false).build();

    String source = "{{ {1:'one', 'two':2, three:'three', numbers['four']:4} | mapToString }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Map<String, Object> context = new HashMap<>();
    context.put("three", "3");
    context.put("numbers", new HashMap<String, Object>() {

      {
        this.put("four", "4");
      }
    });

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("{1=one, 3=three, 4=4, two=2}", writer.toString());
  }

  @SuppressWarnings({"serial", "unused"})
  @Test
  public void testMapWithComplexExpressions() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .extension(new TestingExtension())
        .strictVariables(false).build();

    String source = "{{ {'one' + 'plus':'oneplus', 2 - 1:3, three.number:(2+1), 0:numbers['four'][0], numbers ['five'] .value:'five'} | mapToString }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Map<String, Object> context = new HashMap<>();
    context.put("three", new Object() {

      public Integer number = 3;
    });
    context.put("numbers", new HashMap<String, Object>() {

      {
        this.put("four", new String[]{"4"});
        this.put("five", new Object() {

          private String value = "five";

          public String getValue() {
            return this.value;
          }
        });
      }
    });

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("{0=4, 1=3, 3=3, five=five, oneplus=oneplus}", writer.toString());
  }

  @Test
  public void testSetCommand() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set map = {'key'+1:'value'+'1'} %}{{ map }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("{key1=value1}", writer.toString());
  }

  // this tests use string 'contains' semantics because entry order can't be
  // trusted
  @Test
  public void testForTag() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set names = {'Bob':'Marley','Maria':'Callas','John':'Cobra'} %}{% for name in names %}{{ name.key + '-' + name.value }}{% endfor %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    String result = writer.toString();
    assertTrue(result.indexOf("Bob-Marley") > -1);
    assertTrue(result.indexOf("Maria-Callas") > -1);
    assertTrue(result.indexOf("John-Cobra") > -1);
  }

  @Test
  public void testForTag2() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% for name in {'Bob':'Marley','Maria':'Callas','John':'Cobra'} %}{{ name.key + '-' + name.value }}{% endfor %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    String result = writer.toString();
    assertTrue(result.indexOf("Bob-Marley") > -1);
    assertTrue(result.indexOf("Maria-Callas") > -1);
    assertTrue(result.indexOf("John-Cobra") > -1);
  }

  @Test
  public void testForElseTag() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% for name in {} %}{{ name }}{% else %}{{ 'no name' }}{% endfor %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("no name", writer.toString());
  }

  @Test
  public void testIfTag() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if {'Bob':'Marley','Maria':'Callas','John':'Cobra'} is null %}{{ 'it is' }}{% else %}{{ 'it is not' }}{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("it is not", writer.toString());
  }

  @Test
  public void testMacroTag() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% macro print(name) %}{{ name }}{% endmacro %}{{ print({'Bob':'Marley'}) }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("{Bob=Marley}", writer.toString());
  }

  @Test
  public void testMacroTagNamedArguments() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% macro print(name) %}{{ name }}{% endmacro %}{{ print(name={'Bob':'Marley'}) }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("{Bob=Marley}", writer.toString());
  }

  // no operator overloading for maps
  @Test(expected = PebbleException.class)
  public void testAdditionOverloading() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set map = {'Bob':'Marley'} + 1 %}{{ map }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
  }

  @Test(expected = PebbleException.class)
  public void testSubtractionOverloading() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set map = {'Bob':'Marley'} - 1 %}{{ map }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
  }

  @Test
  public void testEmptyTest() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if {'John':'Cobra'} is empty %}{{ 'true' }}{% else %}{{ 'false' }}{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("false", writer.toString());
  }

  @Test
  public void testEmptyTest2() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if {} is empty %}{{ 'true' }}{% else %}{{ 'false' }}{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("true", writer.toString());
  }

  @Test
  public void testIterableTest() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if {} is iterable %}true{% else %}false{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("false", writer.toString());
  }

  @Test
  public void testContainsOperator() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if {'Bob':'Marley','Maria':'Callas','John':'Cobra'} contains 'Maria' %}true{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("true", writer.toString());
  }

  @Test
  public void testContainsOperator2() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if not ( {'Bob':'Marley','Maria':'Callas','John':'Cobra'} contains 'Freddie') %}true{% else %}false{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("true", writer.toString());
  }

  @Test
  public void testContainsOperator3() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if {'Bob':'Marley','Maria':'Callas','John':'Cobra'} contains 'John' and not ({'Freddie':'Mercury'} contains 'Bob') %}true{% else %}false{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("true", writer.toString());
  }

  @Test
  public void testNestedMaps() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ { 1 : {}, 2 : { 1 : 1 }, { 3 : 3} : 3, 4 : { 4 : { 4 : 4 } } } }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("{{3=3}=3, 1={}, 2={1=1}, 4={4={4=4}}}", writer.toString());
  }

  @Test
  public void testNestedArrayInMap() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ {'array':[]} }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("{array=[]}", writer.toString());
  }

  // brace syntax regression tests

  @Test
  public void testBraceSyntax() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set var = true %}{{ 'hi' }}{# comment #}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("hi", writer.toString());
  }

}
