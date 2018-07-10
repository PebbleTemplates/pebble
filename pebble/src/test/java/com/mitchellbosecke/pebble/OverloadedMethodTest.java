/*******************************************************************************
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble;

import static org.junit.Assert.assertEquals;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

// These tests verify correct behavior when resolving overloaded methods in a model class. See issue #367
public class OverloadedMethodTest {

// Verify that an overloaded method will select the correct version of the method to call based on the input type
//----------------------------------------------------------------------------------------------------------------------

    public static class Model {

        public String testMethod(String input) {
            return "string input: " + input;
        }

        public String testMethod(Integer input) {
            return "Integer input: " + input;
        }

        public String testMethod(Long input) {
            return "Long input: " + input;
        }

        public String testMethod(Object input) {
            return "other input: " + input.getClass();
        }

        public String testMethod2(String input1, String input2) {
            return "string-string inputs";
        }

        public String testMethod2(Object input1, String input2) {
            return "object-string inputs";
        }

        public String testMethod2(String input1, Object input2) {
            return "string-object inputs";
        }

        public String testMethod2(Object input1, Object input2) {
            return "object-object inputs";
        }

    }

    @Test
    public void testWithLiteralString() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder()
                .loader(new StringLoader())
                .strictVariables(false)
                .build();

        String input = "{{ model.testMethod(\"one\") }}";
        String expected = "string input: one";

        Map<String, Object> context = new HashMap<>();
        context.put("model", new Model());

        PebbleTemplate template = pebble.getTemplate(input);

        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals(expected, writer.toString());
    }

    @Test
    public void testWithContextString() throws PebbleException, IOException {
        this.testWithModel(new Model(), "one", "string input: one");
    }

    @Test
    public void testWithInteger() throws PebbleException, IOException {
        this.testWithModel(new Model(), 1, "Integer input: 1");
    }

    @Test
    public void testWithLong() throws PebbleException, IOException {
        this.testWithModel(new Model(), 1L, "Long input: 1");
    }

    @Test
    public void testWithObject() throws PebbleException, IOException {
        this.testWithModel(new Model(), this, "other input: " + this.getClass().toString());
    }

    @Test
    public void testWithStringString() throws PebbleException, IOException {
        this.testModelWith2Inputs("", "", "string-string inputs");
    }

    @Test
    public void testWithStringObject() throws PebbleException, IOException {
        this.testModelWith2Inputs("", this, "string-object inputs");
    }

    @Test
    public void testWithObjectString() throws PebbleException, IOException {
        this.testModelWith2Inputs(this, "", "object-string inputs");
    }

    @Test
    public void testWithObjectObject() throws PebbleException, IOException {
        this.testModelWith2Inputs(this, this, "object-object inputs");
    }

// Verify that multiple overloaded methods can be called in the same template, and the member cache will not return the
// wrong method and cause a ClassCastException.
//----------------------------------------------------------------------------------------------------------------------

    public static class Model2 {

        public String testMethod(BaseClass input) {
            return "BaseClass input";
        }

        public String testMethod(ChildClass1 input) {
            return "ChildClass1 input";
        }

        public String testMethod(ChildClass2 input) {
            return "ChildClass2 input";
        }

    }

    public static class BaseClass {

    }

    public static class ChildClass1 {

    }

    public static class ChildClass2 {

    }

    @Test
    public void testWithMultipleCalls() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder()
                .loader(new StringLoader())
                .strictVariables(false)
                .build();

        String input = "{{ model.testMethod(BaseClass) }}, {{ model.testMethod(ChildClass1) }}, {{ model.testMethod(ChildClass2) }}";
        String expected = "BaseClass input, ChildClass1 input, ChildClass2 input";

        Map<String, Object> context = new HashMap<>();
        context.put("model", new Model2());
        context.put("BaseClass", new BaseClass());
        context.put("ChildClass1", new ChildClass1());
        context.put("ChildClass2", new ChildClass2());

        PebbleTemplate template = pebble.getTemplate(input);

        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals(expected, writer.toString());
    }

// Check that method resolution works with a deep class hierarchy
//----------------------------------------------------------------------------------------------------------------------

    public static class Model3 {

        public String testMethod(BaseClass input) {
            return "BaseClass input";
        }

        public String testMethod(ChildClass1 input) {
            return "ChildClass1 input";
        }

        public String testMethod(ChildClass2 input) {
            return "ChildClass2 input";
        }

        public String testMethod(ChildClass3 input) {
            return "ChildClass3 input";
        }

        public String testMethod(ChildClass4 input) {
            return "ChildClass4 input";
        }

        public String testMethod(ChildClass5 input) {
            return "ChildClass5 input";
        }

    }

    public static class ChildClass3 extends ChildClass2 {

    }

    public static class ChildClass4 extends ChildClass3 {

    }

    public static class ChildClass5 extends ChildClass4 {

    }

    @Test
    public void testWithBaseClass() throws PebbleException, IOException {
        this.testWithModel(new Model3(), new BaseClass(), "BaseClass input");
    }

    @Test
    public void testWithChildClass1() throws PebbleException, IOException {
        this.testWithModel(new Model3(), new ChildClass1(), "ChildClass1 input");
    }

    @Test
    public void testWithChildClass2() throws PebbleException, IOException {
        this.testWithModel(new Model3(), new ChildClass2(), "ChildClass2 input");
    }

    @Test
    public void testWithChildClass3() throws PebbleException, IOException {
        this.testWithModel(new Model3(), new ChildClass3(), "ChildClass3 input");
    }

    @Test
    public void testWithChildClass4() throws PebbleException, IOException {
        this.testWithModel(new Model3(), new ChildClass4(), "ChildClass4 input");
    }

    @Test
    public void testWithChildClass5() throws PebbleException, IOException {
        this.testWithModel(new Model3(), new ChildClass5(), "ChildClass5 input");
    }

// test helpers
//----------------------------------------------------------------------------------------------------------------------

    private void testWithModel(Object model, Object modelInput, String expected) throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder()
                .loader(new StringLoader())
                .strictVariables(false)
                .build();

        String input = "{{ model.testMethod(input) }}";

        Map<String, Object> context = new HashMap<>();
        context.put("model", model);
        context.put("input", modelInput);

        PebbleTemplate template = pebble.getTemplate(input);

        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals(expected, writer.toString());
    }

    private void testModelWith2Inputs(Object modelInput1, Object modelInput2, String expected) throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder()
                .loader(new StringLoader())
                .strictVariables(false)
                .build();

        String input = "{{ model.testMethod2(input1, input2) }}";

        Map<String, Object> context = new HashMap<>();
        context.put("model", new Model());
        context.put("input1", modelInput1);
        context.put("input2", modelInput2);

        PebbleTemplate template = pebble.getTemplate(input);

        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals(expected, writer.toString());
    }

}
