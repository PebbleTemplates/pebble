package io.pebbletemplates.pebble.utils;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.template.RenderedSizeContext;

import java.io.IOException;
import java.io.Writer;

/**
 * A Writer that will wrap around the internal writer if the user also provided a limit
 * on the size of the rendered template. The context is shared between all the writers
 * used to evaluate a template: the one supplied by the user when calling template.evaluate
 * as well as any internally created writers e.g. when evaluating a macro.
 *
 * There will be false positives. For example if a function writes something and its output
 * is passed to a filter than we count both the output of the function and the output of the
 * filter, when we should only count the output of the filter. This is fine because the user
 * can increase the maximum allowable size accordingly. The purpose here is not to be precise
 * but to protect against abuse.
 *
 * If the limit is reached a PebbleException will be thrown.
 * If the limit is negative then no checks will be performed and the original writer used as is.
 *
 * This is thread-safe if RenderedSizeContext is thread-safe.
 */
public class LimitedSizeWriter extends Writer {

    private final Writer internalWriter;

    private final RenderedSizeContext context;

    public static Writer from(Writer internalWriter, RenderedSizeContext context) {
        if (context.getMaxRenderedSize() < 0) {
            return internalWriter;
        }

        return new LimitedSizeWriter(internalWriter, context);
    }

    private LimitedSizeWriter(Writer internalWriter, RenderedSizeContext context) {
        if (context.getMaxRenderedSize() < 0) {
            throw new IllegalArgumentException("maxRenderedSize should not be negative");
        }
        this.internalWriter = internalWriter;
        this.context = context;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        if (this.willExceedMaxChars(len)) {
            throw new PebbleException(null, String.format("Tried to write more than %d chars.", this.context.getMaxRenderedSize()));
        }
        this.internalWriter.write(cbuf, off, len);
    }

    @Override
    public void flush() throws IOException {
        this.internalWriter.flush();
    }

    @Override
    public void close() throws IOException {
        this.internalWriter.close();
    }

    @Override
    public String toString() {
        return internalWriter.toString();
    }

    // This has the side effect of incrementing the number of chars written.
    // This is necessary to maintain thread-safety, otherwise two threads might check
    // the size before writing, both checks might be fine but the resulting output
    // will be greater than the limit.
    // If internalWriter.write throws than the content written and the count of chars
    // written will get out of sync, but that's fine because at that point we don't
    // care about accuracy anymore.
    private boolean willExceedMaxChars(int charsToWrite) {
        return this.context.addAndGet(charsToWrite) > this.context.getMaxRenderedSize();
    }
}
