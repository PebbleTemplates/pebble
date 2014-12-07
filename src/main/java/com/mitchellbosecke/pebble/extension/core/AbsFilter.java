/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.extension.core;

import java.util.List;
import java.util.Map;

import com.mitchellbosecke.pebble.extension.Filter;

public class AbsFilter implements Filter {

    @Override
    public List<String> getArgumentNames() {
        return null;
    }

    @Override
    public Number apply(Object input, Map<String, Object> args) {
        if (input == null) {
            throw new IllegalArgumentException("Can not pass null value to \"abs\" filter.");
        }
        if (input instanceof Long) {
            return Math.abs((Long) input);
        } else {
            return Math.abs((Double) input);
        }
    }

}
