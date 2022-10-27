/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble.operator;

import io.pebbletemplates.pebble.node.expression.BinaryExpression;

public interface BinaryOperator {

  int getPrecedence();

  String getSymbol();

  BinaryExpression<?> createInstance();

  BinaryOperatorType getType();

  Associativity getAssociativity();

}
