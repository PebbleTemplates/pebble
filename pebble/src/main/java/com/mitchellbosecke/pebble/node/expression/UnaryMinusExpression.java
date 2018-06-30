/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.node.expression;

import com.mitchellbosecke.pebble.template.EvaluationContextImpl;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import com.mitchellbosecke.pebble.utils.OperatorUtils;

public class UnaryMinusExpression extends UnaryExpression {

  @Override
  public Object evaluate(PebbleTemplateImpl self, EvaluationContextImpl context) {
    return OperatorUtils.unaryMinus(this.getChildExpression().evaluate(self, context));
  }

}
