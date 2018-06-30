/*
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.node;

import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import java.io.Writer;

public class SetNode extends AbstractRenderableNode {

  private final String name;

  private final Expression<?> value;

  public SetNode(int lineNumber, String name, Expression<?> value) {
    super(lineNumber);
    this.name = name;
    this.value = value;
  }

  @Override
  public void render(PebbleTemplateImpl self, Writer writer, EvaluationContextImpl context) {
    context.getScopeChain().set(this.name, this.value.evaluate(self, context));
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  public Expression<?> getValue() {
    return this.value;
  }

  public String getName() {
    return this.name;
  }

}
