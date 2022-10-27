/*
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.node.expression;

import io.pebbletemplates.error.PebbleException;
import io.pebbletemplates.extension.NodeVisitor;
import io.pebbletemplates.template.EvaluationContextImpl;
import io.pebbletemplates.template.Hierarchy;
import io.pebbletemplates.template.PebbleTemplateImpl;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class ParentFunctionExpression implements Expression<String> {

  private final String blockName;

  private final int lineNumber;

  public ParentFunctionExpression(String blockName, int lineNumber) {
    this.blockName = blockName;
    this.lineNumber = lineNumber;
  }

  @Override
  public String evaluate(PebbleTemplateImpl self, EvaluationContextImpl context) {
    Writer writer = new StringWriter();
    try {
      Hierarchy hierarchy = context.getHierarchy();
      if (hierarchy.getParent() == null) {
        throw new PebbleException(null,
            "Can not use parent function if template does not extend another template.",
            this.lineNumber,
            self.getName());
      }
      PebbleTemplateImpl parent = hierarchy.getParent();

      hierarchy.ascend();
      parent.block(writer, context, this.blockName, true);
      hierarchy.descend();
    } catch (IOException e) {
      throw new PebbleException(e, "Could not render block [" + this.blockName + "]",
          this.getLineNumber(),
          self.getName());
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
