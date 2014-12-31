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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class CoreFiltersTest extends AbstractTest {

    @Test
    public void testAbs() throws PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);

        PebbleTemplate template = pebble.getTemplate("{{ -5 | abs }}");

        Writer writer = new StringWriter();
        template.evaluate(writer);
        assertEquals("5", writer.toString());
    }
    
    @Test
    public void testAbsDouble() throws PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);

        PebbleTemplate template = pebble.getTemplate("{{ -5.2 | abs }}");

        Writer writer = new StringWriter();
        template.evaluate(writer);
        assertEquals("5.2", writer.toString());
    }

    @Test
    public void testChainedFiltersWithNullInput() throws PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);

        PebbleTemplate template = pebble.getTemplate("{{ null | upper | lower }}");
        Writer writer = new StringWriter();
        template.evaluate(writer);
        assertEquals("", writer.toString());
    }

    @Test
    public void testLower() throws PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);

        PebbleTemplate template = pebble.getTemplate("{{ 'TEMPLATE' | lower }}");

        Writer writer = new StringWriter();
        template.evaluate(writer);
        assertEquals("template", writer.toString());
    }

    @Test
    public void testLowerWithNullInput() throws PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);

        PebbleTemplate template = pebble.getTemplate("{{ null | lower }}");

        Writer writer = new StringWriter();
        template.evaluate(writer);
        assertEquals("", writer.toString());
    }

    @Test
    public void testUpper() throws PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);

        PebbleTemplate template = pebble.getTemplate("{{ 'template' | upper }}");
        Writer writer = new StringWriter();
        template.evaluate(writer);
        assertEquals("TEMPLATE", writer.toString());
    }

    @Test
    public void testUpperWithNullInput() throws PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);

        PebbleTemplate template = pebble.getTemplate("{{ null | upper }}");
        Writer writer = new StringWriter();
        template.evaluate(writer);
        assertEquals("", writer.toString());
    }

    @Test
    public void testDate() throws ParseException, PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);

        String source = "{{ realDate | date('MM/dd/yyyy') }}{{ realDate | date(format) }}{{ stringDate | date('yyyy/MMMM/d','yyyy-MMMM-d') }}";

        PebbleTemplate template = pebble.getTemplate(source);
        Map<String, Object> context = new HashMap<>();
        DateFormat format = new SimpleDateFormat("yyyy-MMMM-d");
        Date realDate = format.parse("2012-July-01");
        context.put("realDate", realDate);
        context.put("stringDate", format.format(realDate));
        context.put("format", "yyyy-MMMM-d");

        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("07/01/20122012-July-12012/July/1", writer.toString());
    }

    @Test
    public void testDateWithNamedArguments() throws ParseException, PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);

        String source = "{{ stringDate | date(existingFormat='yyyy-MMMM-d', format='yyyy/MMMM/d') }}";

        PebbleTemplate template = pebble.getTemplate(source);
        Map<String, Object> context = new HashMap<>();
        DateFormat format = new SimpleDateFormat("yyyy-MMMM-d");
        Date realDate = format.parse("2012-July-01");
        context.put("stringDate", format.format(realDate));

        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("2012/July/1", writer.toString());
    }

    @Test
    public void testDateWithNullInput() throws ParseException, PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);

        String source = "{{ null | date(\"MM/dd/yyyy\") }}";

        PebbleTemplate template = pebble.getTemplate(source);

        Writer writer = new StringWriter();
        template.evaluate(writer);
        assertEquals("", writer.toString());
    }

    @Test
    public void testUrlEncode() throws PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);

        PebbleTemplate template = pebble.getTemplate("{{ 'The string ü@foo-bar' | urlencode }}");
        Writer writer = new StringWriter();
        template.evaluate(writer);
        assertEquals("The+string+%C3%BC%40foo-bar", writer.toString());
    }

    @Test
    public void testUrlEncodeWithNullInput() throws PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);

        PebbleTemplate template = pebble.getTemplate("{{ null | urlencode }}");
        Writer writer = new StringWriter();
        template.evaluate(writer);
        assertEquals("", writer.toString());
    }

    @Test
    public void testNumberFormatFilterWithFormat() throws PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);

        PebbleTemplate template = pebble.getTemplate("You owe me {{ 10000.235166 | numberformat(currencyFormat) }}.");
        Map<String, Object> context = new HashMap<>();
        context.put("currencyFormat", "$#,###,###,##0.00");

        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("You owe me $10,000.24.", writer.toString());
    }

    @Test
    public void testNumberFormatFilterWithNamedArgument() throws PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);

        PebbleTemplate template = pebble
                .getTemplate("You owe me {{ 10000.235166 | numberformat(format=currencyFormat) }}.");
        Map<String, Object> context = new HashMap<>();
        context.put("currencyFormat", "$#,###,###,##0.00");

        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("You owe me $10,000.24.", writer.toString());
    }

    @Test
    public void testNumberFormatFilterWithNullInput() throws PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);

        PebbleTemplate template = pebble.getTemplate("{{ null | numberformat(currencyFormat) }}");

        Writer writer = new StringWriter();
        template.evaluate(writer);
        assertEquals("", writer.toString());
    }

    @Test
    public void testNumberFormatFilterWithLocale() throws PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);

        PebbleTemplate template = pebble.getTemplate("{{ 1000000 | numberformat }}");

        Writer writer = new StringWriter();
        template.evaluate(writer);
        assertEquals("1,000,000", writer.toString());
    }

    @Test
    public void testAbbreviate() throws PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);

        PebbleTemplate template = pebble
                .getTemplate("{{ 'This is a test of the abbreviate filter' | abbreviate(16) }}");

        Writer writer = new StringWriter();
        template.evaluate(writer);
        assertEquals("This is a tes...", writer.toString());
    }

    @Test
    public void testAbbreviateWithNamedArguments() throws PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);

        PebbleTemplate template = pebble
                .getTemplate("{{ 'This is a test of the abbreviate filter' | abbreviate(length=16) }}");

        Writer writer = new StringWriter();
        template.evaluate(writer);
        assertEquals("This is a tes...", writer.toString());
    }

    @Test
    public void testAbbreviateWithNullInput() throws PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);

        PebbleTemplate template = pebble.getTemplate("{{ null | abbreviate(16) }}");

        Writer writer = new StringWriter();
        template.evaluate(writer);
        assertEquals("", writer.toString());
    }

    @Test
    public void testCapitalize() throws PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);

        PebbleTemplate template = pebble.getTemplate("{{ 'this should be capitalized.' | capitalize }}");

        Writer writer = new StringWriter();
        template.evaluate(writer);
        assertEquals("This should be capitalized.", writer.toString());
    }

    @Test
    public void testCapitalizeWithLeadingWhitespace() throws PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);

        PebbleTemplate template = pebble.getTemplate("{{ ' \nthis should be capitalized.' | capitalize }}");

        Writer writer = new StringWriter();
        template.evaluate(writer);
        assertEquals(" \nThis should be capitalized.", writer.toString());
    }

    @Test
    public void testCapitalizeWithNullInput() throws PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);

        PebbleTemplate template = pebble.getTemplate("{{ null | capitalize }}");

        Writer writer = new StringWriter();
        template.evaluate(writer);
        assertEquals("", writer.toString());
    }

    @Test
    public void testCapitalizeWithEmptyString() throws PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);

        PebbleTemplate template = pebble.getTemplate("{{ '' | capitalize }}");

        Writer writer = new StringWriter();
        template.evaluate(writer);
        assertEquals("", writer.toString());
    }
    
    @Test
    public void testSortFilter() throws PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);

        PebbleTemplate template = pebble.getTemplate("{% for word in words|sort %}{{ word }} {% endfor %}");
        List<String> words = new ArrayList<>();
        words.add("zebra");
        words.add("apple");
        words.add(" cat");
        words.add("123");
        words.add("Apple");
        words.add("cat");
        
        Map<String, Object> context = new HashMap<>();
        context.put("words", words);
        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals(" cat 123 Apple apple cat zebra ", writer.toString());
    }

    @Test
    public void testTitle() throws PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);

        PebbleTemplate template = pebble
                .getTemplate("{{ null | title }} {{ 'test' | title }} {{ 'test test' | title }} {{ 'TEST TEST' | title }}");

        Writer writer = new StringWriter();
        template.evaluate(writer);
        assertEquals(" Test Test Test TEST TEST", writer.toString());
    }

    @Test
    public void testTitleWithLeadingWhitespace() throws PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);

        PebbleTemplate template = pebble.getTemplate("{{ ' \ntest' | title }}");

        Writer writer = new StringWriter();
        template.evaluate(writer);
        assertEquals(" \nTest", writer.toString());
    }

    @Test
    public void testTrim() throws PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);

        PebbleTemplate template = pebble.getTemplate("{{ '        		This should be trimmed. 		' | trim }}");

        Writer writer = new StringWriter();
        template.evaluate(writer);
        assertEquals("This should be trimmed.", writer.toString());
    }

    @Test
    public void testTrimWithNullInput() throws PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);

        PebbleTemplate template = pebble.getTemplate("{{ null | trim }}");

        Writer writer = new StringWriter();
        template.evaluate(writer);
        assertEquals("", writer.toString());
    }

    @Test
    public void testDefault() throws PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);

        PebbleTemplate template = pebble
                .getTemplate("{{ obj|default('ONE') }} {{ null|default('TWO') }} {{ '  ' |default('THREE') }} {{ 4 |default('FOUR') }}");
        Map<String, Object> context = new HashMap<>();
        context.put("obj", null);

        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("ONE TWO THREE 4", writer.toString());
    }

    @Test
    public void testDefaultWithNamedArguments() throws PebbleException, IOException {
        Loader loader = new StringLoader();
        PebbleEngine pebble = new PebbleEngine(loader);

        PebbleTemplate template = pebble.getTemplate("{{ obj|default(default='ONE') }}");
        Map<String, Object> context = new HashMap<>();
        context.put("obj", null);

        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals("ONE", writer.toString());
    }

    public class User {

        private final String username;

        public User(String username) {
            this.username = username;
        }

        public String getUsername() {
            return username;
        }
    }

}
