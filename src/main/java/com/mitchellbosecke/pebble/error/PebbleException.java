/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.error;

public class PebbleException extends Exception {

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
