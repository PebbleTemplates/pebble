/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.error;

public class ClassAccessException extends PebbleException {

  private static final long serialVersionUID = 5109892021088141417L;

  public ClassAccessException(Integer lineNumber, String filename) {
    super(null, "For security reasons access to class/getClass attribute is denied.", lineNumber,
        filename);
  }
}
