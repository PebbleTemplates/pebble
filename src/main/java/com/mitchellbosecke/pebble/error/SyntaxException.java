/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.error;


public class SyntaxException extends PebbleException {

	/**
	 * Syntax exception
	 * 
	 * @param message	Message to display 
	 * @param lineNumber	Line number of where the exception occurred
	 * @param filename	Filename of the file in which the exception occurred
	 */
	public SyntaxException(Throwable cause, String message, int lineNumber, String filename) {
		super(cause, message, lineNumber, filename);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3712498518512126529L;

}
