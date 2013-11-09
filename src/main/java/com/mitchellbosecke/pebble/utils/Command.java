/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.utils;

public interface Command<T, K> {
	
	public T execute(K data);
}
