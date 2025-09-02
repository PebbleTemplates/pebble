package io.pebbletemplates.pebble;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.extension.core.Nl2brFilter;
import io.pebbletemplates.pebble.extension.escaper.SafeString;
import io.pebbletemplates.pebble.loader.StringLoader;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for nl2br filter
 */
class Nl2brFilterTest {

    private final Nl2brFilter filter = new Nl2brFilter();

    private static String asString(Object o) {
        if (o == null) return null;
        if (o instanceof SafeString) return o.toString();
        return String.valueOf(o);
    }

    @Test
    void testEmptyString() throws PebbleException {
        Object out = filter.apply("", null, null, null, 0);
        assertEquals("", asString(out));
    }

    @Test
    void testNonStringInputThrows() {
        assertThrows(IllegalArgumentException.class, () -> filter.apply(42, null, null, null, 0));
    }

    @Test
    void testLfOnly() throws PebbleException {
        Object out = filter.apply("A\nB\nC", null, null, null, 0);
        assertEquals("A<br />B<br />C", asString(out));
    }

    @Test
    void testCrOnly() throws PebbleException {
        Object out = filter.apply("A\rB\rC", null, null, null, 0);
        assertEquals("A<br />B<br />C", asString(out));
    }

    @Test
    void testCrLfOnly() throws PebbleException {
        Object out = filter.apply("A\r\nB\r\nC", null, null, null, 0);
        assertEquals("A<br />B<br />C", asString(out));
    }

    @Test
    void testMixedNewlines() throws PebbleException {
        Object out = filter.apply("A\nB\rC\r\nD", null, null, null, 0);
        assertEquals("A<br />B<br />C<br />D", asString(out));
    }

    @Test
    void testNoNewlineReturnsSafeString() throws PebbleException {
        String input = "NoNewlinesHere";
        Object out = filter.apply(input, null, null, null, 0);
        assertEquals(input, asString(out));
    }

    @Test
    void testNullInput() throws PebbleException {
        Object out = filter.apply(null, null, null, null, 0);
        assertNull(out);
    }

    @Test
    void testIntegrationWithEngine() throws IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).build();
        PebbleTemplate template = pebble.getTemplate("{{ txt | nl2br | raw }}");
        Writer w = new StringWriter();
        template.evaluate(w, java.util.Collections.singletonMap("txt", "Line1\nLine2\rLine3\r\nLine4"));
        assertEquals("Line1<br />Line2<br />Line3<br />Line4", w.toString());
    }
}
