/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble.node.expression;

import io.pebbletemplates.pebble.extension.NodeVisitor;
import io.pebbletemplates.pebble.template.EvaluationContextImpl;
import io.pebbletemplates.pebble.template.PebbleTemplateImpl;

public class LiteralIntegerExpression implements Expression<Integer> {

  private final Integer value;
  private final int lineNumber;

  public LiteralIntegerExpression(Integer value, int lineNumber) {
    this.value = value;
    this.lineNumber = lineNumber;
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public Integer evaluate(PebbleTemplateImpl self, EvaluationContextImpl context) {
    return this.value;
  }

  @Override
  public int getLineNumber() {
    return this.lineNumber;
  }

}
