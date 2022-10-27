/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble.extension.core;

import io.pebbletemplates.pebble.extension.Filter;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Returns the last element of a collection
 *
 * @author mbosecke
 */
public class LastFilter implements Filter {

  @Override
  public List<String> getArgumentNames() {
    return null;
  }

  @Override
  public Object apply(Object input, Map<String, Object> args, PebbleTemplate self,
                      EvaluationContext context, int lineNumber) {
    if (input == null) {
      return null;
    }

    if (input instanceof String) {
      String inputString = (String) input;
      return inputString.charAt(inputString.length() - 1);
    }

    if (input.getClass().isArray()) {
      int length = Array.getLength(input);
      return length > 0 ? Array.get(input, length - 1) : null;
    }

    Collection<?> inputCollection = (Collection<?>) input;
    Object result = null;
    for (Object o : inputCollection) {
      result = o;
    }
    return result;
  }
}
