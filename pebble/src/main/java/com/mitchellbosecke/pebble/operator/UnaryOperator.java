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

public interface UnaryOperator {

  int getPrecedence();

  String getSymbol();

  Class<? extends UnaryExpression> getNodeClass();

}
