/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble.operator;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.node.expression.BinaryExpression;
import io.pebbletemplates.pebble.node.expression.FilterExpression;
import io.pebbletemplates.pebble.node.expression.NegativeTestExpression;
import io.pebbletemplates.pebble.node.expression.PositiveTestExpression;
import java.util.function.Supplier;

public class BinaryOperatorImpl implements BinaryOperator {

  private final int precedence;

  private final String symbol;

  private final Supplier<? extends BinaryExpression<?>> nodeSupplier;

  private final BinaryOperatorType type;

  private final Associativity associativity;

  /**
   * This constuctor left for backward compatibility with custom extensions
   */
  public BinaryOperatorImpl(String symbol, int precedence,
      Class<? extends BinaryExpression<?>> nodeClass,
      Associativity associativity) {
    this(symbol, precedence, () -> {
      try {
        return nodeClass.newInstance();
      } catch (InstantiationException | IllegalAccessException e) {
        throw new PebbleException(e, "Error instantiating class " + nodeClass.getName());
      }
    }, getDefaultType(nodeClass), associativity);
  }

  /**
   * This constuctor allows you to completely control the instantiation of the expression class
   */
  public BinaryOperatorImpl(String symbol, int precedence,
      Supplier<? extends BinaryExpression<?>> nodeSupplier,
      BinaryOperatorType type,
      Associativity associativity) {
    this.symbol = symbol;
    this.precedence = precedence;
    this.nodeSupplier = nodeSupplier;
    this.type = type;
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
  public BinaryExpression<?> createInstance() {
    return this.nodeSupplier.get();
  }

  @Override
  public BinaryOperatorType getType() {
    return this.type;
  }

  @Override
  public Associativity getAssociativity() {
    return this.associativity;
  }

  private static BinaryOperatorType getDefaultType(Class<? extends BinaryExpression<?>> nodeClass) {
    if (FilterExpression.class.equals(nodeClass)) {
      return BinaryOperatorType.FILTER;
    } else if (PositiveTestExpression.class.equals(nodeClass) || NegativeTestExpression.class
        .equals(nodeClass)) {
      return BinaryOperatorType.TEST;
    } else {
      return BinaryOperatorType.NORMAL;
    }
  }
}
