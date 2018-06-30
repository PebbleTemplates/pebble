/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.extension.core;

import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import com.mitchellbosecke.pebble.utils.OperatorUtils;
import java.util.List;
import java.util.Map;

public class MaxFunction implements Function {

  @Override
  public List<String> getArgumentNames() {
    return null;
  }

  @Override
  public Object execute(Map<String, Object> args, PebbleTemplate self, EvaluationContext context,
      int lineNumber) {
    Object min = null;

    int i = 0;

    while (args.containsKey(String.valueOf(i))) {

      Object candidate = args.get(String.valueOf(i));
      i++;

      if (min == null) {
        min = candidate;
        continue;
      }
      if (OperatorUtils.gt(candidate, min)) {
        min = candidate;
      }

    }
    return min;

  }

}
