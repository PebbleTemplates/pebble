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


public class SyntaxException extends PebbleException {

	/**
	 * Syntax exception
	 * 
	 * @param message	Message to display 
	 * @param lineNumber	Line number of where the exception occurred
	 * @param filename	Filename of the file in which the exception occurred
	 */
	public SyntaxException(String message, int lineNumber, String filename) {
		super(message, lineNumber, filename);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3712498518512126529L;

}
