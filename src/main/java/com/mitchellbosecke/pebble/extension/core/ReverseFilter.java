package com.mitchellbosecke.pebble.extension.core;
import com.mitchellbosecke.pebble.extension.Filter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Revert the order of an input list
 *
 * @author Andrea La Scola
 */
public class ReverseFilter implements Filter {

    @Override
    public List<String> getArgumentNames() {
        return null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public List apply(Object input, Map<String, Object> args) {
        if (input == null) {
            return null;
        }
        List collection = (List) input;
        Collections.reverse(collection);
        return collection;
    }
}
