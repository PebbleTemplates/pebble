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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MergeFilter implements Filter {

  public static final String FILTER_NAME = "merge";

  private final List<String> argumentNames = new ArrayList<>();

  public MergeFilter() {
    this.argumentNames.add("items");
  }

  @Override
  public List<String> getArgumentNames() {
    return this.argumentNames;
  }

  @Override
  public Object apply(Object input, Map<String, Object> args, PebbleTemplate self,
      EvaluationContext context, int lineNumber) throws PebbleException {
    Object items = args.get("items");
    if (input == null) {
      if (items == null) {
        throw new PebbleException(null, "The two arguments to be merged are null", lineNumber,
            self.getName());
      } else {
        return items;
      }
    } else if (items == null) {
      return input;
    }
    // left hand side argument defines resulting type
    if (input instanceof Map) {
      return this.mergeAsMap((Map<?, ?>) input, items);
    } else if (input instanceof List) {
      return this.mergeAsList((List<?>) input, items, lineNumber, self);
    } else if (input.getClass().isArray()) {
      return this.mergeAsArray(input, items, lineNumber, self);
    } else {
      throw new PebbleException(null, "The object being filtered is not a Map/List/Array",
          lineNumber, self.getName());
    }
  }

  private Object mergeAsMap(Map<?, ?> arg1, Object arg2) {
    Map<Object, Object> output;
    if (arg2 instanceof Map) {
      Map<?, ?> collection2 = (Map<?, ?>) arg2;
      output = new HashMap<>(arg1.size() + collection2.size() + 16);
      output.putAll(arg1);
      output.putAll(collection2);
    } else if (arg2 instanceof List) {
      List<?> collection2 = (List<?>) arg2;
      output = new HashMap<>(arg1.size() + collection2.size() + 16);
      output.putAll(arg1);
      for (Object o : collection2) {
        output.put(o, o);
      }
    } else {
      throw new UnsupportedOperationException(
          "Currently, only Maps and Lists can be merged with a Map. Arg2: " + arg2.getClass()
              .getName());
    }
    return output;
  }

  private Object mergeAsList(List<?> arg1, Object arg2, int lineNumber, PebbleTemplate self)
      throws PebbleException {
    List<Object> output;
    if (arg2 instanceof Map) {
      Map<?, ?> collection2 = (Map<?, ?>) arg2;
      output = new ArrayList<>(arg1.size() + collection2.size() + 16);
      output.addAll(arg1);
      output.addAll(collection2.entrySet());
    } else if (arg2 instanceof List) {
      List<?> collection2 = (List<?>) arg2;
      output = new ArrayList<>(arg1.size() + collection2.size() + 16);
      output.addAll(arg1);
      output.addAll(collection2);
    } else {
      throw new PebbleException(null,
          "Currently, only Maps and Lists can be merged with a List. Arg2: " + arg2.getClass()
              .getName(), lineNumber, self.getName());
    }
    return output;
  }

  private Object mergeAsArray(Object arg1, Object arg2, int lineNumber, PebbleTemplate self)
      throws PebbleException {
    Class<?> arg1Class = arg1.getClass().getComponentType();
    Class<?> arg2Class = arg2.getClass().getComponentType();
    if (!arg1Class.equals(arg2Class)) {
      throw new PebbleException(null,
          "Currently, only Arrays of the same component class can be merged. Arg1: " + arg1Class
              .getName()
              + ", Arg2: " + arg2Class.getName(), lineNumber, self.getName());
    }
    Object output = Array.newInstance(arg1Class, Array.getLength(arg1) + Array.getLength(arg2));
    System.arraycopy(arg1, 0, output, 0, Array.getLength(arg1));
    System.arraycopy(arg2, 0, output, Array.getLength(arg1), Array.getLength(arg2));
    return output;
  }

}
