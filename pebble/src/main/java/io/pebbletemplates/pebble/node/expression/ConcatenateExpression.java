package io.pebbletemplates.pebble.node.expression;

import io.pebbletemplates.pebble.template.EvaluationContextImpl;
import io.pebbletemplates.pebble.template.PebbleTemplateImpl;

/**
 * Expression which implements the string concatenation.
 *
 * @author Thomas Hunziker
 */
public class ConcatenateExpression extends BinaryExpression<Object> {

  public ConcatenateExpression() {
  }

  public ConcatenateExpression(Expression<?> left, Expression<?> right) {
    super(left, right);
  }

  @Override
  public String evaluate(PebbleTemplateImpl self, EvaluationContextImpl context) {

    Object left = getLeftExpression().evaluate(self, context);
    Object right = getRightExpression().evaluate(self, context);
    StringBuilder result = new StringBuilder();
    if (left != null) {
      result.append(left.toString());
    }
    if (right != null) {
      result.append(right.toString());
    }

    return result.toString();
  }

  @Override
  public String toString() {
    return String.format("%s + %s", getLeftExpression(), getRightExpression());
  }

}
