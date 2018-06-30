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

public interface BinaryOperator {

  int getPrecedence();

  String getSymbol();

  Class<? extends BinaryExpression<?>> getNodeClass();

  Associativity getAssociativity();

}
