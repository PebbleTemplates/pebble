/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.node.expression;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

import static com.mitchellbosecke.pebble.utils.TypeUtils.compatibleCast;

public class AndExpression extends BinaryExpression<Boolean> {

  @SuppressWarnings("unchecked")
  @Override
  public Boolean evaluate(PebbleTemplateImpl self, EvaluationContextImpl context) {
    Expression<Boolean> leftExpression = (Expression<Boolean>) this.getLeftExpression();
    boolean left = this.evaluateExpression(self, context, leftExpression);
    if (left) {
      Expression<Boolean> rightExpression = (Expression<Boolean>) this.getRightExpression();
      return this.evaluateExpression(self, context, rightExpression);
    }
    return false;
  }

  private boolean evaluateExpression(PebbleTemplateImpl self, EvaluationContextImpl context,
      Expression<Boolean> expression) {
    Boolean evaluatedExpression = compatibleCast(expression.evaluate(self, context), Boolean.class);

    if (evaluatedExpression == null) {
      if (context.isStrictVariables()) {
        throw new PebbleException(null,
            "null value used in and operator and strict variables is set to true",
            this.getLineNumber(),
            self.getName());
      }
      return false;
    }
    return evaluatedExpression;
  }
}
