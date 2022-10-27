package io.pebbletemplates.pebble.extension.core;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.extension.Filter;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * This class implements the 'base64encode' filter.
 *
 * @author Silviu Vergoti
 */
public class Base64EncoderFilter implements Filter {

  public static final String FILTER_NAME = "base64encode";

  @Override
  public List<String> getArgumentNames() {
    return null;
  }

  @Override
  public Object apply(Object input, Map<String, Object> args, PebbleTemplate self,
                      EvaluationContext context, int lineNumber) throws PebbleException {
    if (input == null) {
      return null;
    }

    return Base64.getEncoder().encodeToString(input.toString().getBytes(StandardCharsets.UTF_8));
  }

}
