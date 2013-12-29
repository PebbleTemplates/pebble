/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.utils;

/**
 * This class is used throughout Pebble and is designed
 * to emulate an anonymous Java method. In Java 8 this can be replaced
 * with the use of lambdas.
 * 
 * 
 * @author Mitchell
 *
 * @param <T> The return type of the method
 * @param <K> The type of arguments. Usually "List<Object>" in order to receive multiple arguments.
 */
public interface Method<T, K> {
	
	public T execute(K data);
}
