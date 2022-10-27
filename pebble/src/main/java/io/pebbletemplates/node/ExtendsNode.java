/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.node;

import io.pebbletemplates.extension.NodeVisitor;
import io.pebbletemplates.node.expression.Expression;
import io.pebbletemplates.template.EvaluationContextImpl;
import io.pebbletemplates.template.PebbleTemplateImpl;

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
