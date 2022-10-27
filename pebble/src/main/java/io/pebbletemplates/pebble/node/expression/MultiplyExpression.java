/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble.node.expression;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.template.EvaluationContextImpl;
import io.pebbletemplates.pebble.template.PebbleTemplateImpl;
import io.pebbletemplates.pebble.utils.OperatorUtils;

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
