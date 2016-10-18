/*******************************************************************************
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.extension.core;

import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

import java.util.List;
import java.util.Map;

public class TrimFilter implements Filter {

    @Override
    public List<String> getArgumentNames() {
        return null;
    }

    @Override
    public Object apply(Object input, Map<String, Object> args, PebbleTemplateImpl self, int lineNumber) {
        if (input == null) {
            return null;
        }
        String str = (String) input;
        return str.trim();
    }

}
