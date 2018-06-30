/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.error;

public class ParserException extends PebbleException {

  private static final long serialVersionUID = -3712498518512126529L;

  public ParserException(Throwable cause, String message, int lineNumber, String filename) {
    super(cause, message, lineNumber, filename);
  }

}
