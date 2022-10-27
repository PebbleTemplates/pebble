/*
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import io.pebbletemplates.pebble.error.ParserException;
import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.loader.StringLoader;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class ArraySyntaxTest {

  @Test
  void testArraySyntax() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ [] }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("[]", writer.toString());
  }

  @Test
  void testSimpleArray() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ ['first-name'] }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("[first-name]", writer.toString());
  }

  @Test
  void test2ElementArray() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ ['first-name','last-name'] }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("[first-name, last-name]", writer.toString());
  }

  @Test
  void test2ElementArray2() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ [ 'first-name' ,   'last-name'    ] }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("[first-name, last-name]", writer.toString());
  }

  @Test
  void testNElementArray() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ ['repeated-name','repeated-name','repeated-name','repeated-name','repeated-name','repeated-name','repeated-name'] }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals(
        "[repeated-name, repeated-name, repeated-name, repeated-name, repeated-name, repeated-name, repeated-name]",
        writer.toString());
  }

  /**
   * The template engine should thrown an exception when processing
   * a template that contains incomplete array syntax.
   */
  @Test
  void testIncompleteArraySyntax() throws PebbleException {
    // given a Pebble Engine and template text that contains incomplete array syntax
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();
    String source = "{{ [,] }}";
    // A Parser Exception should be thrown with a message indicating that there was an unexpected token
    assertThatExceptionOfType(ParserException.class).isThrownBy(() -> {
          pebble.getTemplate(source);
        }
    ).withMessageStartingWith("Unexpected token");
  }

  @SuppressWarnings("serial")
  @Test
  void testArrayWithExpressions() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ ['one', 2, three, numbers['four']] }}";
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
    assertEquals("[one, 2, 3, 4]", writer.toString());
  }

  @SuppressWarnings({"serial", "unused"})
  @Test
  void testArrayWithComplexExpressions() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ ['one' + 'plus', 2 - 1, three.number, numbers['four'][0], numbers ['five'] .value ] }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Map<String, Object> context = new HashMap<>();
    context.put("three", new Object() {

      public final Integer number = 3;
    });
    context.put("numbers", new HashMap<String, Object>() {

      {
        this.put("four", new String[]{"4"});
        this.put("five", new Object() {

          private final String value = "five";

          public String getValue() {
            return this.value;
          }
        });
      }
    });

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("[oneplus, 1, 3, 4, five]", writer.toString());
  }

  @Test
  void testSetCommand() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set arr = ['repeated-name',2*5] %}{{ arr }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("[repeated-name, 10]", writer.toString());
  }

  @Test
  void testSetCommand2() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set arr = ['repeated-name',2*5] %}{{ arr [1] }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("10", writer.toString());
  }

  @Test
  void testFirstFilter() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set arr = ['name',2*5] %}{{ arr | first }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("name", writer.toString());
  }

  @Test
  void testFirstFilter2() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ ['name',2*5] | first }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("name", writer.toString());
  }

  @Test
  void testJoinFilter() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set arr = ['name',2*5] %}{{ arr | join(':') }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("name:10", writer.toString());
  }

  @Test
  void testJoinFilter2() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ ['name',2*5] | join(':') }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("name:10", writer.toString());
  }

  @Test
  void testLastFilter() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set arr = ['name',2*5] %}{{ arr | last }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("10", writer.toString());
  }

  @Test
  void testLastFilter2() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ ['name',2*5] | last }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("10", writer.toString());
  }

  @Test
  void testSliceFilter() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set arr = ['name',2*5,'three',1.9] %}{{ arr | slice(1,3) }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("[10, three]", writer.toString());
  }

  @Test
  void testSliceFilter2() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ ['name',2*5,'three',1.9] | slice(1,3) }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("[10, three]", writer.toString());
  }

  @Test
  void testSortFilter() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set arr = [3,2,1,0] %}{{ arr | sort }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("[0, 1, 2, 3]", writer.toString());
  }

  @Test
  void testSortFilter2() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ [3,2,1,0] | sort }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("[0, 1, 2, 3]", writer.toString());
  }

  @Test
  void testSortFilterFromArray() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ 'q,g,s,c,w' | split(',') | sort }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("[c, g, q, s, w]", writer.toString());
  }

  @Test
  void testForTag() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set names = ['Bob','Maria','John'] %}{% for name in names %}{{ name }}{% endfor %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("BobMariaJohn", writer.toString());
  }

  @Test
  void testForTag2() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% for name in ['Bob','Maria','John'] %}{{ name }}{% endfor %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("BobMariaJohn", writer.toString());
  }

  @Test
  void testForTagInvalidIterable() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();
    String source = "{% set myVar = 'somevalue' %}{% for myVal in myVar %}{{ myVal }}{% endfor %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    try {
      template.evaluate(writer, new HashMap<>());
      fail("Expected PebbleException");
    } catch (PebbleException e) {
      assertEquals("Not an iterable object. Value = [somevalue] (" + source + ":1)",
          e.getMessage());
    }
  }

  @Test
  void testForElseTag() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% for name in [] %}{{ name }}{% else %}{{ 'no name' }}{% endfor %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("no name", writer.toString());
  }

  @Test
  void testIfTag() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set names = ['Bob','Maria','John'] %}{% if names is null %}{{ 'it is' }}{% else %}{{ 'it is not' }}{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("it is not", writer.toString());
  }

  @Test
  void testIfTag2() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if ['Bob','Maria','John'] is null %}{{ 'it is' }}{% else %}{{ 'it is not' }}{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("it is not", writer.toString());
  }

  @Test
  void testMacroTag() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% macro firstname(names) %}{{ names | first }}{% endmacro %}{{ firstname(['Bob','Maria','John']) }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("Bob", writer.toString());
  }

  @Test
  void testMacroTagNamedArguments() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% macro firstname(names) %}{{ names | first }}{% endmacro %}{{ firstname(names=['Bob','Maria','John']) }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("Bob", writer.toString());
  }

  @Test
  void testAdditionOverloading() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set arr = [0,1] + 2 %}{{ arr }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("[0, 1, 2]", writer.toString());
  }

  @Test
  void testAdditionOverloading2() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set arr = [0,1] + [2,3] %}{{ arr }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("[0, 1, 2, 3]", writer.toString());
  }

  @Test
  void testAdditionOverloading3() throws PebbleException, IOException {
    //Arrange
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set arr = 1 + [0,1] %}{{ arr }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();

    //Act + Assert
    // A Pebble Exception should be thrown with a message indicating that the addition operation failed
    assertThatExceptionOfType(PebbleException.class).isThrownBy(() -> { 
        template.evaluate(writer, new HashMap<>()); 
      }
    ).withMessageStartingWith("Could not perform addition");
  }

  @Test
  void testSubtractionOverloading() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set arr = [0,1,2] - 1 %}{{ arr }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("[0, 2]", writer.toString());
  }

  @Test
  void testSubtractionOverloading2() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set arr = [0,1,2] - [0,2,3] %}{{ arr }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("[1]", writer.toString());
  }

  @Test
  void testSubtractionOverloading3() throws PebbleException, IOException {
    //Arrange
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set arr = 1 - [0,2] %}{{ arr }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();

    //Act + Assert
    // A Pebble Exception should be thrown with a message indicating that subtraction failed
    assertThatExceptionOfType(PebbleException.class).isThrownBy(() -> {
        template.evaluate(writer, new HashMap<>()); 
      }
    ).withMessageStartingWith("Could not perform subtraction");
  }

  @Test
  void testEmptyTest() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set arr = [0,1,2] %}{% if arr is empty %}{{ 'true' }}{% else %}{{ 'false' }}{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("false", writer.toString());
  }

  @Test
  void testEmptyTest2() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if [0,1,2] is empty %}{{ 'true' }}{% else %}{{ 'false' }}{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("false", writer.toString());
  }

  @Test
  void testEmptyTest3() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if [] is not empty %}{{ 'true' }}{% else %}{{ 'false' }}{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("false", writer.toString());
  }

  @Test
  void testIterableTest() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set arr = [0,1,2] %}{% if arr is iterable %}{{ 'true' }}{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("true", writer.toString());
  }

  @Test
  void testIterableTest2() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if [0,1,2] is iterable %}{{ 'true' }}{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("true", writer.toString());
  }

  @Test
  void testContainsOperator() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set arr = [0,1,2] %}{% if arr contains 1 %}true{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("true", writer.toString());
  }

  @Test
  void testContainsOperator2() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if [0,1,2] contains 1 %}true{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("true", writer.toString());
  }

  @Test
  void testContainsOperator3() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if [0,1,2] contains [1,2] %}true{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("true", writer.toString());
  }

  @Test
  void testContainsOperator4() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if [0,1,2] contains 10 %}true{% else %}false{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("false", writer.toString());
  }

  @Test
  void testContainsOperator5() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if [0,1,2] contains 1 and not ([0,1] contains 0) %}true{% else %}false{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("false", writer.toString());
  }

  @Test
  void testNestedArrays() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set arr = [[]] %}{{ arr }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("[[]]", writer.toString());
  }

  @Test
  void testNestedArrays2() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set arr = [[],['test'],[['nested'],['arrays']]] %}{{ arr }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("[[], [test], [[nested], [arrays]]]", writer.toString());
  }

  @Test
  void testNestedArrays3() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ [[],['test'],[['nested'],['arrays']]] }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("[[], [test], [[nested], [arrays]]]", writer.toString());
  }

  @Test
  void testNestedMapInArray() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ [{1:1}] }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("[{1=1}]", writer.toString());
  }

  // subscript syntax regression tests

  @SuppressWarnings("serial")
  @Test
  void testProblematicSubscriptSyntax() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ person ['first-name'] }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Map<String, Object> context = new HashMap<>();
    context.put("person", new HashMap<String, Object>() {

      {
        this.put("first-name", "Bob");
      }
    });

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("Bob", writer.toString());
  }

  @SuppressWarnings("serial")
  @Test
  void testProblematicSubscriptSyntax2() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ person ['first-name'][0] }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Map<String, Object> context = new HashMap<>();
    context.put("person", new HashMap<String, Object>() {

      {
        this.put("first-name", new String[]{"Bob"});
      }
    });

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("Bob", writer.toString());
  }

  @SuppressWarnings({"serial", "unused"})
  @Test
  void testProblematicSubscriptSyntax3() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ person ['first-name'] .name }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Map<String, Object> context = new HashMap<>();
    context.put("person", new HashMap<String, Object>() {

      {
        this.put("first-name", new Object() {

          private final String name = "Bob";

          public String getName() {
            return this.name;
          }
        });
      }
    });

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("Bob", writer.toString());
  }

  @SuppressWarnings("serial")
  @Test
  void testProblematicSubscriptSyntax4() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if person ['first-name'] == 'Bob' %}{{ person ['first-name'] }}{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Map<String, Object> context = new HashMap<>();
    context.put("person", new HashMap<String, Object>() {

      {
        this.put("first-name", "Bob");
      }
    });

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("Bob", writer.toString());
  }

  @SuppressWarnings("serial")
  @Test
  void testProblematicSubscriptSyntax5() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set name = person ['first-name'] %}{{ name }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Map<String, Object> context = new HashMap<>();
    context.put("person", new HashMap<String, Object>() {

      {
        this.put("first-name", "Bob");
      }
    });

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("Bob", writer.toString());
  }

}
