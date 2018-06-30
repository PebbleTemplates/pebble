/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.node;

import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.expression.Expression;

public class NamedArgumentNode implements Node {

  private final Expression<?> value;

  private final String name;

  public NamedArgumentNode(String name, Expression<?> value) {
    this.name = name;
    this.value = value;
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  public Expression<?> getValueExpression() {
    return this.value;
  }

  public String getName() {
    return this.name;
  }

}
