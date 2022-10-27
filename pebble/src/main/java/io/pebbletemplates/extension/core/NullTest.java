/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.extension.core;

import io.pebbletemplates.extension.Test;
import io.pebbletemplates.template.EvaluationContext;
import io.pebbletemplates.template.PebbleTemplate;
import java.util.List;
import java.util.Map;

public class NullTest implements Test {

  @Override
  public List<String> getArgumentNames() {
    return null;
  }

  @Override
  public boolean apply(Object input, Map<String, Object> args, PebbleTemplate self,
      EvaluationContext context, int
      lineNumber) {
    return input == null;
  }
}
