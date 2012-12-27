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
