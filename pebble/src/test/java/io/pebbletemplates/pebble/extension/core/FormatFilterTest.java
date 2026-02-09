package io.pebbletemplates.pebble.extension.core;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.loader.StringLoader;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class FormatFilterTest {

    @Test
    void testSliceFilter() throws PebbleException, IOException {

        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
            .strictVariables(false).build();

        String source = "{% set fruit = 'apples' %}\n" +
                        "{{ \"I like %s and %s.\"|format(fruit, \"oranges\") }}";
        PebbleTemplate template = pebble.getTemplate(source);

        Writer writer = new StringWriter();
        template.evaluate(writer, new HashMap<>());
        assertEquals("I like apples and oranges.", writer.toString());
    }

}