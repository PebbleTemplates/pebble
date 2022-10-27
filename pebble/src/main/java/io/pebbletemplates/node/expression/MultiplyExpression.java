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
import io.pebbletemplates.utils.OperatorUtils;

public class MultiplyExpression extends BinaryExpression<Object> {

  @Override
  public Object evaluate(PebbleTemplateImpl self, EvaluationContextImpl context) {
    try {
      return OperatorUtils.multiply(this.getLeftExpression().evaluate(self, context),
          this.getRightExpression().evaluate(self, context));
    } catch (Exception ex) {
      throw new PebbleException(ex, "Could not perform multiplication", this.getLineNumber(), self
          .getName());
    }
  }
}
