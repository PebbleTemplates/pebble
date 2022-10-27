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
import io.pebbletemplates.pebble.template.EvaluationContextImpl;
import io.pebbletemplates.pebble.template.PebbleTemplateImpl;

import java.io.Writer;

public class ExtendsNode extends AbstractRenderableNode {

  Expression<?> parentExpression;

  public ExtendsNode(int lineNumber, Expression<?> parentExpression) {
    super(lineNumber);
    this.parentExpression = parentExpression;
  }

  @Override
  public void render(final PebbleTemplateImpl self, Writer writer,
                     final EvaluationContextImpl context) {
    self.setParent(context, (String) this.parentExpression.evaluate(self, context));
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  public Expression<?> getParentExpression() {
    return this.parentExpression;
  }
}
