/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.node;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.node.expression.MapExpression;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Map;

public class IncludeNode extends AbstractRenderableNode {

  private final Expression<?> includeExpression;

  private final MapExpression mapExpression;

  public IncludeNode(int lineNumber, Expression<?> includeExpression, MapExpression mapExpression) {
    super(lineNumber);
    this.includeExpression = includeExpression;
    this.mapExpression = mapExpression;
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
          "The template name in an include tag evaluated to NULL. If the template name is static, make sure to wrap it in quotes.",
          this.getLineNumber(), self.getName());
    }
    self.includeTemplate(writer, context, templateName, map);
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  public Expression<?> getIncludeExpression() {
    return this.includeExpression;
  }

}
