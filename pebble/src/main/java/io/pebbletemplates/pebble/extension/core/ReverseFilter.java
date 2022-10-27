package io.pebbletemplates.pebble.extension.core;

import io.pebbletemplates.pebble.extension.Filter;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;

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
