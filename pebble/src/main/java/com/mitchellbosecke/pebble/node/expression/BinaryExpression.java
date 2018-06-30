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

public abstract class BinaryExpression<T> implements Expression<T> {

  private int lineNumber;

  public BinaryExpression() {

  }

  /**
   * Sets the left and right expressions. This expression is assumed to be defined on the same line
   * as the left expression.
   */
  public BinaryExpression(Expression<?> left, Expression<?> right) {
    this.setLeft(left);
    this.setRight(right);
    this.setLineNumber(left.getLineNumber());
  }

  private Expression<?> leftExpression;

  private Expression<?> rightExpression;

  public void setLeft(Expression<?> left) {
    this.leftExpression = left;
  }

  public void setRight(Expression<?> right) {
    this.rightExpression = right;
  }

  public Expression<?> getLeftExpression() {
    return this.leftExpression;
  }

  public Expression<?> getRightExpression() {
    return this.rightExpression;
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
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
