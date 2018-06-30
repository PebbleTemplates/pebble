/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.node.expression;

import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

public class TernaryExpression implements Expression<Object> {

  private final Expression<Boolean> expression1;

  private Expression<?> expression2;

  private Expression<?> expression3;

  private final int lineNumber;

  public TernaryExpression(Expression<Boolean> expression1, Expression<?> expression2,
      Expression<?> expression3, int lineNumber, String filename) {
    this.expression1 = expression1;
    this.expression2 = expression2;
    this.expression3 = expression3;
    this.lineNumber = lineNumber;
  }

  @Override
  public Object evaluate(PebbleTemplateImpl self, EvaluationContextImpl context) {
    if (this.expression1.evaluate(self, context) != null && this.expression1
        .evaluate(self, context)) {
      return this.expression2.evaluate(self, context);
    } else {
      return this.expression3.evaluate(self, context);
    }
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  public Expression<Boolean> getExpression1() {
    return this.expression1;
  }

  public Expression<?> getExpression2() {
    return this.expression2;
  }

  public Expression<?> getExpression3() {
    return this.expression3;
  }

  public void setExpression3(Expression<?> expression3) {
    this.expression3 = expression3;
  }

  public void setExpression2(Expression<?> expression2) {
    this.expression2 = expression2;
  }

  @Override
  public int getLineNumber() {
    return this.lineNumber;
  }
}
