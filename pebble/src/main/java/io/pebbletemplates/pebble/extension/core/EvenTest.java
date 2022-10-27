/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble.extension.core;

import io.pebbletemplates.pebble.extension.Test;
import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import java.util.List;
import java.util.Map;

public class EvenTest implements Test {

  @Override
  public List<String> getArgumentNames() {
    return null;
  }

  @Override
  public boolean apply(Object input, Map<String, Object> args, PebbleTemplate self,
                       EvaluationContext context, int lineNumber) throws PebbleException {
    if (input == null) {
      throw new PebbleException(null, "Can not pass null value to \"even\" test.", lineNumber,
          self.getName());
    }

    if (input instanceof Integer) {
      return ((Integer) input) % 2 == 0;
    } else {
      return ((Long) input) % 2 == 0;
    }
  }
}
