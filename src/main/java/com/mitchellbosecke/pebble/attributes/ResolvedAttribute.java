package com.mitchellbosecke.pebble.attributes;

import com.mitchellbosecke.pebble.error.PebbleException;

public interface ResolvedAttribute {
	Object evaluate() throws PebbleException;
}
