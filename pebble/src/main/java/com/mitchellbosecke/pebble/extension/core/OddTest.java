/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.extension.core;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.Test;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.util.List;
import java.util.Map;

public class OddTest implements Test {

  @Override
  public List<String> getArgumentNames() {
    return null;
  }

  @Override
  public boolean apply(Object input, Map<String, Object> args, PebbleTemplate self,
      EvaluationContext context, int lineNumber) throws PebbleException {
    if (input == null) {
      throw new IllegalArgumentException("Can not pass null value to \"odd\" test.");
    }
    EvenTest evenTest = new EvenTest();
    return !evenTest.apply(input, args, self, context, lineNumber);
  }
}
