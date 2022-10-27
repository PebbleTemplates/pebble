/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.node.expression;

import io.pebbletemplates.node.Node;
import io.pebbletemplates.template.EvaluationContextImpl;
import io.pebbletemplates.template.PebbleTemplateImpl;

public interface Expression<T> extends Node {

  T evaluate(PebbleTemplateImpl self, EvaluationContextImpl context);

  /**
   * Returns the line number on which the expression is defined on.
   *
   * @return the line number on which the expression is defined on.
   */
  int getLineNumber();
}
