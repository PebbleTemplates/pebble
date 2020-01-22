/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.error;

import java.lang.reflect.Method;

public class MethodAccessException extends PebbleException {

  private static final long serialVersionUID = 1450432062911562507L;

  public MethodAccessException(Method method, String filename, Integer lineNumber) {
    super(null, String.format("For security reasons access to %s method is denied.", method),
        lineNumber,
        filename);
  }
}
