/*
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble;

import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.error.PebbleException;
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

public class ArraySyntaxTest {

  @Rule
  public final ExpectedException thrown = ExpectedException.none();

  @Test
  public void testArraySyntax() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ [] }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("[]", writer.toString());
  }

  @Test
  public void testSimpleArray() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ ['first-name'] }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("[first-name]", writer.toString());
  }

  @Test
  public void test2ElementArray() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ ['first-name','last-name'] }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("[first-name, last-name]", writer.toString());
  }

  @Test
  public void test2ElementArray2() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ [ 'first-name' ,   'last-name'    ] }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("[first-name, last-name]", writer.toString());
  }

  @Test
  public void testNElementArray() throws PebbleException, IOException {
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

  @Test
  public void testIncompleteArraySyntax() throws PebbleException {
    //Arrange
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ [,] }}";

    this.thrown.expect(ParserException.class);
    this.thrown.expectMessage(startsWith("Unexpected token"));

    //Act + Assert
    pebble.getTemplate(source);
  }

  @SuppressWarnings("serial")
  @Test
  public void testArrayWithExpressions() throws PebbleException, IOException {
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
  public void testArrayWithComplexExpressions() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ ['one' + 'plus', 2 - 1, three.number, numbers['four'][0], numbers ['five'] .value ] }}";
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
    assertEquals("[oneplus, 1, 3, 4, five]", writer.toString());
  }

  @Test
  public void testSetCommand() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set arr = ['repeated-name',2*5] %}{{ arr }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("[repeated-name, 10]", writer.toString());
  }

  @Test
  public void testSetCommand2() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set arr = ['repeated-name',2*5] %}{{ arr [1] }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("10", writer.toString());
  }

  @Test
  public void testFirstFilter() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set arr = ['name',2*5] %}{{ arr | first }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("name", writer.toString());
  }

  @Test
  public void testFirstFilter2() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ ['name',2*5] | first }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("name", writer.toString());
  }

  @Test
  public void testJoinFilter() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set arr = ['name',2*5] %}{{ arr | join(':') }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("name:10", writer.toString());
  }

  @Test
  public void testJoinFilter2() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ ['name',2*5] | join(':') }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("name:10", writer.toString());
  }

  @Test
  public void testLastFilter() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set arr = ['name',2*5] %}{{ arr | last }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("10", writer.toString());
  }

  @Test
  public void testLastFilter2() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ ['name',2*5] | last }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("10", writer.toString());
  }

  @Test
  public void testSliceFilter() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set arr = ['name',2*5,'three',1.9] %}{{ arr | slice(1,3) }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("[10, three]", writer.toString());
  }

  @Test
  public void testSliceFilter2() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ ['name',2*5,'three',1.9] | slice(1,3) }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("[10, three]", writer.toString());
  }

  @Test
  public void testSortFilter() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set arr = [3,2,1,0] %}{{ arr | sort }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("[0, 1, 2, 3]", writer.toString());
  }

  @Test
  public void testSortFilter2() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ [3,2,1,0] | sort }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("[0, 1, 2, 3]", writer.toString());
  }

  @Test
  public void testForTag() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set names = ['Bob','Maria','John'] %}{% for name in names %}{{ name }}{% endfor %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("BobMariaJohn", writer.toString());
  }

  @Test
  public void testForTag2() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% for name in ['Bob','Maria','John'] %}{{ name }}{% endfor %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("BobMariaJohn", writer.toString());
  }

  @Test
  public void testForTagInvalidIterable() throws PebbleException, IOException {

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
  public void testForElseTag() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% for name in [] %}{{ name }}{% else %}{{ 'no name' }}{% endfor %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("no name", writer.toString());
  }

  @Test
  public void testIfTag() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set names = ['Bob','Maria','John'] %}{% if names is null %}{{ 'it is' }}{% else %}{{ 'it is not' }}{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("it is not", writer.toString());
  }

  @Test
  public void testIfTag2() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if ['Bob','Maria','John'] is null %}{{ 'it is' }}{% else %}{{ 'it is not' }}{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("it is not", writer.toString());
  }

  @Test
  public void testMacroTag() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% macro firstname(names) %}{{ names | first }}{% endmacro %}{{ firstname(['Bob','Maria','John']) }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("Bob", writer.toString());
  }

  @Test
  public void testMacroTagNamedArguments() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% macro firstname(names) %}{{ names | first }}{% endmacro %}{{ firstname(names=['Bob','Maria','John']) }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("Bob", writer.toString());
  }

  @Test
  public void testAdditionOverloading() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set arr = [0,1] + 2 %}{{ arr }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("[0, 1, 2]", writer.toString());
  }

  @Test
  public void testAdditionOverloading2() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set arr = [0,1] + [2,3] %}{{ arr }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("[0, 1, 2, 3]", writer.toString());
  }

  @Test
  public void testAdditionOverloading3() throws PebbleException, IOException {
    //Arrange
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set arr = 1 + [0,1] %}{{ arr }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();

    this.thrown.expect(PebbleException.class);
    this.thrown.expectMessage(startsWith("Could not perform addition"));

    //Act + Assert
    template.evaluate(writer, new HashMap<>());
  }

  @Test
  public void testSubtractionOverloading() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set arr = [0,1,2] - 1 %}{{ arr }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("[0, 2]", writer.toString());
  }

  @Test
  public void testSubtractionOverloading2() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set arr = [0,1,2] - [0,2,3] %}{{ arr }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("[1]", writer.toString());
  }

  @Test
  public void testSubtractionOverloading3() throws PebbleException, IOException {
    //Arrange
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set arr = 1 - [0,2] %}{{ arr }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();

    this.thrown.expect(PebbleException.class);
    this.thrown.expectMessage(startsWith("Could not perform subtraction"));

    //Act + Assert
    template.evaluate(writer, new HashMap<>());
  }

  @Test
  public void testEmptyTest() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set arr = [0,1,2] %}{% if arr is empty %}{{ 'true' }}{% else %}{{ 'false' }}{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("false", writer.toString());
  }

  @Test
  public void testEmptyTest2() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if [0,1,2] is empty %}{{ 'true' }}{% else %}{{ 'false' }}{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("false", writer.toString());
  }

  @Test
  public void testEmptyTest3() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if [] is not empty %}{{ 'true' }}{% else %}{{ 'false' }}{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("false", writer.toString());
  }

  @Test
  public void testIterableTest() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set arr = [0,1,2] %}{% if arr is iterable %}{{ 'true' }}{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("true", writer.toString());
  }

  @Test
  public void testIterableTest2() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if [0,1,2] is iterable %}{{ 'true' }}{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("true", writer.toString());
  }

  @Test
  public void testContainsOperator() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set arr = [0,1,2] %}{% if arr contains 1 %}true{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("true", writer.toString());
  }

  @Test
  public void testContainsOperator2() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if [0,1,2] contains 1 %}true{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("true", writer.toString());
  }

  @Test
  public void testContainsOperator3() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if [0,1,2] contains [1,2] %}true{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("true", writer.toString());
  }

  @Test
  public void testContainsOperator4() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if [0,1,2] contains 10 %}true{% else %}false{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("false", writer.toString());
  }

  @Test
  public void testContainsOperator5() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% if [0,1,2] contains 1 and not ([0,1] contains 0) %}true{% else %}false{% endif %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("false", writer.toString());
  }

  @Test
  public void testNestedArrays() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set arr = [[]] %}{{ arr }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("[[]]", writer.toString());
  }

  @Test
  public void testNestedArrays2() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{% set arr = [[],['test'],[['nested'],['arrays']]] %}{{ arr }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("[[], [test], [[nested], [arrays]]]", writer.toString());
  }

  @Test
  public void testNestedArrays3() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ [[],['test'],[['nested'],['arrays']]] }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<>());
    assertEquals("[[], [test], [[nested], [arrays]]]", writer.toString());
  }

  @Test
  public void testNestedMapInArray() throws PebbleException, IOException {

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
  public void testProblematicSubscriptSyntax() throws PebbleException, IOException {

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
  public void testProblematicSubscriptSyntax2() throws PebbleException, IOException {

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
  public void testProblematicSubscriptSyntax3() throws PebbleException, IOException {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ person ['first-name'] .name }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Map<String, Object> context = new HashMap<>();
    context.put("person", new HashMap<String, Object>() {

      {
        this.put("first-name", new Object() {

          private String name = "Bob";

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
  public void testProblematicSubscriptSyntax4() throws PebbleException, IOException {

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
  public void testProblematicSubscriptSyntax5() throws PebbleException, IOException {

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
