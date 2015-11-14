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

import com.mitchellbosecke.pebble.extension.Function;

public class RangeFunction implements Function {
	private static final String PARAM_START = "start";
	private static final String PARAM_END = "end";
	private static final String PARAM_INCREMENT = "increment";
	private final List<String> argumentNames = new ArrayList<>();

	public RangeFunction() {
		argumentNames.add(PARAM_START);
		argumentNames.add(PARAM_END);
		argumentNames.add(PARAM_INCREMENT);
	}
	
    @Override
    public List<String> getArgumentNames() {
        return argumentNames;
    }

    @Override
    public Object execute(Map<String, Object> args) {
    	Long start = (Long) args.get(PARAM_START);
    	Long end = (Long) args.get(PARAM_END);
    	Long increment = (Long) args.get(PARAM_INCREMENT);
    	if (increment == null) {
    		increment = 1L;
    	}
    	
    	List<Long> results = new ArrayList<>();
    	if (increment > 0) {
    		for (Long i = start; i <= end; i += increment) {
        		results.add(i);
        	}
    	}
    	else {
    		for (Long i = start; i >= end; i += increment) {
        		results.add(i);
        	}
    	}
    	
    	return results;
    }
}
