/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.extension.core;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.mitchellbosecke.pebble.extension.Filter;

public class SortFilter implements Filter {

    @Override
    public List<String> getArgumentNames() {
        return null;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public List<Comparable> apply(Object input, Map<String, Object> args) {
        if (input == null) {
            return null;
        }
        List<Comparable> collection = (List<Comparable>) input;
        Collections.sort(collection);
        return collection;
    }

}
