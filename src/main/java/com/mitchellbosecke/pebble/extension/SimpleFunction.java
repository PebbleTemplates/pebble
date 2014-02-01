/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.extension;

import java.util.List;

import com.mitchellbosecke.pebble.utils.Function;

//@FunctionalInterface
public interface SimpleFunction extends Function<Object, List<Object>> {
	
	@Override
	public Object execute(List<Object> args);

}
