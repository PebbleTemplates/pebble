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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AbbreviateFilter implements Filter {

  private final List<String> argumentNames = new ArrayList<>();

  public AbbreviateFilter() {
    this.argumentNames.add("length");
  }

  @Override
  public List<String> getArgumentNames() {
    return this.argumentNames;
  }

  @Override
  public Object apply(Object input, Map<String, Object> args, PebbleTemplate self,
      EvaluationContext context, int lineNumber) {
    if (input == null) {
      return null;
    }
    String value = (String) input;
    int maxWidth = ((Long) args.get("length")).intValue();

    if (maxWidth < 0) {
      throw new PebbleException(null,
          "Invalid argument to abbreviate filter; must be greater than zero",
          lineNumber, self.getName());
    }

    String ellipsis = "...";
    int length = value.length();

    if (length < maxWidth) {
      return value;
    }
    if (length <= 3) {
      return value;
    }
    if (maxWidth <= 3) {
      return value.substring(0, maxWidth);
    }
    return value.substring(0, Math.max(0, maxWidth - 3)) + ellipsis;
  }

}
