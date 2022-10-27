/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.extension.core;

import io.pebbletemplates.error.PebbleException;
import io.pebbletemplates.extension.Filter;
import io.pebbletemplates.extension.Test;
import io.pebbletemplates.template.EvaluationContext;
import io.pebbletemplates.template.PebbleTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultFilter implements Filter {

  private final List<String> argumentNames = new ArrayList<>();

  public DefaultFilter() {
    this.argumentNames.add("default");
  }

  @Override
  public List<String> getArgumentNames() {
    return this.argumentNames;
  }

  @Override
  public Object apply(Object input, Map<String, Object> args, PebbleTemplate self,
      EvaluationContext context, int lineNumber) throws PebbleException {

    Object defaultObj = args.get("default");

    Test emptyTest = new EmptyTest();
    if (emptyTest.apply(input, new HashMap<>(), self, context, lineNumber)) {
      return defaultObj;
    }
    return input;
  }

}
