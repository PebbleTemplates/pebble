/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.extension;

import java.util.Map;

public interface Function extends NamedArguments {

	public Object execute(Map<String, Object> args);

}
