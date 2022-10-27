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
import io.pebbletemplates.pebble.node.expression.Expression;
import io.pebbletemplates.pebble.template.EvaluationContextImpl;
import io.pebbletemplates.pebble.template.MacroAttributeProvider;
import io.pebbletemplates.pebble.template.PebbleTemplateImpl;

import java.io.Writer;

public class ImportNode extends AbstractRenderableNode {

  private final Expression<?> importExpression;
  private final String alias;

  public ImportNode(int lineNumber, Expression<?> importExpression, String alias) {
    super(lineNumber);
    this.importExpression = importExpression;
    this.alias = alias;
  }

  @Override
  public void render(PebbleTemplateImpl self, Writer writer, EvaluationContextImpl context) {
    String templateName = (String) this.importExpression.evaluate(self, context);
    if (this.alias != null) {
      self.importNamedTemplate(context, templateName, this.alias);

      // put the imported template into scope
      PebbleTemplateImpl template = self.getNamedImportedTemplate(context, this.alias);
      context.getScopeChain().put(this.alias, new MacroAttributeProvider(template));

    } else {
      self.importTemplate(context, templateName);
    }
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  public Expression<?> getImportExpression() {
    return this.importExpression;
  }

}
