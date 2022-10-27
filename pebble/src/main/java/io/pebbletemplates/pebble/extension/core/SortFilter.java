/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble.extension.core;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.extension.Filter;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SortFilter implements Filter {

  @Override
  public List<String> getArgumentNames() {
    return null;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public List<Comparable> apply(Object input, Map<String, Object> args, PebbleTemplate self,
                                EvaluationContext context, int lineNumber) {
    if (input == null) {
      return null;
    }

    List<Comparable> collection;
    if (input instanceof List) {
      collection = (List<Comparable>) input;
    } else if (input instanceof Comparable[]) {
      collection = Arrays.asList((Comparable[]) input);
    } else {
      throw new PebbleException(null, "Unsupported input type for sort filter", lineNumber,
          self.getName());
    }
    Collections.sort(collection);
    return collection;
  }

}
