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

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.escaper.EscaperExtension;
import com.mitchellbosecke.pebble.extension.escaper.EscapingStrategy;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class EscaperExtensionTest extends AbstractTest {

    @Test
    public void testEscapeHtml() throws PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);

        PebbleTemplate template = pebble.getTemplate("{{ '&<>\"\\'' | escape }}");

        Writer writer = new StringWriter();
        template.evaluate(writer);
        assertEquals("&amp;&lt;&gt;&quot;&#39;", writer.toString());
    }

    @Test
    public void testEscapeContextVariable() throws PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);

        PebbleTemplate template = pebble.getTemplate("{{ text | escape(strategy='html') }}");

        Map<String, Object> context = new HashMap<>();
        context.put("text", "&<>\"\'");
        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("&amp;&lt;&gt;&quot;&#39;", writer.toString());
    }

    @Test
    public void testEscapeWithNamedArguments() throws PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);

        PebbleTemplate template = pebble.getTemplate("{{ '&<>\"\\'' | escape(strategy='html') }}");

        Writer writer = new StringWriter();
        template.evaluate(writer);
        assertEquals("&amp;&lt;&gt;&quot;&#39;", writer.toString());
    }

    @Test
    public void testAutoescapeLiteral() throws PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);
        PebbleTemplate template = pebble.getTemplate("{{ '<br />' }}");
        Writer writer = new StringWriter();
        template.evaluate(writer);
        assertEquals("<br />", writer.toString());
    }

    @Test
    public void testAutoescapePrintExpression() throws PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);
        PebbleTemplate template = pebble.getTemplate("{{ text }}");
        Map<String, Object> context = new HashMap<>();
        context.put("text", "<br />");
        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("&lt;br /&gt;", writer.toString());
    }

    @Test
    public void testDisableAutoEscaping() throws PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);
        EscaperExtension escaper = pebble.getExtension(EscaperExtension.class);
        escaper.setAutoEscaping(false);
        PebbleTemplate template = pebble.getTemplate("{{ text }}");
        Map<String, Object> context = new HashMap<>();
        context.put("text", "<br />");
        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("<br />", writer.toString());
    }

    @Test
    public void testRawFilter() throws PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);
        PebbleTemplate template = pebble.getTemplate("{{ text | upper | raw }}");
        Map<String, Object> context = new HashMap<>();
        context.put("text", "<br />");
        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("<BR />", writer.toString());
    }

    @Test
    public void testRawFilterNotBeingLast() throws PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);
        PebbleTemplate template = pebble.getTemplate("{{ text | raw | upper}}");
        Map<String, Object> context = new HashMap<>();
        context.put("text", "<br />");
        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("&lt;BR /&gt;", writer.toString());
    }

    @Test
    public void testDoubleEscaping() throws PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);
        PebbleTemplate template = pebble.getTemplate("{{ text | escape }}");
        Map<String, Object> context = new HashMap<>();
        context.put("text", "<br />");
        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("&lt;br /&gt;", writer.toString());
    }

    @Test
    public void testAutoescapeToken() throws PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);
        EscaperExtension escaper = pebble.getExtension(EscaperExtension.class);
        escaper.setAutoEscaping(false);
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
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);
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
        PebbleTemplate template = pebble.getTemplate("template.autoescapeInclude1.peb");
        Map<String, Object> context = new HashMap<>();
        context.put("text", "<br>");
        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("<&lt;br&gt;>", writer.toString());
    }

    @Test
    public void testRawFilterWithinAutoescapeToken() throws PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);
        EscaperExtension escaper = pebble.getExtension(EscaperExtension.class);
        escaper.setAutoEscaping(false);
        PebbleTemplate template = pebble.getTemplate("{% autoescape 'html' %}{{ text|raw }}{% endautoescape %}");
        Map<String, Object> context = new HashMap<>();
        context.put("text", "<br />");
        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("<br />", writer.toString());
    }

    @Test
    public void testAutoEscapingParentFunction() throws PebbleException, IOException {
        PebbleTemplate template = pebble.getTemplate("template.autoescapeParent1.peb");
        Map<String, Object> context = new HashMap<>();
        context.put("text", "<br>");
        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("<&lt;br&gt;>", writer.toString());
    }

    @Test
    public void testAutoEscapingBlockFunction() throws PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);
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
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);
        EscaperExtension extension = pebble.getExtension(EscaperExtension.class);
        extension.setDefaultStrategy("custom");

        // replaces all a's with b's
        extension.addEscapingStrategy("custom", new EscapingStrategy() {

            @Override
            public String escape(String input) {
                return input.replace('a', 'b');
            }
        });
        PebbleTemplate template = pebble.getTemplate("{{ text }}");
        Map<String, Object> context = new HashMap<>();
        context.put("text", "my name is alex");
        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("my nbme is blex", writer.toString());
    }
}
