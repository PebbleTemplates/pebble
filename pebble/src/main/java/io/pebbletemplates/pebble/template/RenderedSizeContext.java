package io.pebbletemplates.pebble.template;

public interface RenderedSizeContext {
    int getMaxRenderedSize();

    int addAndGet(int delta);
}
