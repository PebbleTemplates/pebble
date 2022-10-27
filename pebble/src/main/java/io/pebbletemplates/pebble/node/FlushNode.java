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
import io.pebbletemplates.pebble.template.EvaluationContextImpl;
import io.pebbletemplates.pebble.template.PebbleTemplateImpl;

import java.io.IOException;
import java.io.Writer;

public class FlushNode extends AbstractRenderableNode {

  public FlushNode(int lineNumber) {
    super(lineNumber);
  }

  @Override
  public void render(PebbleTemplateImpl self, Writer writer, EvaluationContextImpl context)
      throws IOException {
    writer.flush();
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

}
