package io.pebbletemplates.pebble.node.expression;

import io.pebbletemplates.pebble.extension.core.RangeFunction;
import io.pebbletemplates.pebble.node.ArgumentsNode;
import io.pebbletemplates.pebble.node.PositionalArgumentNode;
import io.pebbletemplates.pebble.template.EvaluationContextImpl;
import io.pebbletemplates.pebble.template.PebbleTemplateImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Expression which implements the range function.
 *
 * @author Eric Bussieres
 */
public class RangeExpression extends BinaryExpression<Object> {

  @Override
  public Object evaluate(PebbleTemplateImpl self, EvaluationContextImpl context) {
    List<PositionalArgumentNode> positionalArgs = new ArrayList<>();
    positionalArgs.add(new PositionalArgumentNode(getLeftExpression()));
    positionalArgs.add(new PositionalArgumentNode(getRightExpression()));

    ArgumentsNode arguments = new ArgumentsNode(positionalArgs, null, this.getLineNumber());
    FunctionOrMacroInvocationExpression function = new FunctionOrMacroInvocationExpression(
        RangeFunction.FUNCTION_NAME, arguments, this.getLineNumber());

    return function.evaluate(self, context);
  }

}