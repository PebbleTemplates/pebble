package com.mitchellbosecke.pebble.extension.escaper;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class RawFilterTest {
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
    public void testRawFilterWithJsonObject() throws PebbleException, IOException {
    	PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();
        PebbleTemplate template = pebble.getTemplate("{{ text | raw }}");
        Map<String, Object> context = new HashMap<>();
        context.put("text", new JsonObject());
        Writer writer = new StringWriter();
        template.evaluate(writer, context);
        assertEquals(JsonObject.JSON_VALUE, writer.toString());
    }
    
    @Test
    public void testRawFilterWithNullObject() throws PebbleException, IOException {
    	PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();
        PebbleTemplate template = pebble.getTemplate("{{ text | raw }}");
        Writer writer = new StringWriter();
        template.evaluate(writer, new HashMap<String, Object>());
        assertEquals("", writer.toString());
    }
    
    private class JsonObject {
    	public static final String JSON_VALUE = "{\"menu\": {\"id\": \"file\",\"value\": \"File\",\"popup\": {\"menuitem\": [{\"value\": \"New\", \"onclick\": \"CreateNewDoc()\"},{\"value\": \"Open\", \"onclick\": \"OpenDoc()\"},{\"value\": \"Close\", \"onclick\": \"CloseDoc()\"}]}}}";
    	
    	@Override
    	public String toString() {
    		return JSON_VALUE;
    	}
    }
}
