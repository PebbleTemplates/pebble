package com.mitchellbosecke.pebble.cache;

import java.util.concurrent.Callable;

import com.mitchellbosecke.pebble.error.PebbleException;

public interface TemplateLoadingCache<K,V> {
	
	public V get(K key, Callable<V> loadingFunction) throws PebbleException;
}
