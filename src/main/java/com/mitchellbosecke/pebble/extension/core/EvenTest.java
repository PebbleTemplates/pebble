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

import com.mitchellbosecke.pebble.extension.Test;

public class EvenTest implements Test {
	
	@Override
	public List<String> getArgumentNames() {
		return null;
	}

	@Override
	public boolean apply(Object input, Map<String,Object> args) {
		if (input == null) {
			throw new IllegalArgumentException("Can not pass null value to \"even\" test.");
		}
		Long num = null;
		if(input instanceof Integer){
			num = Long.valueOf((Integer)input);
		}else{
			num = (Long) input;
		}
		return (num % 2 == 0);
	}
}
