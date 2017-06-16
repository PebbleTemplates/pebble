package com.mitchellbosecke.pebble.extension;

import com.mitchellbosecke.pebble.error.PebbleException;

public interface ResolvedAttribute {
	Object get() throws PebbleException;
}
