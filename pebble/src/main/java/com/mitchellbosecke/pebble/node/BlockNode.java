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
import com.mitchellbosecke.pebble.template.Block;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import java.io.IOException;
import java.io.Writer;

public class BlockNode extends AbstractRenderableNode {

  private final BodyNode body;

  private String name;

  public BlockNode(int lineNumber, String name) {
    this(lineNumber, name, null);
  }

  public BlockNode(int lineNumber, String name, BodyNode body) {
    super(lineNumber);
    this.body = body;
    this.name = name;
  }

  @Override
  public void render(final PebbleTemplateImpl self, Writer writer, EvaluationContextImpl context)
      throws IOException {
    self.block(writer, context, this.name, false);
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  public Block getBlock() {
    return new Block() {

      @Override
      public String getName() {
        return BlockNode.this.name;
      }

      @Override
      public void evaluate(PebbleTemplateImpl self, Writer writer, EvaluationContextImpl context)
          throws IOException {
        BlockNode.this.body.render(self, writer, context);
      }
    };
  }

  public BodyNode getBody() {
    return this.body;
  }

  public String getName() {
    return this.name;
  }

}
