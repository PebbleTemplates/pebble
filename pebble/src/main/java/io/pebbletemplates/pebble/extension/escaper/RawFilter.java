/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble.extension.escaper;

import io.pebbletemplates.pebble.extension.Filter;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import java.util.List;
import java.util.Map;

public class RawFilter implements Filter {

  public List<String> getArgumentNames() {
    return null;
  }

  @Override
  public Object apply(Object inputObject, Map<String, Object> args, PebbleTemplate self,
                      EvaluationContext context, int lineNumber) {
    return inputObject == null ? null : new SafeString(inputObject.toString());
  }

}
