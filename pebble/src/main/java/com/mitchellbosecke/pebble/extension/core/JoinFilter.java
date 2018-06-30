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
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Concatenates all entries of a collection, optionally glued together with a particular character
 * such as a comma.
 *
 * @author mbosecke
 */
public class JoinFilter implements Filter {

  private final List<String> argumentNames = new ArrayList<>();

  public JoinFilter() {
    this.argumentNames.add("separator");
  }

  @Override
  public List<String> getArgumentNames() {
    return this.argumentNames;
  }

  @Override
  public Object apply(Object input, Map<String, Object> args, PebbleTemplate self,
      EvaluationContext context, int lineNumber)
      throws PebbleException {
    if (input == null) {
      return null;
    }

    String glue = null;
    if (args.containsKey("separator")) {
      glue = (String) args.get("separator");
    }

    if (input.getClass().isArray()) {
      List<Object> items = new ArrayList<>();
      int length = Array.getLength(input);
      for (int i = 0; i < length; i++) {
        items.add(Array.get(input, i));
      }
      return this.join(items, glue);
    } else if (input instanceof Collection) {
      return this.join((Collection<?>) input, glue);
    } else {
      throw new PebbleException(null,
          "The 'join' filter expects that the input is either a collection or an array.",
          lineNumber,
          self.getName());
    }
  }

  private String join(Collection<?> inputCollection, String glue) {
    StringBuilder builder = new StringBuilder();

    boolean isFirst = true;
    for (Object entry : inputCollection) {

      if (!isFirst && glue != null) {
        builder.append(glue);
      }
      builder.append(entry);

      isFirst = false;
    }
    return builder.toString();
  }
}
