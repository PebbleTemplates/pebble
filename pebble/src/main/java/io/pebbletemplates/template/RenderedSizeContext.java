package io.pebbletemplates.template;

public interface RenderedSizeContext {
    int getMaxRenderedSize();

    int addAndGet(int delta);
}
