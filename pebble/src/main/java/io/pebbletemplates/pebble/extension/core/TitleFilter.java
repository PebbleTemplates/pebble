/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble.extension.core;

import io.pebbletemplates.pebble.extension.Filter;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import java.util.List;
import java.util.Map;

public class TitleFilter implements Filter {

  @Override
  public List<String> getArgumentNames() {
    return null;
  }

  @Override
  public Object apply(Object input, Map<String, Object> args, PebbleTemplate self,
                      EvaluationContext context, int lineNumber) {
    if (input == null) {
      return null;
    }
    String value = (String) input;

    if (value.length() == 0) {
      return value;
    }

    StringBuilder result = new StringBuilder();

    boolean capitalizeNextCharacter = true;

    for (char c : value.toCharArray()) {
      if (Character.isWhitespace(c)) {
        capitalizeNextCharacter = true;
      } else if (capitalizeNextCharacter) {
        c = Character.toTitleCase(c);
        capitalizeNextCharacter = false;
      }
      result.append(c);
    }

    return result.toString();
  }

}
