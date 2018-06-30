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

public abstract class UnaryExpression implements Expression<Object> {

  private Expression<?> childExpression;

  private int lineNumber;

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  public Expression<?> getChildExpression() {
    return this.childExpression;
  }

  public void setChildExpression(Expression<?> childExpression) {
    this.childExpression = childExpression;
  }


  /**
   * Sets the line number on which the expression is defined on.
   *
   * @param lineNumber the line number on which the expression is defined on.
   */
  public void setLineNumber(int lineNumber) {
    this.lineNumber = lineNumber;
  }

  @Override
  public int getLineNumber() {
    return this.lineNumber;
  }


}
