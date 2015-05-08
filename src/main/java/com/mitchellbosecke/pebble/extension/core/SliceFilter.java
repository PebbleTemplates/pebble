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
import java.util.List;
import java.util.Map;

import com.mitchellbosecke.pebble.extension.Filter;

public class SliceFilter implements Filter {

    private final List<String> argumentNames = new ArrayList<>();

    public SliceFilter() {
        argumentNames.add("fromIndex");
        argumentNames.add("toIndex");
    }

    @Override
    public List<String> getArgumentNames() {
        return argumentNames;
    }

    @Override
    public Object apply(Object input, Map<String, Object> args) {
        if (input == null) return null;
        // argument parsing
        Object argFrom = args.get("fromIndex");
        if (!(argFrom instanceof Long)) 
        	throw new IllegalArgumentException("Argument fromIndex must be a number. Actual type: " + (argFrom == null ? "null" : argFrom.getClass().getName()));
        Object argTo = args.get("toIndex");
        if (!(argTo instanceof Long)) 
        	throw new IllegalArgumentException("Argument toIndex must be a number. Actual type: " + (argTo == null ? "null" : argTo.getClass().getName()));
        int from = ((Long) argFrom).intValue();
        int to = ((Long) argTo).intValue();
        if (from < 0) throw new IllegalArgumentException("fromIndex must be greater than 0");
        if (from >= to) throw new IllegalArgumentException("toIndex must be greater than fromIndex");
        // slice input
        if (input instanceof List) {
        	List<?> value = (List<?>) input;
            int length = value.size();
            if (to > length) throw new IllegalArgumentException("toIndex must be smaller than input size: " + length);
            //FIXME maybe sublist() is not the best option due to its implementation?
        	return value.subList(from, to);
        } else if (input.getClass().isArray()) {
        	//TODO support for arrays
        	throw new UnsupportedOperationException("Pending implementation");
        } else if (input instanceof String) {
            String value = (String) input;
            int length = value.length();
            if (to > length) throw new IllegalArgumentException("toIndex must be smaller than input size: " + length);
            return value.substring(from, to);
        } else {
        	throw new IllegalArgumentException("Slice filter can only be applied to String, List and array inputs. Actual type was: " + input.getClass().getName());
        }
    }

}
