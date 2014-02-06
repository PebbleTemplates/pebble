package com.mitchellbosecke.pebble.template;

import java.util.HashMap;

/**
 * A scope is a map of variables. If a scope contains a reference to a parent
 * scope, variable lookup will delegate to the parent if the current scope does
 * not contain the variable.
 * 
 * @author Mitchell
 * 
 */
public class Scope extends HashMap<String, Object> {

	private static final long serialVersionUID = -3220073691105236100L;

	private final Scope parent;

	public Scope(Scope parent) {
		this.parent = parent;
	}

	public Scope getParent() {
		return parent;
	}

}