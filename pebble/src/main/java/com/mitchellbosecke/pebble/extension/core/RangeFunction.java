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
import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Range function to iterate over long or a string with a length of 1.
 *
 * @author Eric Bussieres
 */
public class RangeFunction implements Function {

  public static final String FUNCTION_NAME = "range";

  private static final String PARAM_END = "end";

  private static final String PARAM_INCREMENT = "increment";

  private static final String PARAM_START = "start";

  private final List<String> argumentNames = new ArrayList<>();

  public RangeFunction() {
    this.argumentNames.add(PARAM_START);
    this.argumentNames.add(PARAM_END);
    this.argumentNames.add(PARAM_INCREMENT);
  }

  @Override
  public Object execute(Map<String, Object> args, PebbleTemplate self, EvaluationContext context,
      int lineNumber) throws PebbleException {
    Object start = args.get(PARAM_START);
    Object end = args.get(PARAM_END);
    Object increment = args.get(PARAM_INCREMENT);
    if (increment == null) {
      increment = 1L;
    } else if (!(increment instanceof Number)) {
      throw new PebbleException(null,
          "The increment of the range function must be a number " + increment,
          lineNumber, self.getName());
    }

    long incrementNum = ((Number) increment).longValue();

    List<Object> results = new ArrayList<>();
    // Iterating over Number
    if (start instanceof Number && end instanceof Number) {
      long startNum = ((Number) start).longValue();
      long endNum = ((Number) end).longValue();

      if (incrementNum > 0) {
        for (long i = startNum; i <= endNum; i += incrementNum) {
          results.add(i);
        }
      } else if (incrementNum < 0) {
        for (long i = startNum; i >= endNum; i += incrementNum) {
          results.add(i);
        }
      } else {
        throw new PebbleException(null,
            "The increment of the range function must be different than 0",
            lineNumber, self.getName());
      }
    }
    // Iterating over character
    else if (start instanceof String && end instanceof String) {
      String startStr = (String) start;
      String endStr = (String) end;
      if (startStr.length() != 1 || endStr.length() != 1) {
        throw new PebbleException(null,
            "Arguments of range function must be of type Number or String with "
                + "a length of 1", lineNumber, self.getName());
      }

      char startChar = startStr.charAt(0);
      char endChar = endStr.charAt(0);

      if (incrementNum > 0) {
        for (int i = startChar; i <= endChar; i += incrementNum) {
          results.add((char) i);
        }
      } else if (incrementNum < 0) {
        for (int i = startChar; i >= endChar; i += incrementNum) {
          results.add((char) i);
        }
      } else {
        throw new PebbleException(null,
            "The increment of the range function must be different than 0",
            lineNumber, self.getName());
      }
    } else {
      throw new PebbleException(null,
          "Arguments of range function must be of type Number or String with a "
              + "length of 1", lineNumber, self.getName());
    }

    return results;
  }

  @Override
  public List<String> getArgumentNames() {
    return this.argumentNames;
  }
}
