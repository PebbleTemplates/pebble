package com.mitchellbosecke.pebble.spring;

import com.mitchellbosecke.pebble.PebbleEngine;

public interface PebbleConfig {

	/**
	 * Return the PebbleEngine for the current web application context.
	 * 
	 * @return the PebbleEngine
	 */
	PebbleEngine getPebbleEngine();

}
