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
import io.pebbletemplates.node.expression.MapExpression;
import io.pebbletemplates.template.EvaluationContextImpl;
import io.pebbletemplates.template.PebbleTemplateImpl;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EmbedNode extends AbstractRenderableNode {

  private final Expression<?> includeExpression;

  private final MapExpression mapExpression;

  private final List<BlockNode> nodes;

  public EmbedNode(int lineNumber, Expression<?> includeExpression, MapExpression mapExpression, List<BlockNode> nodes) {
    super(lineNumber);
    this.includeExpression = includeExpression;
    this.mapExpression = mapExpression;
    this.nodes = nodes;
  }

  @Override
  public void render(PebbleTemplateImpl self, Writer writer, EvaluationContextImpl context)
      throws IOException {
    String templateName = (String) this.includeExpression.evaluate(self, context);

    Map<?, ?> map = Collections.emptyMap();
    if (this.mapExpression != null) {
      map = this.mapExpression.evaluate(self, context);
    }

    if (templateName == null) {
      throw new PebbleException(
          null,
          "The template name in an embed tag evaluated to NULL. If the template name is static, make sure to wrap it in quotes.",
          this.getLineNumber(), self.getName());
    }
    self.embedTemplate(getLineNumber(), writer, context, templateName, map, nodes);
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

}
