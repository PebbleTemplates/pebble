package com.mitchellbosecke.pebble;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.StringLoader;

public class StringInterpolationTest {

    @Test
    public void testSimpleVariableInterpolation() throws PebbleException, IOException {
        String source = "{{ \"Hello, #{name}\" }}";
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("name", "joe");
        String evaluated = evaluate(source, ctx);

        assertEquals("Hello, joe", evaluated);
    }

    @Test
    public void testExpressionInterpolation() throws PebbleException, IOException {
        String src = "{{ \"1 plus 2 equals #{1 + 2}\" }}";
        String evaluated = evaluate(src);
        assertEquals("1 plus 2 equals 3", evaluated);
    }

    @Test
    public void testUnclosedInterpolation() throws PebbleException, IOException {
        String src = "{{ \" #{ 1 +\" }}";

        try {
            evaluate(src);
            fail(String.format("Expected exception %s not thrown", ParserException.class.getSimpleName()));
        } catch (ParserException ex) {
        }
    }

    @Test
    public void testFunctionInInterpolation() throws PebbleException, IOException {
        String src = "{{ \"Maximum: #{ max(5, 10) } \" }}";
        String evaluated = evaluate(src);
        assertEquals("Maximum: 10 ", evaluated);
    }

    @Test
    public void testVerbatimInterpolation() throws PebbleException, IOException {
        String src = "{% verbatim %}{{ \"Sum: #{ 1 + 2 }\" }}{% endverbatim %}";
        String evaluated = evaluate(src);
        assertEquals("{{ \"Sum: #{ 1 + 2 }\" }}", evaluated);
    }

    @Test
    public void testNestedInterpolation() throws PebbleException, IOException {
        String src = "{{ \"Nested: #{ outer + \" #{ inner }\" }\" }}";
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("outer", "OUTER");
        ctx.put("inner", "INNER");
        String evaluated = evaluate(src, ctx);
        assertEquals("Nested: OUTER INNER", evaluated);
    }

    @Test
    public void testNestedInterpolation1() throws PebbleException, IOException {
        String src = "{{ \"Outer: #{ \"inner: #{ 3 + 4 }\"}\" }}";
        assertEquals("Outer: inner: 7", evaluate(src));
    }

    @Test
    public void testNestedInterpolation2() throws PebbleException, IOException {
        String src = "{{ \" Outer: #{ \"inner: #{ 3 + 4 }\"}\" }}";
        assertEquals(" Outer: inner: 7", evaluate(src));
    }

    @Test
    public void testNestedInterpolation3() throws PebbleException, IOException {
        String src = "{{ \"Outer: #{ \"inner: #{ 3 + 4 } \"}\" }}";
        assertEquals("Outer: inner: 7 ", evaluate(src));
    }

    @Test
    public void testStringInsideInterpolation() throws PebbleException, IOException {
        String src = "{{ \"Outer: #{ \"inner\" }\" }}";
        String evaluated = evaluate(src);
        assertEquals("Outer: inner", evaluated);
    }

    private String evaluate(String template) throws PebbleException, IOException {
        return evaluate(template, null);
    }

    private String evaluate(String template, Map<String, Object> context) throws PebbleException, IOException {
        Writer writer = new StringWriter();

        new PebbleEngine.Builder()
                .loader(new StringLoader())
                .strictVariables(false)
                .build()
                .getTemplate(template)
                .evaluate(writer, context);
        return writer.toString();
    }
}
