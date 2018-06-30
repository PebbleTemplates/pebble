/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.operator;

import com.mitchellbosecke.pebble.node.expression.UnaryExpression;

public class UnaryOperatorImpl implements UnaryOperator {

  private final int precedence;

  private final String symbol;

  private final Class<? extends UnaryExpression> nodeClass;

  public UnaryOperatorImpl(String symbol, int precedence,
      Class<? extends UnaryExpression> nodeClass) {
    this.symbol = symbol;
    this.precedence = precedence;
    this.nodeClass = nodeClass;
  }

  @Override
  public int getPrecedence() {
    return this.precedence;
  }

  @Override
  public String getSymbol() {
    return this.symbol;
  }

  @Override
  public Class<? extends UnaryExpression> getNodeClass() {
    return this.nodeClass;
  }
}
