/*******************************************************************************
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.extension.escaper.EscapingStrategy;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class EscaperExtensionTest extends AbstractTest {

    @Test
    public void testEscapeHtml() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

        PebbleTemplate template = pebble.getTemplate("{{ '&<>\"\\'' | escape }}");

        Writer writer = new StringWriter();
        template.evaluate(writer);
        assertEquals("&amp;&lt;&gt;&quot;&#39;", writer.toString());
    }

    @Test()
    public void testPrintBigDecimal() throws PebbleException, IOException {

        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(true).build();

        String source = "{{ num }}";
        PebbleTemplate template = pebble.getTemplate(source);

        Map<String, Object> context = new HashMap<>();
        BigDecimal num = new BigDecimal("1234E+4");
        context.put("num", num);

        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals(num.toPlainString(), writer.toString());
        assertNotEquals(num.toString(), writer.toString());
    }

    @Test
    public void testEscapeContextVariable() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

        PebbleTemplate template = pebble.getTemplate("{{ text | escape(strategy='html') }}");

        Map<String, Object> context = new HashMap<>();
        context.put("text", "&<>\"\'");
        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("&amp;&lt;&gt;&quot;&#39;", writer.toString());
    }

    @Test
    public void testEscapeWithNamedArguments() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

        PebbleTemplate template = pebble.getTemplate("{{ '&<>\"\\'' | escape(strategy='html') }}");

        Writer writer = new StringWriter();
        template.evaluate(writer);
        assertEquals("&amp;&lt;&gt;&quot;&#39;", writer.toString());
    }

    @Test
    public void testAutoescapeLiteral() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();
        PebbleTemplate template = pebble.getTemplate("{{ '<br />' }}");
        Writer writer = new StringWriter();
        template.evaluate(writer);
        assertEquals("<br />", writer.toString());
    }

    @Test
    public void testAutoescapePrintExpression() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();
        PebbleTemplate template = pebble.getTemplate("{{ text }}");
        Map<String, Object> context = new HashMap<>();
        context.put("text", "<br />");
        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("&lt;br /&gt;", writer.toString());
    }

    @Test
    public void testAutoescapeNonString() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();
        PebbleTemplate template = pebble.getTemplate("{{ text }}");
        Map<String, Object> context = new HashMap<>();
        context.put("text", Collections.singletonList("<br />"));
        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("[&lt;br /&gt;]", writer.toString());
    }

    @Test
    public void testDisableAutoEscaping() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false)
                .autoEscaping(false).build();
        PebbleTemplate template = pebble.getTemplate("{{ text }}");
        Map<String, Object> context = new HashMap<>();
        context.put("text", "<br />");
        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("<br />", writer.toString());
    }

    @Test
    public void testRawFilter() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();
        PebbleTemplate template = pebble.getTemplate("{{ text | upper | raw }}");
        Map<String, Object> context = new HashMap<>();
        context.put("text", "<br />");
        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("<BR />", writer.toString());
    }

    @Test
    public void testRawFilterNotBeingLast() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();
        PebbleTemplate template = pebble.getTemplate("{{ text | raw | upper}}");
        Map<String, Object> context = new HashMap<>();
        context.put("text", "<br />");
        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("&lt;BR /&gt;", writer.toString());
    }

    @Test
    public void testEscapeIntoAbbreviate() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();
        PebbleTemplate template = pebble.getTemplate("{{ text | escape | abbreviate(5)}}");
        Map<String, Object> context = new HashMap<>();
        context.put("text", "1234567");
        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("12...", writer.toString());
    }

    @Test
    public void testDoubleEscaping() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();
        PebbleTemplate template = pebble.getTemplate("{{ text | escape }}");
        Map<String, Object> context = new HashMap<>();
        context.put("text", "<br />");
        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("&lt;br /&gt;", writer.toString());
    }

    @Test
    public void testAutoescapeToken() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false)
                .autoEscaping(false).build();
        PebbleTemplate template = pebble.getTemplate(
                "{% autoescape 'html' %}{{ text }}{% endautoescape %}" + "{% autoescape %}{{ text }}{% endautoescape %}"
                        + "{% autoescape false %}{{ text }}{% endautoescape %}");
        Map<String, Object> context = new HashMap<>();
        context.put("text", "<br />");
        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("&lt;br /&gt;&lt;br /&gt;<br />", writer.toString());
    }

    @Test
    public void testAutoEscapingMacroOutput() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();
        PebbleTemplate template = pebble
                .getTemplate("{{ test(danger) }}{% macro test(input) %}<{{ input }}>{% endmacro %}");
        Writer writer = new StringWriter();
        Map<String, Object> context = new HashMap<>();
        context.put("danger", "<br>");
        template.evaluate(writer, context);
        assertEquals("<&lt;br&gt;>", writer.toString());
    }

    @Test
    public void testAutoEscapingInclude() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(false).build();
        PebbleTemplate template = pebble.getTemplate("templates/template.autoescapeInclude1.peb");
        Map<String, Object> context = new HashMap<>();
        context.put("text", "<br>");
        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("<&lt;br&gt;>", writer.toString());
    }

    @Test
    public void testRawFilterWithinAutoescapeToken() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false)
                .autoEscaping(false).build();
        PebbleTemplate template = pebble.getTemplate("{% autoescape 'html' %}{{ text|raw }}{% endautoescape %}");
        Map<String, Object> context = new HashMap<>();
        context.put("text", "<br />");
        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("<br />", writer.toString());
    }

    @Test
    public void testAutoEscapingParentFunction() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(false).build();
        PebbleTemplate template = pebble.getTemplate("templates/template.autoescapeParent1.peb");
        Map<String, Object> context = new HashMap<>();
        context.put("text", "<br>");
        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("<&lt;br&gt;>", writer.toString());
    }

    @Test
    public void testAutoEscapingBlockFunction() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();
        PebbleTemplate template = pebble
                .getTemplate("{% block header %}<{{ text }}>{% endblock %}{{ block('header') }}");
        Map<String, Object> context = new HashMap<>();
        context.put("text", "<br>");
        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("<&lt;br&gt;><&lt;br&gt;>", writer.toString());
    }

    @Test
    public void testCustomEscapingStrategy() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false)
                .defaultEscapingStrategy("custom").addEscapingStrategy("custom", new EscapingStrategy() {

                    @Override
                    public String escape(String input) {
                        return input.replace('a', 'b');
                    }
                }).build();

        // replaces all a's with b's
        PebbleTemplate template = pebble.getTemplate("{{ text }}");
        Map<String, Object> context = new HashMap<>();
        context.put("text", "my name is alex");
        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("my nbme is blex", writer.toString());
    }

    @Test
    public void testEscapeFunction() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false)
                .extension(new TestExtension()).build();
        PebbleTemplate template = pebble.getTemplate("{{ bad() }}");
        Map<String, Object> context = new HashMap<>();
        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("&lt;script&gt;alert(&quot;injection&quot;);&lt;/script&gt;", writer.toString());
    }

    @Test
    public void testNoEscapeMacro() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();
        PebbleTemplate template = pebble.getTemplate("{{ test() }}{% macro test() %}<br/>{% endmacro %}");
        Map<String, Object> context = new HashMap<>();
        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("<br/>", writer.toString());
    }

    @Test
    public void testCompareSafeStrings() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();
        PebbleTemplate template = pebble.getTemplate("{{ text|raw == text|raw }}");
        Map<String, Object> context = new HashMap<>();
        context.put("text", "a");
        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("true", writer.toString());
    }

    public static class TestExtension extends AbstractExtension {

        @Override
        public Map<String, Function> getFunctions() {
            return Collections.<String, Function>singletonMap("bad", new Function() {

                @Override
                public List<String> getArgumentNames() {
                    return null;
                }

                @Override
                public Object execute(Map<String, Object> args, PebbleTemplateImpl self, int lineNumber) {
                    return "<script>alert(\"injection\");</script>";
                }

            });
        }

    }
}
