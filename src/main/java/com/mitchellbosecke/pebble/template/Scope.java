package com.mitchellbosecke.pebble.template;

import java.util.HashMap;

public class Scope extends HashMap<String, Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3220073691105236100L;

	private final Scope parent;

	public Scope(Scope parent) {
		this.parent = parent;
	}

	public Scope getParent() {
		return parent;
	}

}