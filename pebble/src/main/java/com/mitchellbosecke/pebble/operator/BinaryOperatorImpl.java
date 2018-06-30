/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.operator;

import com.mitchellbosecke.pebble.node.expression.BinaryExpression;

public class BinaryOperatorImpl implements BinaryOperator {

  private final int precedence;

  private final String symbol;

  private final Class<? extends BinaryExpression<?>> nodeClass;

  private final Associativity associativity;

  public BinaryOperatorImpl(String symbol, int precedence,
      Class<? extends BinaryExpression<?>> nodeClass,
      Associativity associativity) {
    this.symbol = symbol;
    this.precedence = precedence;
    this.nodeClass = nodeClass;
    this.associativity = associativity;
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
  public Class<? extends BinaryExpression<?>> getNodeClass() {
    return this.nodeClass;
  }

  @Override
  public Associativity getAssociativity() {
    return this.associativity;
  }
}
