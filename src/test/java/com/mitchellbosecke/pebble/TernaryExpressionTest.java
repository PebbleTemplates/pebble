/*******************************************************************************
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble;

import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TernaryExpressionTest extends AbstractTest {

    @Test(expected = ParserException.class)
    public void testTernaryFail1() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

        String source = "{{ 1 > 1 ? 'true' }}";
        PebbleTemplate template = pebble.getTemplate(source);

        Writer writer = new StringWriter();
        template.evaluate(writer, new HashMap<String, Object>());
    }

    @Test(expected = ParserException.class)
    public void testTernaryFail2() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

        String source = "{{ 1 > 1 ? : 'true' }}";
        PebbleTemplate template = pebble.getTemplate(source);

        Writer writer = new StringWriter();
        template.evaluate(writer, new HashMap<String, Object>());
    }

    @Test(expected = ParserException.class)
    public void testTernaryFail3() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

        String source = "{{ 1 > 1 ? 'true' : }}";
        PebbleTemplate template = pebble.getTemplate(source);

        Writer writer = new StringWriter();
        template.evaluate(writer, new HashMap<String, Object>());
    }

    @Test(expected = ParserException.class)
    public void testTernaryFail4() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

        String source = "{{ 1 > 1 ? : }}";
        PebbleTemplate template = pebble.getTemplate(source);

        Writer writer = new StringWriter();
        template.evaluate(writer, new HashMap<String, Object>());
    }

    @Test(expected = ParserException.class)
    public void testTernaryFail5() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

        String source = "{{ 1 > 1 ? : ? 'true' : 'false' }}";
        PebbleTemplate template = pebble.getTemplate(source);

        Writer writer = new StringWriter();
        template.evaluate(writer, new HashMap<String, Object>());
    }

    @Test(expected = ParserException.class)
    public void testTernaryFail6() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

        String source = "{{ 1 > 1 ? true ? 'true' : 'false' }}";
        PebbleTemplate template = pebble.getTemplate(source);

        Writer writer = new StringWriter();
        template.evaluate(writer, new HashMap<String, Object>());
    }

    @Test(expected = ParserException.class)
    public void testTernaryFail7() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

        String source = "{{ 1 > 1 ? : false ? 'true' : 'false' }}";
        PebbleTemplate template = pebble.getTemplate(source);

        Writer writer = new StringWriter();
        template.evaluate(writer, new HashMap<String, Object>());
    }

    @Test(expected = ParserException.class)
    public void testTernaryFail8() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

        String source = "{{ 1 > 1 ? 2 > 2 ? 'true' : 'false' }}";
        PebbleTemplate template = pebble.getTemplate(source);

        Writer writer = new StringWriter();
        template.evaluate(writer, new HashMap<String, Object>());
    }

    @Test(expected = ParserException.class)
    public void testTernaryFail9() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

        String source = "{{ 1 > 1 ? 2 > 2 ? : 'false' : 'false' }}";
        PebbleTemplate template = pebble.getTemplate(source);

        Writer writer = new StringWriter();
        template.evaluate(writer, new HashMap<String, Object>());
    }

    @Test(expected = ParserException.class)
    public void testTernaryFail10() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

        String source = "{{ 1 > 1 ? 2 > 2 ? : : 'false' }}";
        PebbleTemplate template = pebble.getTemplate(source);

        Writer writer = new StringWriter();
        template.evaluate(writer, new HashMap<String, Object>());
    }

    @Test(expected = ParserException.class)
    public void testTernaryFail11() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

        String source = "{{ 1 > 1 ? 'true' : 3 > 3 ? 'false' }}";
        PebbleTemplate template = pebble.getTemplate(source);

        Writer writer = new StringWriter();
        template.evaluate(writer, new HashMap<String, Object>());
    }

    @Test(expected = ParserException.class)
    public void testTernaryFail12() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

        String source = "{{ 1 > 1 ? 'true' : 3 > 3 ? : 'false' }}";
        PebbleTemplate template = pebble.getTemplate(source);

        Writer writer = new StringWriter();
        template.evaluate(writer, new HashMap<String, Object>());
    }

    @Test(expected = ParserException.class)
    public void testTernaryFail13() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

        String source = "{{ 1 > 1 ? 'true' : 3 > 3 ? : }}";
        PebbleTemplate template = pebble.getTemplate(source);

        Writer writer = new StringWriter();
        template.evaluate(writer, new HashMap<String, Object>());
    }

    @Test
    public void testTernary1() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

        String source = "{{ 1 == 1 ? 'true' : 'false' }}";
        PebbleTemplate template = pebble.getTemplate(source);

        Writer writer = new StringWriter();
        template.evaluate(writer, new HashMap<String, Object>());
        assertEquals("true", writer.toString());
    }

    @Test
    public void testTernary2() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

        String source = "{{ 1 > 1 ? 'true' : 'false' }}";
        PebbleTemplate template = pebble.getTemplate(source);

        Writer writer = new StringWriter();
        template.evaluate(writer, new HashMap<String, Object>());
        assertEquals("false", writer.toString());
    }

    @Test
    public void testTernary3() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

        String source = "{{ 1 > 1 ? true : false ? 2 > 2 ? 'a' : 'b' : 3 == 3 ? 'c' : 'd' }}";
        PebbleTemplate template = pebble.getTemplate(source);

        Writer writer = new StringWriter();
        template.evaluate(writer, new HashMap<String, Object>());
        assertEquals("c", writer.toString());
    }

    @Test
    public void testComplexTernary1() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

        String source = "{{ ('a' == 'b' ? 2 + 2 : (val - 2 is not even ? true : false) ) ? (min(otherVal,-1) | abs <  3 / 3 - 1 ? false : ['yay!'] contains 'yay!' ) : ('?' is not empty ? ''~'?' : 0) }}";
        PebbleTemplate template = pebble.getTemplate(source);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("val", 3);
        params.put("otherVal", 100);
        Writer writer = new StringWriter();
        template.evaluate(writer, params);
        assertEquals("true", writer.toString());
    }

    @Test
    public void testComplexTernary2() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

        String source = "{{ 'a' == 'b' ? 2 + 2 : val - 2 is not even ? true : false ? min(otherVal,-1) | abs <  3 / 3 - 1 ? false : ['yay!'] contains 'yay!' : '?' is not empty ? ''~'?' : 0 }}";
        PebbleTemplate template = pebble.getTemplate(source);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("val", 3);
        params.put("otherVal", 100);
        Writer writer = new StringWriter();
        template.evaluate(writer, params);
        assertEquals("true", writer.toString());
    }

}
