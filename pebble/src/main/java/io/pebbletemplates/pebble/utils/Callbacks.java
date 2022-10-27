package io.pebbletemplates.pebble.utils;

import java.io.IOException;

public class Callbacks {

    public interface PebbleConsumer<T> {
        void accept(T t) throws IOException;
    }

}
