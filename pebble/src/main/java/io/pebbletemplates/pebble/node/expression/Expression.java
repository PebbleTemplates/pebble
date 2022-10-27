/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble.node.expression;

import io.pebbletemplates.pebble.node.Node;
import io.pebbletemplates.pebble.template.EvaluationContextImpl;
import io.pebbletemplates.pebble.template.PebbleTemplateImpl;

public interface Expression<T> extends Node {

  T evaluate(PebbleTemplateImpl self, EvaluationContextImpl context);

  /**
   * Returns the line number on which the expression is defined on.
   *
   * @return the line number on which the expression is defined on.
   */
  int getLineNumber();
}
