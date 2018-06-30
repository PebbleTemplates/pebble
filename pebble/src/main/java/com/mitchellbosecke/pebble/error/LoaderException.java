/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.error;

public class LoaderException extends PebbleException {

  private static final long serialVersionUID = -6445262510797040243L;

  public LoaderException(Throwable cause, String message) {
    super(cause, message);
  }

}
