/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble.node.expression;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.extension.NodeVisitor;
import io.pebbletemplates.pebble.node.ArgumentsNode;
import io.pebbletemplates.pebble.template.EvaluationContextImpl;
import io.pebbletemplates.pebble.template.PebbleTemplateImpl;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class BlockFunctionExpression implements Expression<String> {

  private final Expression<?> blockNameExpression;

  private final int lineNumber;

  public BlockFunctionExpression(ArgumentsNode args, int lineNumber) {
    this.blockNameExpression = args.getPositionalArgs().get(0).getValueExpression();
    this.lineNumber = lineNumber;
  }

  @Override
  public String evaluate(PebbleTemplateImpl self, EvaluationContextImpl context) {
    Writer writer = new StringWriter();
    String blockName = (String) this.blockNameExpression.evaluate(self, context);
    try {
      self.block(writer, context, blockName, false);
    } catch (IOException e) {
      throw new PebbleException(e, "Could not render block [" + blockName + "]",
          this.getLineNumber(), self.getName());
    }
    return writer.toString();
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public int getLineNumber() {
    return this.lineNumber;
  }

}
