/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.extension;

import io.pebbletemplates.error.PebbleException;
import io.pebbletemplates.template.EvaluationContext;
import io.pebbletemplates.template.PebbleTemplate;
import java.util.Map;

public interface Test extends NamedArguments {

  boolean apply(Object input, Map<String, Object> args, PebbleTemplate self,
      EvaluationContext context, int
      lineNumber) throws PebbleException;
}
