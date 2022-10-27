/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.node;

import io.pebbletemplates.error.PebbleException;
import io.pebbletemplates.extension.NodeVisitor;
import io.pebbletemplates.node.expression.Expression;
import io.pebbletemplates.extension.writer.SpecializedWriter;
import io.pebbletemplates.extension.writer.StringWriterSpecializedAdapter;
import io.pebbletemplates.template.EvaluationContextImpl;
import io.pebbletemplates.template.PebbleTemplateImpl;
import io.pebbletemplates.utils.StringUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class PrintNode extends AbstractRenderableNode {

  private Expression<?> expression;

  public PrintNode(Expression<?> expression, int lineNumber) {
    super(lineNumber);
    this.expression = expression;
  }

  @Override
  public void render(PebbleTemplateImpl self, Writer writer, EvaluationContextImpl context)
      throws IOException,
          PebbleException {
    Object var = this.expression.evaluate(self, context);
    if (var != null) {
      if (writer instanceof StringWriter) {
        new StringWriterSpecializedAdapter((StringWriter) writer).write(var);
      } else if (writer instanceof SpecializedWriter) {
        ((SpecializedWriter) writer).write(var);
      } else {
        writer.write(StringUtils.toString(var));
      }
    }
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  public Expression<?> getExpression() {
    return this.expression;
  }

  public void setExpression(Expression<?> expression) {
    this.expression = expression;
  }

}
