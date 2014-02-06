package com.mitchellbosecke.pebble.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mitchellbosecke.pebble.error.CompilationException;

/**
 * A temporary way of storing a combination of positional and named arguments.
 * The PebbleTemplateImpl class will convert the positional arguments into their
 * respective named counterparts.
 * 
 * 
 * @author Mitchell
 * 
 */
public class ArgumentMap {

	private final List<Object> positionalArguments;

	private final Map<String, Object> namedArguments;

	private ArgumentMap() {
		this.positionalArguments = new ArrayList<>();
		this.namedArguments = new HashMap<>();
	}

	public static ArgumentMap create() {
		return new ArgumentMap();
	}

	public ArgumentMap add(String name, Object value) throws CompilationException {
		if (name == null) {
			if (!namedArguments.isEmpty()) {
				throw new CompilationException(null, "Positional arguments must occur before any named arguments.");
			}
			positionalArguments.add(value);
		} else {

			getNamedArguments().put(name, value);
		}
		return this;
	}

	public List<Object> getPositionalArguments() {
		return positionalArguments;
	}

	public Map<String, Object> getNamedArguments() {
		return namedArguments;
	}

}
