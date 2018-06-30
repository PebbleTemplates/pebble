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

public class LiteralDoubleExpression implements Expression<Double> {

  private final Double value;

  private final int lineNumber;

  public LiteralDoubleExpression(Double value, int lineNumber) {
    this.value = value;
    this.lineNumber = lineNumber;
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public Double evaluate(PebbleTemplateImpl self, EvaluationContextImpl context) {
    return this.value;
  }

  @Override
  public int getLineNumber() {
    return this.lineNumber;
  }

  @Override
  public String toString() {
    return this.value.toString();
  }

}
