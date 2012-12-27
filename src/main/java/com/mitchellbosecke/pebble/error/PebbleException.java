/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2012 Mitchell Bosecke.
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 
 * Unported License. To view a copy of this license, visit 
 * http://creativecommons.org/licenses/by-sa/3.0/
 ******************************************************************************/
package com.mitchellbosecke.pebble.error;

public class PebbleException extends RuntimeException {

	private static final long serialVersionUID = -2855774187093732189L;

	protected Integer lineNumber;
	protected String filename;
	protected String message;
	protected PebbleException previous;

	public PebbleException(String message) {
		this(message, null, null);
	}

	public PebbleException(String message, Integer lineNumber, String filename) {
		this(message, lineNumber, filename, null);
	}

	public PebbleException(String message, Integer lineNumber, String filename,
			PebbleException previous) {
		super(String.format("%s(%s:%d)", message, filename, lineNumber));
		this.message = message;
		this.lineNumber = lineNumber;
		this.filename = filename;
		this.previous = previous;
	}

}
