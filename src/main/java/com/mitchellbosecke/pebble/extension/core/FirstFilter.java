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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.mitchellbosecke.pebble.extension.Filter;

/**
 * Returns the first element of a collection
 * 
 * @author mbosecke
 *
 */
public class FirstFilter implements Filter {

    @Override
    public List<String> getArgumentNames() {
        return null;
    }

    @Override
    public Object apply(Object input, Map<String, Object> args) {
        if (input == null) {
            return null;
        }
        
        if(input instanceof String){
            String inputString = (String)input;
            return inputString.charAt(0);
        }

        if(input.getClass().isArray()) {
            int length = Array.getLength(input);
            return length > 0 ? Array.get(input, 0) : null;
        }
        
        Collection<?> inputCollection = (Collection<?>) input;
        return inputCollection.iterator().next();
    }
}
