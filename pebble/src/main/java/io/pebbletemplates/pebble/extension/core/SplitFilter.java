package io.pebbletemplates.pebble.extension.core;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.extension.Filter;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SplitFilter implements Filter {

  public static final String FILTER_NAME = "split";
  private static final String ARGUMENT_NAME_DELIMITER = "delimiter";
  private static final String ARGUMENT_NAME_LIMIT = "limit";

  private final List<String> argumentNames = new ArrayList<>();

  public SplitFilter() {
    this.argumentNames.add(ARGUMENT_NAME_DELIMITER);
    this.argumentNames.add(ARGUMENT_NAME_LIMIT);
  }

  @Override
  public Object apply(Object input, Map<String, Object> args, PebbleTemplate self,
                      EvaluationContext context, int lineNumber) throws PebbleException {
    if (input == null) {
      return null;
    }

    String delimiter = (String) args.get(ARGUMENT_NAME_DELIMITER);
    Number limit = (Number) args.get(ARGUMENT_NAME_LIMIT);
    if (delimiter == null) {
      throw new PebbleException(null, "missing delimiter parameter in split filter", lineNumber,
          self.getName());
    }

    if (limit == null) {
      return ((String) input).split(delimiter);
    }
    return ((String) input).split(delimiter, limit.intValue());
  }

  @Override
  public List<String> getArgumentNames() {
    return this.argumentNames;
  }
}
