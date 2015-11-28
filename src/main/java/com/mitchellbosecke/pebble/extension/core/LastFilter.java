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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mitchellbosecke.pebble.extension.Filter;

/**
 * Returns the last element of a collection
 * 
 * @author mbosecke
 *
 */
public class LastFilter implements Filter {

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
            return inputString.charAt(inputString.length() - 1);
        }

        if(input.getClass().isArray()) {
            int length = Array.getLength(input);
            return length > 0 ? Array.get(input, length - 1) : null;
        }
        
        @SuppressWarnings("unchecked")
        Collection<Object> inputCollection = (Collection<Object>) input;
        Object result = null;
        Iterator<Object> iterator = inputCollection.iterator();
        while(iterator.hasNext()){
            result = iterator.next();
        }
        return result;
    }
}
