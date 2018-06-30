/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.extension;

import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

/**
 * Pretty-printing of java arrays
 */
public class ArrayToStringFilter implements Filter {

  @Override
  public List<String> getArgumentNames() {
    return null;
  }

  @Override
  public String apply(Object input, Map<String, Object> args, PebbleTemplate self,
      EvaluationContext context, int lineNumber) {
    if (input == null) {
      return null;
    }

    StringBuilder result = new StringBuilder("[");
    int length = Array.getLength(input);
    for (int i = 0; i < length; i++) {
      if (i > 0) {
        result.append(",");
      }
      result.append(Array.get(input, i));
    }
    result.append("]");

    return result.toString();
  }

}
