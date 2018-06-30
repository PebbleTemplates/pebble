/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.extension;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.util.List;
import java.util.Map;

/**
 * Pretty-printing of List, implementation independent
 */
public class ListToStringFilter implements Filter {

  @Override
  public List<String> getArgumentNames() {
    return null;
  }

  @Override
  public String apply(Object input, Map<String, Object> args, PebbleTemplate self,
      EvaluationContext context, int lineNumber) throws PebbleException {
    if (input == null) {
      return null;
    }

    if (!(input instanceof List)) {
      throw new PebbleException(null,
          "The 'listToString' filter expects that the input to be a list.", lineNumber,
          self.getName());
    }

    List<?> inputList = (List<?>) input;
    StringBuilder result = new StringBuilder("[");
    for (int i = 0; i < inputList.size(); i++) {
      if (i > 0) {
        result.append(",");
      }
      result.append(inputList.get(i));
    }
    result.append("]");

    return result.toString();
  }

}
