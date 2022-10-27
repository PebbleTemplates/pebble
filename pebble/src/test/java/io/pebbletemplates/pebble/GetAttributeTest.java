/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import io.pebbletemplates.pebble.error.AttributeNotFoundException;
import io.pebbletemplates.pebble.error.ClassAccessException;
import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.error.RootAttributeNotFoundException;
import io.pebbletemplates.pebble.loader.StringLoader;
import io.pebbletemplates.pebble.attributes.methodaccess.NoOpMethodAccessValidator;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class GetAttributeTest {

  @Test
  void testOneLayerAttributeNesting() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(true).build();

    PebbleTemplate template = pebble.getTemplate("hello {{ object.name }}");
    Map<String, Object> context = new HashMap<>();
    context.put("object", new SimpleObject());

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("hello Steve", writer.toString());
  }

  @Test
  void testAttributeCacheHitting() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(true).build();

    PebbleTemplate template = pebble.getTemplate("hello {{ object.name }}{{ object.name }}");
    Map<String, Object> context = new HashMap<>();
    context.put("object", new SimpleObject());

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
  }

  @Test
  void testMultiLayerAttributeNesting() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(true).build();

    PebbleTemplate template = pebble
        .getTemplate("hello {{ object.simpleObject2.simpleObject.name }}");
    Map<String, Object> context = new HashMap<>();
    context.put("object", new SimpleObject3());

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("hello Steve", writer.toString());
  }

  @Test
  void testHashmapAttribute() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(true).build();

    PebbleTemplate template = pebble.getTemplate("hello {{ object.name }}");
    Map<String, Object> context = new HashMap<>();
    Map<String, String> map = new HashMap<>();
    map.put("name", "Steve");
    context.put("object", map);

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("hello Steve", writer.toString());
  }

  @Test
  void testHashmapAttributeWithArgumentOfNull() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).build();
    PebbleTemplate template = pebble.getTemplate("hello {{ object[missingContextProperty] }}");
    Map<String, Object> context = new HashMap<>();
    Map<String, String> map = new HashMap<>();
    map.put("name", "Steve");
    context.put("object", map);

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("hello ", writer.toString());
  }

  @Test
  void testNonExistingHashMapAttributeWithoutStrictVariables()
      throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ object.nonExisting }}";
    PebbleTemplate template = pebble.getTemplate(source);

    Map<String, Object> context = new HashMap<>();
    Map<String, String> map = new HashMap<>();
    map.put("name", "Steve");
    context.put("object", map);

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("", writer.toString());
  }

  @Test
  void testNonExistingMapAttributeWithStrictVariables() throws PebbleException, IOException {
    assertThrows(AttributeNotFoundException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
          .strictVariables(true).build();

      String source = "{{ object.nonExisting }}";
      PebbleTemplate template = pebble.getTemplate(source);

      Map<String, Object> context = new HashMap<>();
      Map<String, String> map = new HashMap<>();
      map.put("name", "Steve");
      context.put("object", map);

      Writer writer = new StringWriter();
      template.evaluate(writer, context);
    });
  }

  @Test
  void testNonExistingMapAttributeWithStrictVariablesAndEmptyMap() throws PebbleException, IOException {
    assertThrows(AttributeNotFoundException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
          .strictVariables(true).build();

      String source = "{{ object.nonExisting }}";
      PebbleTemplate template = pebble.getTemplate(source);

      Map<String, Object> context = new HashMap<>();
      context.put("object", new HashMap<>());

      Writer writer = new StringWriter();
      template.evaluate(writer, context);
    });
  }

  @Test
  void testNullMapValueWithoutStrictVariables() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("hello {{ map.name }}");
    Map<String, Object> context = new HashMap<>();

    Map<String, Object> map = new HashMap<>();
    map.put("name", null);
    context.put("map", map);

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("hello ", writer.toString());
  }

  /**
   * Issue 446
   */
  @Test
  void testNullMapValueWithStrictVariables() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(true).build();

    PebbleTemplate template = pebble.getTemplate("hello {{ map.name }}");
    Map<String, Object> context = new HashMap<>();

    Map<String, Object> map = new HashMap<>();
    map.put("name", null);
    context.put("map", map);

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("hello ", writer.toString());
  }

  @Test
  void testMethodAttribute() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(true).build();

    PebbleTemplate template = pebble.getTemplate("hello {{ object.name }}");
    Map<String, Object> context = new HashMap<>();
    context.put("object", new SimpleObject4());

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("hello Steve", writer.toString());
  }

  /**
   * Make sure we are properly accounting for getting the class object from an Object in all
   * situations:
   *
   * | AllowUnsafeMethods | Strict Variables | Access Type | Result  |
   * | ------------------ | ---------------- | ----------- | ------- |
   * | true               | false            | property    | allowed |
   * | true               | false            | method      | allowed |
   * | true               | true             | property    | allowed |
   * | true               | true             | method      | allowed |
   * | false              | false            | property    | throw   |
   * | false              | false            | method      | throw   |
   * | false              | true             | property    | throw   |
   * | false              | true             | method      | throw   |
   */
  @Test
  void testAccessingClass_AllowUnsafeMethodsOn_StrictVariableOff_Property()
      throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .methodAccessValidator(new NoOpMethodAccessValidator())
        .strictVariables(false)
        .build();

    PebbleTemplate template = pebble.getTemplate("hello [{{ object.class }}]");
    Map<String, Object> context = new HashMap<>();
    context.put("object", new SimpleObject());

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("hello [" + SimpleObject.class.toString() + "]", writer.toString());
  }

  @Test
  void testAccessingClass_AllowUnsafeMethodsOn_StrictVariableOff_Method()
      throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .methodAccessValidator(new NoOpMethodAccessValidator())
        .strictVariables(false)
        .build();

    PebbleTemplate template = pebble.getTemplate("hello [{{ object.getClass() }}]");
    Map<String, Object> context = new HashMap<>();
    context.put("object", new SimpleObject());

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("hello [" + SimpleObject.class.toString() + "]", writer.toString());
  }

  @Test
  void testAccessingClass_AllowUnsafeMethodsOn_StrictVariableOn_Property()
      throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .methodAccessValidator(new NoOpMethodAccessValidator())
        .strictVariables(true)
        .build();

    PebbleTemplate template = pebble.getTemplate("hello [{{ object.class }}]");
    Map<String, Object> context = new HashMap<>();
    context.put("object", new SimpleObject());

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("hello [" + SimpleObject.class.toString() + "]", writer.toString());
  }

  @Test
  void testAccessingClass_AllowUnsafeMethodsOn_StrictVariableOn_Method()
      throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .methodAccessValidator(new NoOpMethodAccessValidator())
        .strictVariables(true)
        .build();

    PebbleTemplate template = pebble.getTemplate("hello [{{ object.getClass() }}]");
    Map<String, Object> context = new HashMap<>();
    context.put("object", new SimpleObject());

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("hello [" + SimpleObject.class.toString() + "]", writer.toString());
  }

  @Test
  void testAccessingClass_AllowUnsafeMethodsOff_StrictVariableOff_Property()
      throws PebbleException, IOException {
    assertThrows(ClassAccessException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
          .strictVariables(false)
          .build();

      PebbleTemplate template = pebble.getTemplate("hello [{{ object.class }}]");
      Map<String, Object> context = new HashMap<>();
      context.put("object", new SimpleObject());

      Writer writer = new StringWriter();
      template.evaluate(writer, context);
    });
  }

  @Test
  void testAccessingClass_AllowUnsafeMethodsOff_StrictVariableOff_Method()
      throws PebbleException, IOException {
    assertThrows(ClassAccessException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
          .strictVariables(false)
          .build();

      PebbleTemplate template = pebble.getTemplate("hello [{{ object.getClass() }}]");
      Map<String, Object> context = new HashMap<>();
      context.put("object", new SimpleObject());

      Writer writer = new StringWriter();
      template.evaluate(writer, context);
    });
  }

  @Test
  void testAccessingClass_AllowUnsafeMethodsOff_StrictVariableOn_Property()
      throws PebbleException, IOException {
    assertThrows(ClassAccessException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
          .strictVariables(true)
          .build();

      PebbleTemplate template = pebble.getTemplate("hello [{{ object.class }}]");
      Map<String, Object> context = new HashMap<>();
      context.put("object", new SimpleObject());

      Writer writer = new StringWriter();
      template.evaluate(writer, context);
    });
  }

  @Test
  void testAccessingClass_AllowUnsafeMethodsOff_StrictVariableOn_Method()
      throws PebbleException, IOException {
    assertThrows(ClassAccessException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
          .strictVariables(true)
          .build();

      PebbleTemplate template = pebble.getTemplate("hello [{{ object.getClass() }}]");
      Map<String, Object> context = new HashMap<>();
      context.put("object", new SimpleObject());

      Writer writer = new StringWriter();
      template.evaluate(writer, context);
    });
  }

  @Test
  void testAccessingClass_AllowUnsafeMethodsOnIsCaseInsensitive_Property()
      throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder()
        .loader(new StringLoader())
        .methodAccessValidator(new NoOpMethodAccessValidator())
        .build();

    PebbleTemplate template = pebble.getTemplate("hello [{{ object.ClAsS }}]");
    Map<String, Object> context = new HashMap<>();
    context.put("object", new SimpleObject());

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("hello [" + SimpleObject.class.toString() + "]", writer.toString());
  }

  @Test
  void testAccessingClass_AllowUnsafeMethodsOffIsCaseInsensitive_Property()
      throws PebbleException, IOException {
    assertThrows(ClassAccessException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder()
          .loader(new StringLoader())
          .build();

      PebbleTemplate template = pebble.getTemplate("hello [{{ object.ClAsS }}]");
      Map<String, Object> context = new HashMap<>();
      context.put("object", new SimpleObject());

      Writer writer = new StringWriter();
      template.evaluate(writer, context);
    });
  }

  @Test
  void testAccessingClass_AllowUnsafeMethodsOnIsCaseInsensitive_Method()
      throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder()
        .loader(new StringLoader())
        .methodAccessValidator(new NoOpMethodAccessValidator())
        .build();

    PebbleTemplate template = pebble.getTemplate("hello [{{ object.GeTcLAsS() }}]");
    Map<String, Object> context = new HashMap<>();
    context.put("object", new SimpleObject());

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("hello [" + SimpleObject.class.toString() + "]", writer.toString());
  }

  @Test
  void testAccessingClass_AllowUnsafeMethodsOffIsCaseInsensitive_Method()
      throws PebbleException, IOException {
    assertThrows(ClassAccessException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder()
          .loader(new StringLoader())
          .build();

      PebbleTemplate template = pebble.getTemplate("hello [{{ object.GeTcLAsS() }}]");
      Map<String, Object> context = new HashMap<>();
      context.put("object", new SimpleObject());

      Writer writer = new StringWriter();
      template.evaluate(writer, context);
    });
  }

  @Test
  void testAccessingClass_AllowUnsafeMethodsOffForMethodNotify_thenThrowException()
      throws PebbleException, IOException {
    assertThrows(ClassAccessException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder()
          .loader(new StringLoader())
          .build();

      PebbleTemplate template = pebble.getTemplate("hello [{{ object.notify() }}]");
      Map<String, Object> context = new HashMap<>();
      context.put("object", new SimpleObject());

      Writer writer = new StringWriter();
      template.evaluate(writer, context);
    });
  }

  /**
   * The GetAttribute expression involves caching, we test with different objects to make sure that
   * the caching doesnt have any negative side effects.
   */
  @Test
  void testMethodAttributeWithDifferentObjects() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(true).build();
    PebbleTemplate template = pebble.getTemplate("hello {{ object.name }}");

    Map<String, Object> context1 = new HashMap<>();
    context1.put("object", new CustomizableObject("Alex"));
    Writer writer1 = new StringWriter();
    template.evaluate(writer1, context1);
    assertEquals("hello Alex", writer1.toString());

    Map<String, Object> context2 = new HashMap<>();
    context2.put("object", new CustomizableObject("Steve"));
    Writer writer2 = new StringWriter();
    template.evaluate(writer2, context2);
    assertEquals("hello Steve", writer2.toString());
  }

  @Test
  void testBeanMethodWithArgument() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(true).build();

    PebbleTemplate template = pebble.getTemplate("hello {{ object.name('Steve') }}");
    Map<String, Object> context = new HashMap<>();
    context.put("object", new BeanWithMethodsThatHaveArguments());

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("hello Steve", writer.toString());
  }

  @Test
  void testBeanMethodWithLongArgument() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(true).build();

    PebbleTemplate template = pebble.getTemplate("hello {{ object.number(2) }}");
    Map<String, Object> context = new HashMap<>();
    context.put("object", new BeanWithMethodsThatHaveArguments());

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("hello 2", writer.toString());
  }

  @Test
  void testBeanMethodWithLongArgument2() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(true).build();

    PebbleTemplate template = pebble.getTemplate("hello {{ object.number(2L) }}");
    Map<String, Object> context = new HashMap<>();
    context.put("object", new BeanWithMethodsThatHaveArguments());

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("hello 2", writer.toString());
  }

  @Test
  void testBeanMethodWithTreatLiteralDecimalAsLong() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(true).literalDecimalTreatedAsInteger(false)
        .greedyMatchMethod(false).build();

    PebbleTemplate template = pebble.getTemplate("hello {{ object.integer(2) }}");
    Map<String, Object> context = new HashMap<>();
    context.put("object", new BeanWithMethodsThatHaveArguments());

    try {
      Writer writer = new StringWriter();
      template.evaluate(writer, context);
      fail("expected PebbleException");
    } catch (PebbleException e) {
      assertEquals(e.getLineNumber(), (Integer) 1);
      assertEquals(e.getClass(), AttributeNotFoundException.class);
    }
  }

  @Test
  void testBeanMethodWithTreatNumberAsInteger() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(true).literalDecimalTreatedAsInteger(true)
        .build();

    PebbleTemplate template = pebble.getTemplate("hello {{ object.integer(2) }}");
    Map<String, Object> context = new HashMap<>();
    context.put("object", new BeanWithMethodsThatHaveArguments());

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("hello 2", writer.toString());
  }

  @Test
  void testBeanMethodWithGreedyMatchArgument() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(true).greedyMatchMethod(true).build();

    PebbleTemplate template = pebble
        .getTemplate("hello {{ object.integer(2) }} {{ object.short(2) }}");
    Map<String, Object> context = new HashMap<>();
    context.put("object", new BeanWithMethodsThatHaveArguments());

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("hello 2 2", writer.toString());
  }

  @Test
  void testBeanMethodWithNumberLiteralsAsBigDecimals() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
            .strictVariables(true).literalNumbersAsBigDecimals(true)
            .build();

    PebbleTemplate template = pebble.getTemplate("hello {{ 1234567890123456789012345678901234567890 }}");
    Map<String, Object> context = new HashMap<>();

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("hello 1234567890123456789012345678901234567890", writer.toString());
  }

  @Test
  void testBeanMethodWithoutNumberLiteralsAsBigDecimals() throws PebbleException, IOException {
    assertThrows(NumberFormatException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
              .strictVariables(true).literalNumbersAsBigDecimals(false)
              .build();

      PebbleTemplate template = pebble.getTemplate("hello {{ 1234567890123456789012345678901234567890 }}");
      Map<String, Object> context = new HashMap<>();

      Writer writer = new StringWriter();
      template.evaluate(writer, context);
    });
  }

  @Test
  void testBeanMethodWithOverloadedArgument() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(true).build();

    PebbleTemplate template = pebble.getTemplate("hello {{ object.number(2.0) }}");
    Map<String, Object> context = new HashMap<>();
    context.put("object", new BeanWithMethodsThatHaveArguments());

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("hello 4.0", writer.toString());
  }

  @Test
  void testBeanMethodWithTwoArguments() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(true).build();

    PebbleTemplate template = pebble.getTemplate("hello {{ object.multiply(2, 3) }}");
    Map<String, Object> context = new HashMap<>();
    context.put("object", new BeanWithMethodsThatHaveArguments());

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("hello 6", writer.toString());
  }

  @Test
  void testGetMethodAttribute() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("hello {{ object.name }}");
    Map<String, Object> context = new HashMap<>();
    context.put("object", new SimpleObject5());

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("hello Steve", writer.toString());
  }

  @Test
  void testHasMethodAttribute() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("hello {{ object.name }}");
    Map<String, Object> context = new HashMap<>();
    context.put("object", new SimpleObject9());

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("hello Steve", writer.toString());
  }

  @Test
  void testIsMethodAttribute() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("hello {{ object.name }}");
    Map<String, Object> context = new HashMap<>();
    context.put("object", new SimpleObject6());

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("hello Steve", writer.toString());
  }

  @Test
  void testComplexNestedAttributes() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "hello {{ object.map.SimpleObject2.simpleObject.name }}. My name is {{ object.map.SimpleObject6.name }}.";
    PebbleTemplate template = pebble.getTemplate(source);
    Map<String, Object> context = new HashMap<>();
    context.put("object", new ComplexObject());

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("hello Steve. My name is Steve.", writer.toString());
  }

  @Test
  void testAttributeOfNullObjectWithStrictVariables() throws PebbleException, IOException {
    assertThrows(RootAttributeNotFoundException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
          .strictVariables(true).build();

      PebbleTemplate template = pebble.getTemplate("hello {{ object.name }}");

      Writer writer = new StringWriter();
      template.evaluate(writer);
    });
  }

  @Test
  void testAttributeOfNullObjectWithoutStrictVariables()
      throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("hello {{ object.name }}");

    Map<String, Object> context = new HashMap<>();
    context.put("object", null);
    Writer writer = new StringWriter();
    template.evaluate(writer, context);

    assertEquals("hello ", writer.toString());
  }

  @Test
  void testNonExistingAttributeWithoutStrictVariables() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("hello {{ object.name }}");
    Map<String, Object> context = new HashMap<>();
    context.put("object", new Object());

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("hello ", writer.toString());
  }

  @Test
  void testNonExistingAttributeWithStrictVariables() throws PebbleException, IOException {
    assertThrows(AttributeNotFoundException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
          .strictVariables(true).build();

      PebbleTemplate template = pebble.getTemplate("hello {{ object.name }}");
      Map<String, Object> context = new HashMap<>();
      context.put("object", new Object());

      Writer writer = new StringWriter();
      template.evaluate(writer, context);
      assertEquals("hello ", writer.toString());
    });
  }

  @Test
  void testNullAttributeWithoutStrictVariables() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("hello {{ object.name }}");
    Map<String, Object> context = new HashMap<>();
    context.put("object", new SimpleObject7());

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("hello ", writer.toString());

  }

  /**
   * Should behave the same as it does with strictVariables = false.
   */
  @Test
  void testNullAttributeWithStrictVariables() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(true).build();

    PebbleTemplate template = pebble.getTemplate("hello {{ object.name }}");
    Map<String, Object> context = new HashMap<>();
    context.put("object", new SimpleObject7());

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("hello ", writer.toString());

  }

  @Test()
  void testPrimitiveAttribute() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("hello {{ object.name }}");
    Map<String, Object> context = new HashMap<>();
    context.put("object", new SimpleObject8());

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("hello true", writer.toString());
  }

  @Test
  void testArrayIndexAttribute() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ arr[2] }}");
    Map<String, Object> context = new HashMap<>();
    String[] data = new String[3];
    data[0] = "Zero";
    data[1] = "One";
    data[2] = "Two";
    context.put("arr", data);

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("Two", writer.toString());
  }

  @Test
  void testListIndexAttribute() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ arr[2] }}");
    Map<String, Object> context = new HashMap<>();
    List<String> data = new ArrayList<>();
    data.add("Zero");
    data.add("One");
    data.add("Two");
    context.put("arr", data);

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("Two", writer.toString());
  }

  /**
   * Tests retrieving a non-existing index from a list with strict mode on.
   */
  @Test
  void testListNonExistingIndexAttributeWithStrictMode()
      throws PebbleException, IOException {
    assertThrows(AttributeNotFoundException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
          .strictVariables(true).build();

      PebbleTemplate template = pebble.getTemplate("{{ arr[1] }}");
      Map<String, Object> context = new HashMap<>();
      List<String> data = new ArrayList<>();
      context.put("arr", data);

      Writer writer = new StringWriter();
      template.evaluate(writer, context);
      assertEquals("Two", writer.toString());
    });
  }

  /**
   * Tests retrieving a non-existing index from a list with strict mode off.
   */
  @Test
  void testListNonExistingIndexAttribute() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ arr[1] }}");
    Map<String, Object> context = new HashMap<>();
    List<String> data = new ArrayList<>();
    context.put("arr", data);

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("", writer.toString());
  }

  @Test()
  void testInheritedAttribute() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(true).build();

    PebbleTemplate template = pebble.getTemplate("hello {{ object.name }}");
    Map<String, Object> context = new HashMap<>();
    context.put("object", new ChildObject());

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("hello parent", writer.toString());
  }


  public class Person {

    public final String name = "Name";
    public final String surname = "Surname";
  }

  @Test
  void testAccessingValueWithSubscriptInLoop() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(true).build();

    String source = "{% for attribute in ['name', 'surname']%}{{ person[attribute] }}{% endfor %}";
    PebbleTemplate template = pebble.getTemplate(source);

    Map<String, Object> context = new HashMap<>();
    context.put("person", new Person());

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("NameSurname", writer.toString());
  }

  public class SimpleObject {

    public final String name = "Steve";
  }

  public class SimpleObject2 {

    public final SimpleObject simpleObject = new SimpleObject();
  }

  public class SimpleObject3 {

    public final SimpleObject2 simpleObject2 = new SimpleObject2();
  }

  public class SimpleObject4 {

    public String name() {
      return "Steve";
    }
  }

  public class SimpleObject5 {

    public String getName() {
      return "Steve";
    }
  }

  public class SimpleObject6 {

    public String isName() {
      return "Steve";
    }
  }

  public class SimpleObject7 {

    public String name = null;
  }

  public class SimpleObject8 {

    public boolean name = true;
  }

  public class SimpleObject9 {

    public String hasName() {
      return "Steve";
    }
  }

  public class BeanWithMethodsThatHaveArguments {

    public String getName(String name) {
      return name;
    }

    public Double getNumber(Double number) {
      return number * 2;
    }

    public Long getNumber(Long number) {
      return number;
    }

    public Integer getInteger(Integer number) {
      return number;
    }

    public Short getShort(short number) {
      return number;
    }

    public Long multiply(Long one, Long two) {
      return one * two;
    }
  }

  public class ComplexObject {

    public final Map<String, Object> map = new HashMap<>();

    {
      this.map.put("SimpleObject2", new SimpleObject2());
      this.map.put("SimpleObject6", new SimpleObject6());
    }
  }

  public class CustomizableObject {

    private final String name;

    public CustomizableObject(String name) {
      this.name = name;
    }

    public String getName() {
      return this.name;
    }
  }

  public class ChildObject extends ParentObject {

  }

  public class ParentObject {

    public String getName() {
      return "parent";
    }
  }

  @Test()
  void testPrimitiveArgument() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(true).build();

    PebbleTemplate template = pebble
        .getTemplate("{{ obj.getStringFromLong(1) }} {{ obj.getStringFromLongs(1,2) }}"
            + " {{ obj.getStringFromBoolean(true) }}");

    Map<String, Object> context = new HashMap<>();
    context.put("obj", new PrimitiveArguments());

    Writer writer = new StringWriter();
    template.evaluate(writer, context);

    assertEquals("1 1 2 true", writer.toString());
  }

  @Test
  void testBeanMethodWithNullArgument() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(true).build();

    PebbleTemplate template = pebble.getTemplate("hello {{ object.name(var) }}");
    Map<String, Object> context = new HashMap<>();
    context.put("object", new BeanWithMethodsThatHaveArguments());
    context.put("var", null);

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("hello ", writer.toString());
  }

  public class PrimitiveArguments {

    public String getStringFromLong(long id) {
      return String.valueOf(id);
    }

    public String getStringFromLongs(Long first, long second) {
      return first + " " + second;
    }

    public String getStringFromBoolean(boolean bool) {
      return String.valueOf(bool);
    }
  }

  @Test
  void testAttributePrimitiveAccessWithEmptyMap() throws Exception {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate(String.format("hello {{ object[1].name }}"));
    Map<String, Object> context = new HashMap<>();
    context.put("object", new HashMap<>());

    Writer writer = new StringWriter();
    template.evaluate(writer, context);

    assertEquals("hello ", writer.toString());
  }

  @Test
  void testAttributePrimitiveAccessWithInteger() throws Exception {
    String result = this.testAttributePrimitiveAccess(1);

    assertEquals("hello Steve", result);
  }

  private String testAttributePrimitiveAccess(Number value) throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("hello {{ object[key].name }}");
    Map<String, Object> context = new HashMap<>();
    context.put("key", value);
    context.put("object", Collections.singletonMap(value, new SimpleObject()));

    Writer writer = new StringWriter();
    template.evaluate(writer, context);

    return writer.toString();
  }

  @Test
  void testAttributePrimitiveAccessWithLong() throws Exception {
    String result = this.testAttributePrimitiveAccess(1L);

    assertEquals("hello Steve", result);
  }

  @Test
  void testAttributePrimitiveAccessWithDouble() throws Exception {
    String result = this.testAttributePrimitiveAccess(1.05D);

    assertEquals("hello Steve", result);
  }

  @Test
  void testAttributePrimitiveAccessWithFloat() throws Exception {
    String result = this.testAttributePrimitiveAccess(1.05F);

    assertEquals("hello Steve", result);
  }

  @Test
  void testAttributePrimitiveAccessWithShort() throws Exception {
    String result = this.testAttributePrimitiveAccess((short) 1);

    assertEquals("hello Steve", result);
  }

  @Test
  void testAttributePrimitiveAccessWithByte() throws Exception {
    String result = this.testAttributePrimitiveAccess((byte) 1);

    assertEquals("hello Steve", result);
  }
}
