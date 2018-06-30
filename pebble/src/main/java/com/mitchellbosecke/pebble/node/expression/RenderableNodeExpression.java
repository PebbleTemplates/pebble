/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.node.expression;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.node.RenderableNode;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * This class wraps a {@link RenderableNode} into an expression. This is used by the filter TAG to
 * apply a filter to large chunk of template which is contained within a renderable node.
 *
 * @author mbosecke
 */
public class RenderableNodeExpression extends UnaryExpression {

  private final RenderableNode node;

  private final int lineNumber;

  public RenderableNodeExpression(RenderableNode node, int lineNumber) {
    this.node = node;
    this.lineNumber = lineNumber;
  }

  @Override
  public Object evaluate(PebbleTemplateImpl self, EvaluationContextImpl context) {
    Writer writer = new StringWriter();
    try {
      this.node.render(self, writer, context);
    } catch (IOException e) {
      throw new PebbleException(e, "Error occurred while rendering node", this.getLineNumber(),
          self.getName());
    }
    return writer.toString();
  }

  @Override
  public int getLineNumber() {
    return this.lineNumber;
  }

}
