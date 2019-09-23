package com.mitchellbosecke.pebble;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.loader.DelegatingLoader;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;


public class EmbedCachingTagTest {

    private final PebbleEngine pebble;
    private final Map<String, Object> context;

    public EmbedCachingTagTest() {
        StringLoader stringLoader = new StringLoader();
        ClasspathLoader classpathLoader = new ClasspathLoader();
        classpathLoader.setPrefix("templates/embed/cache");

        pebble = new PebbleEngine.Builder()
                .loader(new DelegatingLoader(Arrays.asList(
                        classpathLoader,
                        stringLoader
                )))
                .strictVariables(false)
                .cacheActive(true)
                .build();

        Writer writer = new StringWriter();
        context = new HashMap<>();
        context.put("foo", "FOO");
        context.put("bar", "BAR");
    }

    @Test
    public void testEmbedNotChangingCachedTemplate() throws PebbleException, IOException {
        Writer writer1 = new StringWriter();
        PebbleTemplate template1 = pebble.getTemplate("template1.peb");
        template1.evaluate(writer1, context);
        String result1 = writer1.toString();

        Writer writer2 = new StringWriter();
        PebbleTemplate template2 = pebble.getTemplate("template2.peb");
        template2.evaluate(writer2, context);
        String result2 = writer2.toString();

        assertEquals("" +
                "BEFORE BASE\n" +
                "EMBED OVERRIDE\n" +
                "AFTER BASE",
                result1
        );
        assertEquals("" +
                "BEFORE BASE\n" +
                "EMBED BASE\n" +
                "AFTER BASE",
                result2
        );
    }

}
