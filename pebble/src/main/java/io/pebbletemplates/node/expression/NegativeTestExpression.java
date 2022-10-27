/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.node.expression;

import io.pebbletemplates.template.EvaluationContextImpl;
import io.pebbletemplates.template.PebbleTemplateImpl;

public class NegativeTestExpression extends PositiveTestExpression {

  @Override
  public Object evaluate(PebbleTemplateImpl self, EvaluationContextImpl context) {
    return !((Boolean) super.evaluate(self, context));
  }
}
