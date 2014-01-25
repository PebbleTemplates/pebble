package com.mitchellbosecke.pebble.cache;

import java.util.concurrent.Callable;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public interface TemplateLoadingCache {
	
	public PebbleTemplate get(String key, Callable<PebbleTemplate> loadingFunction) throws PebbleException;
}
