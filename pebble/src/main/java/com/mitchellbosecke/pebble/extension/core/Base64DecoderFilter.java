package com.mitchellbosecke.pebble.extension.core;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * This class implements the 'base64encode' filter.
 *
 * @author Silviu Vergoti
 */
public class Base64DecoderFilter implements Filter {

  public static final String FILTER_NAME = "base64decode";

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

    String decoded = null;
    if (input instanceof String) {
      try {
        byte [] bytes = Base64.getDecoder().decode(((String) input).getBytes(StandardCharsets.UTF_8));
        decoded = new String(bytes, StandardCharsets.UTF_8);
      } catch (Exception e) {
        throw new PebbleException(e, "Please provide a correctly Base64 encoded string containing an UTF-8 string\n", lineNumber, self.getName());
      }
    } else {
      throw new PebbleException(null, "This filter applies to String\n", lineNumber, self.getName());
    }
    return decoded;
  }

}
