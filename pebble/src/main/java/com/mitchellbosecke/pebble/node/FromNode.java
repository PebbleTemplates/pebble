package com.mitchellbosecke.pebble.node;

import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import com.mitchellbosecke.pebble.utils.Pair;
import java.io.Writer;
import java.util.List;

/**
 * From Node for
 *
 * <p>{% from "templateName" import macroName as alias %}<p>
 *
 * @author yanxiyue
 */
public class FromNode extends AbstractRenderableNode {

  private final Expression<?> fromExpression;
  private final List<Pair<String, String>> namedMacros;

  public FromNode(int lineNumber, Expression<?> fromExpression,
      List<Pair<String, String>> namedMacros) {
    super(lineNumber);
    this.fromExpression = fromExpression;
    this.namedMacros = namedMacros;
  }

  @Override
  public void render(PebbleTemplateImpl self, Writer writer, EvaluationContextImpl context) {
    String templateName = (String) fromExpression.evaluate(self, context);
    self.importNamedMacrosFromTemplate(templateName, namedMacros);
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

}
