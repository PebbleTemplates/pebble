/*******************************************************************************
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.extension.core;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.mitchellbosecke.pebble.extension.Filter;

/**
 * Concatenates all entries of a collection or array, optionally glued together with a
 * particular character such as a comma.
 *
 * @author mbosecke
 */
public class JoinFilter implements Filter {

    private static final List<String> argumentNames = Collections.singletonList("separator");

    public JoinFilter() {
    }

    @Override
    public List<String> getArgumentNames() {
        return argumentNames;
    }

    @Override
    public Object apply(Object input, Map<String, Object> args) {
        if (input == null) {
            return null;
        }

        String separator = args.containsKey("separator") ? (String) args.get("separator") : null;
        StringBuilder builder = new StringBuilder();

        if (input instanceof Iterable<?>) {
            boolean isFirst = true;
            for (Object data : ((Iterable<?>) input)) {
                append(builder, data, isFirst ? null : separator);
                isFirst = false;
            }
        } else if (input instanceof Object[]) {
            //optimized handling of Object[] arrays (we assume that this is very common)
            Object[] array = (Object[]) input;
            for (int i = 0; i < array.length; i++) {
                append(builder, array[i], i >= 1 ? separator : null);
            }
        } else if (input.getClass().isArray()) {
            //fallback to reflection to iterate all types of arrays of primitive types
            for (int i = 0; i < Array.getLength(input); i++) {
                append(builder, Array.get(input, i), i >= 1 ? separator : null);
            }
        } else {
            throw new IllegalArgumentException("input is not an array or collection");
        }

        return builder.toString();
    }

    private void append(StringBuilder builder, Object data, String separator) {
        if (separator != null) {
            builder.append(separator);
        }

        builder.append(data);
    }
}
