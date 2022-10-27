/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble.error;

import java.lang.reflect.Method;

public class ClassAccessException extends PebbleException {

  private static final long serialVersionUID = 5109892021088141417L;

  public ClassAccessException(Method method, String filename, Integer lineNumber) {
    super(null, String.format("For security reasons access to %s method is denied.", method),
        lineNumber,
        filename);
  }
}
