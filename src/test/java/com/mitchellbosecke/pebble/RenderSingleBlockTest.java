package com.mitchellbosecke.pebble;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;

import org.junit.Test;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class RenderSingleBlockTest extends AbstractTest {

    @Test
    public void testRenderSingleBlock() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

        String source = "Prefix {% block block_a %}Block A{% endblock %}{% block block_b %}Block B{% endblock %} Postfix";
        PebbleTemplate template = pebble.getTemplate(source);

        Writer writer_a = new StringWriter();
        template.evaluateBlock("block_a", writer_a, new HashMap<String, Object>(), null);
        assertEquals("Block A", writer_a.toString());

        Writer writer_b = new StringWriter();
        template.evaluateBlock("block_b", writer_b, new HashMap<String, Object>(), null);
        assertEquals("Block B", writer_b.toString());
    }

    @Test
    public void testRenderSingleExtendedBlock() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(true).build();
        PebbleTemplate template = pebble.getTemplate("templates/single-block/template.renderextendedblock1.peb");

        Writer writer_a = new StringWriter();
        template.evaluateBlock("container_a", writer_a, new HashMap<String, Object>(), null);
        assertEquals("Block A extended", writer_a.toString());

        Writer writer_b = new StringWriter();
        template.evaluateBlock("container_b", writer_b, new HashMap<String, Object>(), null);
        assertEquals("Block B extended", writer_b.toString());
    }

}
