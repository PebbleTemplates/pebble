package com.mitchellbosecke.pebble.extension.core;

import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LengthFilter implements Filter {

  @Override
  public List<String> getArgumentNames() {
    return null;
  }

  @Override
  public Object apply(Object input, Map<String, Object> args, PebbleTemplate self,
      EvaluationContext context, int
      lineNumber) {
    if (input == null) {
      return 0;
    }
    if (input instanceof String) {
      return ((String) input).length();
    } else if (input instanceof Collection) {
      return ((Collection<?>) input).size();
    } else if (input.getClass().isArray()) {
      return Array.getLength(input);
    } else if (input instanceof Map) {
      return ((Map<?, ?>) input).size();
    } else if (input instanceof Iterable) {
      Iterator<?> it = ((Iterable<?>) input).iterator();
      int size = 0;
      while (it.hasNext()) {
        it.next();
        size++;
      }
      return size;
    } else if (input instanceof Iterator) {
      Iterator<?> it = (Iterator<?>) input;
      int size = 0;
      while (it.hasNext()) {
        it.next();
        size++;
      }
      return size;
    } else {
      return 0;
    }
  }

}
