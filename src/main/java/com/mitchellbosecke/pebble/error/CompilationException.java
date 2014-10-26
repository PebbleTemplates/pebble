/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.error;

public class CompilationException extends PebbleException {

    /**
     * Syntax exception
     * 
     * @param message
     *            Message to display
     * @param lineNumber
     *            Line number of where the exception occurred
     * @param filename
     *            Filename of the file in which the exception occurred
     */
    public CompilationException(Throwable cause, String message) {
        super(cause, message);
    }

    /**
	 * 
	 */
    private static final long serialVersionUID = -3712498518512126529L;

}
