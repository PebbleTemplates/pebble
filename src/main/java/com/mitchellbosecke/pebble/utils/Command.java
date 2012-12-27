package com.mitchellbosecke.pebble.utils;

public interface Command<T, K> {
	
	public T execute(K data);
}
