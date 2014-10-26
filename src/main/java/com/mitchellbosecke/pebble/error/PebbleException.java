/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
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

    public PebbleException(Throwable cause, String message) {
        this(cause, message, null, null);
    }

    public PebbleException(Throwable cause, String message, Integer lineNumber, String filename) {
        super(String.format("%s (%s:%s)", message, filename == null ? "?" : filename,
                lineNumber == null ? "?" : String.valueOf(lineNumber)), cause);
        this.message = message;
        this.lineNumber = lineNumber;
        this.filename = filename;
    }

}
