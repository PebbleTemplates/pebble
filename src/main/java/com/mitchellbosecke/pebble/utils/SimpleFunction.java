/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.utils;

import java.util.List;

public interface SimpleFunction extends Function<Object, List<Object>> {
	
	public String getName();

	@Override
	public Object execute(List<Object> args);

}
