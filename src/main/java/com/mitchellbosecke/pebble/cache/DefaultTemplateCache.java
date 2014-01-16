package com.mitchellbosecke.pebble.cache;

import java.util.HashMap;
import java.util.Map;

import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class DefaultTemplateCache implements Cache<String,PebbleTemplate>{
	
	private final Map<String,PebbleTemplate> cache = new HashMap<>();

	@Override
	public void put(String key, PebbleTemplate value) {
		cache.put(key, value);
		
	}

	@Override
	public PebbleTemplate get(String key) {
		return cache.get(key);
	}


}
