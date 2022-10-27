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

public class AutoEscapeNode extends AbstractRenderableNode {

  private final BodyNode body;

  private final String strategy;

  private final boolean active;

  public AutoEscapeNode(int lineNumber, BodyNode body, boolean active, String strategy) {
    super(lineNumber);
    this.body = body;
    this.strategy = strategy;
    this.active = active;
  }

  @Override
  public void render(PebbleTemplateImpl self, Writer writer, EvaluationContextImpl context)
      throws IOException {
    this.body.render(self, writer, context);
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  public BodyNode getBody() {
    return this.body;
  }

  public String getStrategy() {
    return this.strategy;
  }

  public boolean isActive() {
    return this.active;
  }

}
