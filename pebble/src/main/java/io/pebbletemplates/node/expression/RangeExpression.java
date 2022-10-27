package io.pebbletemplates.node.expression;

import io.pebbletemplates.extension.core.RangeFunction;
import io.pebbletemplates.node.ArgumentsNode;
import io.pebbletemplates.node.PositionalArgumentNode;
import io.pebbletemplates.template.EvaluationContextImpl;
import io.pebbletemplates.template.PebbleTemplateImpl;

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