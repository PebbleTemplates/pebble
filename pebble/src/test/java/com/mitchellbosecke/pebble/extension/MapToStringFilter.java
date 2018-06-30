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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * There were changes in how HashMaps are serialized to strings between Java 7 and 8 so this filter
 * ensures a standardized result that we can test with.
 *
 * @author mbosecke
 */
public class MapToStringFilter implements Filter {

  @Override
  public List<String> getArgumentNames() {
    return null;
  }

  @SuppressWarnings({"unchecked"})
  @Override
  public String apply(Object input, Map<String, Object> args, PebbleTemplate self,
      EvaluationContext context, int lineNumber) {
    if (input == null) {
      return null;
    }
    Map<Object, Object> map = (Map<Object, Object>) input;

    List<String> pairs = new ArrayList<>();

    for (Entry<Object, Object> entry : map.entrySet()) {
      String pair = String.valueOf(entry.getKey()) + "=" + String.valueOf(entry.getValue());
      pairs.add(pair);
    }

    Collections.sort(pairs);

    StringBuilder result = new StringBuilder("{");
    for (String pair : pairs) {
      result.append(pair).append(", ");
    }
    result.setLength(result.length() - 2); // remove extra comma and
    // space
    result.append("}");

    return result.toString();
  }
}
