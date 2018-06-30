/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.extension.core;

import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Returns the first element of a collection
 *
 * @author mbosecke
 */
public class FirstFilter implements Filter {

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
      return inputString.charAt(0);
    }

    if (input.getClass().isArray()) {
      int length = Array.getLength(input);
      return length > 0 ? Array.get(input, 0) : null;
    }

    Collection<?> inputCollection = (Collection<?>) input;

    Iterator<?> iterator = inputCollection.iterator();
    if (iterator.hasNext()) {
      return iterator.next();
    }
    return null;
  }
}
