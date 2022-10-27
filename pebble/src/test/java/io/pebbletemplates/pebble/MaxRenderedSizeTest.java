package io.pebbletemplates.pebble;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.io.Writer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MaxRenderedSizeTest {
    @Test
    void renderingExplodingMacroWithLimitWillThrowPebbleException() {
        PebbleEngine pebble = new PebbleEngine.Builder()
                .maxRenderedSize(1000)
                .build();

        PebbleTemplate template = pebble.getTemplate("templates/template.macro.exploding.peb");

        Writer writer = new StringWriter();

        PebbleException thrown = assertThrows(PebbleException.class, () -> template.evaluate(writer));
        String result = writer.toString();
        // We didn't write more than allowed.
        assertTrue(result.length() <= 1000);
        assertTrue(thrown.getMessage().contains("1000"));
    }

    @Test
    @Disabled("This test passes but takes about a minute to do so. Creating a faster macro bomb would be nice.")
    void renderingExplodingMacroWithoutLimitWillThrowOOMException() {
        PebbleEngine pebble = new PebbleEngine.Builder()
                .build();

        PebbleTemplate template = pebble.getTemplate("templates/template.macro.exploding.peb");

        StringWriter writer = new StringWriter();

        assertThrows(OutOfMemoryError.class, () -> template.evaluate(writer));
    }
}
