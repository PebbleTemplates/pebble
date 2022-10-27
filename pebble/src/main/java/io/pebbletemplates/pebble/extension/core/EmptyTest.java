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
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class EmptyTest implements Test {

  @Override
  public List<String> getArgumentNames() {
    return null;
  }

  @Override
  public boolean apply(Object input, Map<String, Object> args, PebbleTemplate self,
                       EvaluationContext context, int lineNumber) {
    boolean isEmpty = input == null;

    if (!isEmpty && input instanceof String) {
      String value = (String) input;
      isEmpty = "".equals(value.trim());
    }

    if (!isEmpty && input instanceof Collection) {
      isEmpty = ((Collection<?>) input).isEmpty();
    }

    if (!isEmpty && input instanceof Map) {
      isEmpty = ((Map<?, ?>) input).isEmpty();
    }

    return isEmpty;
  }

}
