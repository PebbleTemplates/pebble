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
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import java.io.IOException;
import java.io.Writer;

public abstract class AbstractRenderableNode implements RenderableNode {

  private int lineNumber;

  @Override
  public abstract void render(PebbleTemplateImpl self, Writer writer, EvaluationContextImpl context)
      throws IOException;

  @Override
  public abstract void accept(NodeVisitor visitor);

  public AbstractRenderableNode() {
  }

  public AbstractRenderableNode(int lineNumber) {
    this.setLineNumber(lineNumber);
  }

  public int getLineNumber() {
    return this.lineNumber;
  }

  public void setLineNumber(int lineNumber) {
    this.lineNumber = lineNumber;
  }
}
