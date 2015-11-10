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

import com.mitchellbosecke.pebble.extension.Function;

public class SizeFunction implements Function {
    private static final String COLLECTION_PARAM = "collection";
    private final List<String> argumentNames = new ArrayList<>();

    public SizeFunction() {
        this.argumentNames.add(COLLECTION_PARAM);
    }

    @Override
    public Object execute(Map<String, Object> args) {
        Object col = args.get(COLLECTION_PARAM);
        int size = 0;
        if (col != null) {
            if (col instanceof Collection) {
                size = ((Collection<?>) col).size();
            }
            else if (col instanceof Map) {
                size = ((Map<?, ?>) col).size();
            }
        }

        return size;
    }

    @Override
    public List<String> getArgumentNames() {
        return this.argumentNames;
    }

}
