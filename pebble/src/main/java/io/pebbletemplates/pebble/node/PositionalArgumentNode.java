/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble.node;

import io.pebbletemplates.pebble.extension.NodeVisitor;
import io.pebbletemplates.pebble.node.expression.Expression;

public class PositionalArgumentNode implements Node {

  private final Expression<?> value;

  public PositionalArgumentNode(Expression<?> value) {
    this.value = value;
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  public Expression<?> getValueExpression() {
    return this.value;
  }

  @Override
  public String toString() {
    return this.value.toString();
  }

}
