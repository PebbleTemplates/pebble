package io.pebbletemplates.pebble.utils;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.template.RenderedSizeContext;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link LimitedSizeWriter}.
 */
class LimitedSizeWriterTest {
    @Test
    void negativeMaxSizeReturnsTheOriginalWriter() {
        Writer internalWriter = new StringWriter();

        Writer limitedSizeWriter = LimitedSizeWriter.from(internalWriter, new TestRenderedSizeContext(-1));

        assertSame(internalWriter, limitedSizeWriter);
    }

    @Test
    void canWriteLessThanLimitChars() throws IOException {
        Writer internalWriter = new StringWriter();

        Writer limitedSizeWriter = LimitedSizeWriter.from(internalWriter, new TestRenderedSizeContext(20));

        limitedSizeWriter.write("0123456789");
        limitedSizeWriter.write("0123456789");

        assertEquals("01234567890123456789", internalWriter.toString());
    }

    @Test
    void cannotWriteMoreThanLimitChars() throws IOException {
        Writer internalWriter = new StringWriter();

        Writer limitedSizeWriter = LimitedSizeWriter.from(internalWriter, new TestRenderedSizeContext(19));

        limitedSizeWriter.write("0123456789");

        PebbleException thrown = assertThrows(
                PebbleException.class,
                () -> limitedSizeWriter.write("0123456789"));

        assertTrue(thrown.getMessage().contains("19"));
    }

    @Test
    void contextIsSharedBetweenWriters() throws IOException {
        Writer internalWriter1 = new StringWriter();
        Writer internalWriter2 = new StringWriter();

        TestRenderedSizeContext context = new TestRenderedSizeContext(19);
        Writer limitedSizeWriter1 = LimitedSizeWriter.from(internalWriter1, context);
        Writer limitedSizeWriter2 = LimitedSizeWriter.from(internalWriter2, context);

        limitedSizeWriter1.write("0123456789");

        PebbleException thrown = assertThrows(
                PebbleException.class,
                () -> limitedSizeWriter2.write("0123456789"));

        assertTrue(thrown.getMessage().contains("19"));
    }

    // This is the exact same implementation as in EvaluationContextImpl.
    static private class TestRenderedSizeContext implements RenderedSizeContext {
        private final int maxRenderedSize;
        private final AtomicInteger charsRendered = new AtomicInteger();

        public TestRenderedSizeContext(int maxRenderedSize) {
            this.maxRenderedSize = maxRenderedSize;
        }

        @Override
        public int getMaxRenderedSize() {
            return this.maxRenderedSize;
        }

        @Override
        public int addAndGet(int delta) {
            return this.charsRendered.addAndGet(delta);
        }
    }
}
