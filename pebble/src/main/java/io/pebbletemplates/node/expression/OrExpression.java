/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.node.expression;

import io.pebbletemplates.error.PebbleException;
import io.pebbletemplates.template.EvaluationContextImpl;
import io.pebbletemplates.template.PebbleTemplateImpl;

import static io.pebbletemplates.utils.TypeUtils.compatibleCast;

public class OrExpression extends BinaryExpression<Boolean> {

  @SuppressWarnings("unchecked")
  @Override
  public Boolean evaluate(PebbleTemplateImpl self, EvaluationContextImpl context) {
    Expression<Boolean> leftExpression = (Expression<Boolean>) this.getLeftExpression();
    boolean left = this.evaluateExpression(self, context, leftExpression);
    if (!left) {
      Expression<Boolean> rightExpression = (Expression<Boolean>) this.getRightExpression();
      return this.evaluateExpression(self, context, rightExpression);
    }
    return true;
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
