package com.mitchellbosecke.pebble.extension.core;

import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Revert the order of an input list
 *
 * @author Andrea La Scola
 */
public class ReverseFilter implements Filter {

  @Override
  public List<String> getArgumentNames() {
    return null;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public Object apply(Object input, Map<String, Object> args, PebbleTemplate self,
      EvaluationContext context, int lineNumber) {
    if (input == null) {
      return null;
    }
    List collection = (List) input;
    Collections.reverse(collection);
    return collection;
  }
}
