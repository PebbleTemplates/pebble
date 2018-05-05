/*******************************************************************************
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble;

import com.mitchellbosecke.pebble.error.AttributeNotFoundException;
import com.mitchellbosecke.pebble.error.ClassAccessException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.error.RootAttributeNotFoundException;
import com.mitchellbosecke.pebble.extension.DynamicAttributeProvider;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class GetAttributeTest extends AbstractTest {

    @Test
    public void testOneLayerAttributeNesting() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(true).build();

        PebbleTemplate template = pebble.getTemplate("hello {{ object.name }}");
        Map<String, Object> context = new HashMap<>();
        context.put("object", new SimpleObject());

        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("hello Steve", writer.toString());
    }

    @Test
    public void testAttributeCacheHitting() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(true).build();

        PebbleTemplate template = pebble.getTemplate("hello {{ object.name }}{{ object.name }}");
        Map<String, Object> context = new HashMap<>();
        context.put("object", new SimpleObject());

        Writer writer = new StringWriter();
        template.evaluate(writer, context);
    }

    @Test
    public void testMultiLayerAttributeNesting() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(true).build();

        PebbleTemplate template = pebble.getTemplate("hello {{ object.simpleObject2.simpleObject.name }}");
        Map<String, Object> context = new HashMap<>();
        context.put("object", new SimpleObject3());

        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("hello Steve", writer.toString());
    }

    @Test
    public void testHashmapAttribute() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(true).build();

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
    public void testHashmapAttributeWithArgumentOfNull() throws PebbleException, IOException {
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
    public void testMethodAttribute() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(true).build();

        PebbleTemplate template = pebble.getTemplate("hello {{ object.name }}");
        Map<String, Object> context = new HashMap<>();
        context.put("object", new SimpleObject4());

        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("hello Steve", writer.toString());
    }

    /**
     * Make sure we are properly accounting for getting the class object from an Object in all situations:
     *
     * | AllowGetClass | Strict Variables | Access Type | Result  |
     * | ------------- | ---------------- | ----------- | ------- |
     * | true          | false            | property    | allowed |
     * | true          | false            | method      | allowed |
     * | true          | true             | property    | allowed |
     * | true          | true             | method      | allowed |
     * | false         | false            | property    | throw   |
     * | false         | false            | method      | throw   |
     * | false         | true             | property    | throw   |
     * | false         | true             | method      | throw   |
     *
     * @throws PebbleException
     * @throws IOException
     */

    @Test
    public void testAccessingClass_AllowGetClassOn_StrictVariableOff_Property() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
                .allowGetClass(true)
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
    public void testAccessingClass_AllowGetClassOn_StrictVariableOff_Method() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
                .allowGetClass(true)
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
    public void testAccessingClass_AllowGetClassOn_StrictVariableOn_Property() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
                .allowGetClass(true)
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
    public void testAccessingClass_AllowGetClassOn_StrictVariableOn_Method() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
                .allowGetClass(true)
                .strictVariables(true)
                .build();

        PebbleTemplate template = pebble.getTemplate("hello [{{ object.getClass() }}]");
        Map<String, Object> context = new HashMap<>();
        context.put("object", new SimpleObject());

        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("hello [" + SimpleObject.class.toString() + "]", writer.toString());
    }

    @Test(expected = ClassAccessException.class)
    public void testAccessingClass_AllowGetClassOff_StrictVariableOff_Property() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
                .allowGetClass(false)
                .strictVariables(false)
                .build();

        PebbleTemplate template = pebble.getTemplate("hello [{{ object.class }}]");
        Map<String, Object> context = new HashMap<>();
        context.put("object", new SimpleObject());

        Writer writer = new StringWriter();
        template.evaluate(writer, context);
    }

    @Test(expected = ClassAccessException.class)
    public void testAccessingClass_AllowGetClassOff_StrictVariableOff_Method() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
                .allowGetClass(false)
                .strictVariables(false)
                .build();

        PebbleTemplate template = pebble.getTemplate("hello [{{ object.getClass() }}]");
        Map<String, Object> context = new HashMap<>();
        context.put("object", new SimpleObject());

        Writer writer = new StringWriter();
        template.evaluate(writer, context);
    }

    @Test(expected = ClassAccessException.class)
    public void testAccessingClass_AllowGetClassOff_StrictVariableOn_Property() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
                .allowGetClass(false)
                .strictVariables(true)
                .build();

        PebbleTemplate template = pebble.getTemplate("hello [{{ object.class }}]");
        Map<String, Object> context = new HashMap<>();
        context.put("object", new SimpleObject());

        Writer writer = new StringWriter();
        template.evaluate(writer, context);
    }

    @Test(expected = ClassAccessException.class)
    public void testAccessingClass_AllowGetClassOff_StrictVariableOn_Method() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
                .allowGetClass(false)
                .strictVariables(true)
                .build();

        PebbleTemplate template = pebble.getTemplate("hello [{{ object.getClass() }}]");
        Map<String, Object> context = new HashMap<>();
        context.put("object", new SimpleObject());

        Writer writer = new StringWriter();
        template.evaluate(writer, context);
    }

    /**
     * The GetAttribute expression involves caching, we test with different
     * objects to make sure that the caching doesnt have any negative side
     * effects.
     *
     * @throws PebbleException
     * @throws IOException
     */
    @Test
    public void testMethodAttributeWithDifferentObjects() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(true).build();
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
    public void testBeanMethodWithArgument() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(true).build();

        PebbleTemplate template = pebble.getTemplate("hello {{ object.name('Steve') }}");
        Map<String, Object> context = new HashMap<>();
        context.put("object", new BeanWithMethodsThatHaveArguments());

        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("hello Steve", writer.toString());
    }

    @Test
    public void testBeanMethodWithLongArgument() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(true).build();

        PebbleTemplate template = pebble.getTemplate("hello {{ object.number(2) }}");
        Map<String, Object> context = new HashMap<>();
        context.put("object", new BeanWithMethodsThatHaveArguments());

        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("hello 2", writer.toString());
    }

    @Test
    public void testBeanMethodWithOverloadedArgument() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(true).build();

        PebbleTemplate template = pebble.getTemplate("hello {{ object.number(2.0) }}");
        Map<String, Object> context = new HashMap<>();
        context.put("object", new BeanWithMethodsThatHaveArguments());

        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("hello 4.0", writer.toString());
    }

    @Test
    public void testBeanMethodWithTwoArguments() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(true).build();

        PebbleTemplate template = pebble.getTemplate("hello {{ object.multiply(2, 3) }}");
        Map<String, Object> context = new HashMap<>();
        context.put("object", new BeanWithMethodsThatHaveArguments());

        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("hello 6", writer.toString());
    }

    @Test
    public void testGetMethodAttribute() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

        PebbleTemplate template = pebble.getTemplate("hello {{ object.name }}");
        Map<String, Object> context = new HashMap<>();
        context.put("object", new SimpleObject5());

        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("hello Steve", writer.toString());
    }

    @Test
    public void testHasMethodAttribute() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

        PebbleTemplate template = pebble.getTemplate("hello {{ object.name }}");
        Map<String, Object> context = new HashMap<>();
        context.put("object", new SimpleObject9());

        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("hello Steve", writer.toString());
    }

    @Test
    public void testIsMethodAttribute() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

        PebbleTemplate template = pebble.getTemplate("hello {{ object.name }}");
        Map<String, Object> context = new HashMap<>();
        context.put("object", new SimpleObject6());

        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("hello Steve", writer.toString());
    }

    @Test
    public void testComplexNestedAttributes() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

        String source = "hello {{ object.map.SimpleObject2.simpleObject.name }}. My name is {{ object.map.SimpleObject6.name }}.";
        PebbleTemplate template = pebble.getTemplate(source);
        Map<String, Object> context = new HashMap<>();
        context.put("object", new ComplexObject());

        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("hello Steve. My name is Steve.", writer.toString());
    }

    @Test(expected = RootAttributeNotFoundException.class)
    public void testAttributeOfNullObjectWithStrictVariables() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(true).build();

        PebbleTemplate template = pebble.getTemplate("hello {{ object.name }}");

        Writer writer = new StringWriter();
        template.evaluate(writer);
    }

    @Test
    public void testAttributeOfNullObjectWithoutStrictVariables() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

        PebbleTemplate template = pebble.getTemplate("hello {{ object.name }}");

        Map<String, Object> context = new HashMap<>();
        context.put("object", null);
        Writer writer = new StringWriter();
        template.evaluate(writer, context);

        assertEquals("hello ", writer.toString());
    }

    @Test
    public void testNonExistingAttributeWithoutStrictVariables() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

        PebbleTemplate template = pebble.getTemplate("hello {{ object.name }}");
        Map<String, Object> context = new HashMap<>();
        context.put("object", new Object());

        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("hello ", writer.toString());
    }

    @Test(expected = AttributeNotFoundException.class)
    public void testNonExistingAttributeWithStrictVariables() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(true).build();

        PebbleTemplate template = pebble.getTemplate("hello {{ object.name }}");
        Map<String, Object> context = new HashMap<>();
        context.put("object", new Object());

        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("hello ", writer.toString());
    }

    @Test
    public void testNullAttributeWithoutStrictVariables() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

        PebbleTemplate template = pebble.getTemplate("hello {{ object.name }}");
        Map<String, Object> context = new HashMap<>();
        context.put("object", new SimpleObject7());

        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("hello ", writer.toString());

    }

    /**
     * Should behave the same as it does with strictVariables = false.
     *
     * @throws PebbleException
     * @throws IOException
     */
    @Test
    public void testNullAttributeWithStrictVariables() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(true).build();

        PebbleTemplate template = pebble.getTemplate("hello {{ object.name }}");
        Map<String, Object> context = new HashMap<>();
        context.put("object", new SimpleObject7());

        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("hello ", writer.toString());

    }

    @Test()
    public void testPrimitiveAttribute() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

        PebbleTemplate template = pebble.getTemplate("hello {{ object.name }}");
        Map<String, Object> context = new HashMap<>();
        context.put("object", new SimpleObject8());

        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("hello true", writer.toString());
    }

    @Test
    public void testArrayIndexAttribute() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

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
    public void testListIndexAttribute() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

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
     *
     * @throws PebbleException
     * @throws IOException
     */
    @Test(expected = AttributeNotFoundException.class)
    public void testListNonExistingIndexAttributeWithStrictMode() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(true).build();

        PebbleTemplate template = pebble.getTemplate("{{ arr[1] }}");
        Map<String, Object> context = new HashMap<>();
        List<String> data = new ArrayList<>();
        context.put("arr", data);

        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("Two", writer.toString());
    }

    /**
     * Tests retrieving a non-existing index from a list with strict mode off.
     *
     * @throws PebbleException
     * @throws IOException
     */
    @Test
    public void testListNonExistingIndexAttribute() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

        PebbleTemplate template = pebble.getTemplate("{{ arr[1] }}");
        Map<String, Object> context = new HashMap<>();
        List<String> data = new ArrayList<>();
        context.put("arr", data);

        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("", writer.toString());
    }

    @Test()
    public void testInheritedAttribute() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(true).build();

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
    public void testAccessingValueWithSubscriptInLoop() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(true).build();

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
    public void testPrimitiveArgument() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(true).build();

        PebbleTemplate template = pebble.getTemplate("{{ obj.getStringFromLong(1) }} {{ obj.getStringFromLongs(1,2) }}"
                + " {{ obj.getStringFromBoolean(true) }}");

        Map<String, Object> context = new HashMap<>();
        context.put("obj", new PrimitiveArguments());

        Writer writer = new StringWriter();
        template.evaluate(writer, context);

        assertEquals("1 1 2 true", writer.toString());
    }

    @Test
    public void testBeanMethodWithNullArgument() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(true).build();

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
            return String.valueOf(first) + " " + String.valueOf(second);
        }

        public String getStringFromBoolean(boolean bool) {
            return String.valueOf(bool);
        }
    }

    public class DynamicAttributeProviderObject implements DynamicAttributeProvider {

        public String getSurname() {
            return "Doe";
        }

        public String name = "Invalid";
        public String getName() {
            return name;
        }

        @Override
        public boolean canProvideDynamicAttribute(Object attributeName) {

            if("name".equals(attributeName)) {
                return true;
            }
            return false;
        }

        @Override
        public Object getDynamicAttribute(Object attributeName, Object[] argumentValues) {

            if("name".equals(attributeName)) {

                String name = "Steve";
                if(argumentValues != null && argumentValues.length > 0) {
                    name += " " + Arrays.toString(argumentValues);
                }
                return name;
            }

            return "Invalid";
        }
    }

    @Test
    public void testAttributeProviderSimple() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

        PebbleTemplate template = pebble.getTemplate("hello {{ object.name }} {{ object.surname }}{{ object.invalidAttr }}");
        Map<String, Object> context = new HashMap<>();
        context.put("object", new DynamicAttributeProviderObject());

        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("hello Steve Doe", writer.toString());
    }

    @Test
    public void testAttributeProviderEmptyArguments() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(true).build();

        PebbleTemplate template = pebble.getTemplate("hello {{ object.name() }}");
        Map<String, Object> context = new HashMap<>();
        context.put("object", new DynamicAttributeProviderObject());

        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("hello Steve", writer.toString());
    }

    @Test
    public void testAttributeProviderWithArguments() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(true).build();

        PebbleTemplate template = pebble.getTemplate("hello {{ object.name('abc', (40 + 2)) }}");
        Map<String, Object> context = new HashMap<>();
        context.put("object", new DynamicAttributeProviderObject());

        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("hello Steve [abc, 42]", writer.toString());
    }



    @Test
    public void testAttributePrimitiveAccessWithEmptyMap() throws Exception {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

        PebbleTemplate template = pebble.getTemplate(String.format("hello {{ object[1].name }}"));
        Map<String, Object> context = new HashMap<>();
        context.put("object", new HashMap<>());

        Writer writer = new StringWriter();
        template.evaluate(writer, context);

        assertEquals("hello ", writer.toString());
    }

    @Test
    public void testAttributePrimitiveAccessWithInteger() throws Exception {
        String result = this.testAttributePrimitiveAccess(1);

        assertEquals("hello Steve", result);
    }

    private String testAttributePrimitiveAccess(Number value) throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

        PebbleTemplate template = pebble.getTemplate("hello {{ object[key].name }}");
        Map<String, Object> context = new HashMap<>();
        context.put("key", value);
        context.put("object", Collections.singletonMap(value, new SimpleObject()));

        Writer writer = new StringWriter();
        template.evaluate(writer, context);

        return writer.toString();
    }

    @Test
    public void testAttributePrimitiveAccessWithLong() throws Exception {
        String result = this.testAttributePrimitiveAccess(1L);

        assertEquals("hello Steve", result);
    }

    @Test
    public void testAttributePrimitiveAccessWithDouble() throws Exception {
        String result = this.testAttributePrimitiveAccess(1.05D);

        assertEquals("hello Steve", result);
    }

    @Test
    public void testAttributePrimitiveAccessWithFloat() throws Exception {
        String result = this.testAttributePrimitiveAccess(1.05F);

        assertEquals("hello Steve", result);
    }

    @Test
    public void testAttributePrimitiveAccessWithShort() throws Exception {
        String result = this.testAttributePrimitiveAccess((short) 1);

        assertEquals("hello Steve", result);
    }

    @Test
    public void testAttributePrimitiveAccessWithByte() throws Exception {
        String result = this.testAttributePrimitiveAccess((byte) 1);

        assertEquals("hello Steve", result);
    }
}
