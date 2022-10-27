/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble;

import io.pebbletemplates.pebble.error.ParserException;
import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.extension.TestingExtension;
import io.pebbletemplates.pebble.loader.StringLoader;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MapSyntaxTest {

  @Test
  void testMapSyntax() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ {} }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("{}", writer.toString());
  }

  @Test
  void testSimpleMap() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ {'key':'value'} }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("{key=value}", writer.toString());
  }

  @Test
  void test2ElementMap() throws PebbleException, IOException {
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
  void test2ElementMap2() throws PebbleException, IOException {
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
  void testNElementMap() throws PebbleException, IOException {
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
  void testIncompleteMapSyntax() throws PebbleException {
    assertThrows(ParserException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
          .strictVariables(false).build();

      String source = "{{ {,} }}";

      pebble.getTemplate(source);
    });
  }

  @Test
  void testIncompleteMapSyntax2() throws PebbleException {
    assertThrows(ParserException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
          .strictVariables(false).build();

      String source = "{{ {'key'} }}";

      pebble.getTemplate(source);
    });
  }

  @Test
  void testIncompleteMapSyntax3() throws PebbleException {
    assertThrows(ParserException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
          .strictVariables(false).build();

      String source = "{{ {'key':} }}";

      pebble.getTemplate(source);
    });
  }

  @Test
  void testIncompleteMapSyntax4() throws PebbleException {
    assertThrows(ParserException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
          .strictVariables(false).build();

      String source = "{{ {:'value'} }}";

      pebble.getTemplate(source);
    });
  }

  @Test
  void testIncompleteMapSyntax5() throws PebbleException {
    assertThrows(ParserException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
          .strictVariables(false).build();

      String source = "{{ {'key':'value',} }}";

      pebble.getTemplate(source);
    });
  }

  @SuppressWarnings("serial")
  @Test
  void testMapWithExpressions() throws PebbleException, IOException {
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
  void testMapWithComplexExpressions() throws PebbleException, IOException {
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
  void testSetCommand() throws PebbleException, IOException {
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
  void testForTag() throws PebbleException, IOException {
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
  void testForTag2() throws PebbleException, IOException {
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
  void testForElseTag() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% for name in {} %}{{ name }}{% else %}{{ 'no name' }}{% endfor %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("no name", writer.toString());
  }

  @Test
  void testIfTag() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if {'Bob':'Marley','Maria':'Callas','John':'Cobra'} is null %}{{ 'it is' }}{% else %}{{ 'it is not' }}{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("it is not", writer.toString());
  }

  @Test
  void testMacroTag() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% macro print(name) %}{{ name }}{% endmacro %}{{ print({'Bob':'Marley'}) }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("{Bob=Marley}", writer.toString());
  }

  @Test
  void testMacroTagNamedArguments() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% macro print(name) %}{{ name }}{% endmacro %}{{ print(name={'Bob':'Marley'}) }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("{Bob=Marley}", writer.toString());
  }

  // no operator overloading for maps
  @Test
  void testAdditionOverloading() throws PebbleException, IOException {
    assertThrows(PebbleException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
          .strictVariables(false).build();

      String source = "{% set map = {'Bob':'Marley'} + 1 %}{{ map }}";
      PebbleTemplate template = pebble.getTemplate(source);

      Writer writer = new StringWriter();
      template.evaluate(writer, new HashMap<>());
    });
  }

  @Test
  void testSubtractionOverloading() throws PebbleException, IOException {
    assertThrows(PebbleException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
          .strictVariables(false).build();

      String source = "{% set map = {'Bob':'Marley'} - 1 %}{{ map }}";
      PebbleTemplate template = pebble.getTemplate(source);

      Writer writer = new StringWriter();
      template.evaluate(writer, new HashMap<>());
    });
  }

  @Test
  void testEmptyTest() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if {'John':'Cobra'} is empty %}{{ 'true' }}{% else %}{{ 'false' }}{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("false", writer.toString());
  }

  @Test
  void testEmptyTest2() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if {} is empty %}{{ 'true' }}{% else %}{{ 'false' }}{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("true", writer.toString());
  }

  @Test
  void testIterableTest() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if {} is iterable %}true{% else %}false{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("false", writer.toString());
  }

  @Test
  void testContainsOperator() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if {'Bob':'Marley','Maria':'Callas','John':'Cobra'} contains 'Maria' %}true{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("true", writer.toString());
  }

  @Test
  void testContainsOperator2() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if not ( {'Bob':'Marley','Maria':'Callas','John':'Cobra'} contains 'Freddie') %}true{% else %}false{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("true", writer.toString());
  }

  @Test
  void testContainsOperator3() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if {'Bob':'Marley','Maria':'Callas','John':'Cobra'} contains 'John' and not ({'Freddie':'Mercury'} contains 'Bob') %}true{% else %}false{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("true", writer.toString());
  }

  @Test
  void testNestedMaps() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ { 1 : {}, 2 : { 1 : 1 }, { 3 : 3} : 3, 4 : { 4 : { 4 : 4 } } } }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("{{3=3}=3, 1={}, 2={1=1}, 4={4={4=4}}}", writer.toString());
  }

  @Test
  void testNestedArrayInMap() throws PebbleException, IOException {
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
  void testBraceSyntax() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set var = true %}{{ 'hi' }}{# comment #}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("hi", writer.toString());
  }

}
