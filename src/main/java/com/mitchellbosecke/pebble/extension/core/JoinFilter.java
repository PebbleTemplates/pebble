/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.extension.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.mitchellbosecke.pebble.extension.Filter;

/**
 * Concatenates all entries of a collection, optionally glued together with a
 * particular character such as a comma.
 * 
 * @author mbosecke
 *
 */
public class JoinFilter implements Filter {

    private final List<String> argumentNames = new ArrayList<>();

    public JoinFilter() {
        argumentNames.add("separator");
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

        @SuppressWarnings("unchecked")
        Collection<Object> inputCollection = (Collection<Object>) input;

        StringBuilder builder = new StringBuilder();

        String glue = null;
        if (args.containsKey("separator")) {
            glue = (String) args.get("separator");
        }

        boolean isFirst = true;
        for (Object entry : inputCollection) {

            if (!isFirst && glue != null) {
                builder.append(glue);
            }
            builder.append(entry);

            isFirst = false;
        }
        return builder.toString();
    }
}
