package com.mitchellbosecke.pebble.node.expression;

import java.util.ArrayList;
import java.util.List;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.core.RangeFunction;
import com.mitchellbosecke.pebble.node.ArgumentsNode;
import com.mitchellbosecke.pebble.node.PositionalArgumentNode;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

/**
 * Expression which implements the range function.
 *
 * @author Eric Bussieres
 *
 */
public class RangeExpression extends BinaryExpression<Object> {

    @Override
    public Object evaluate(PebbleTemplateImpl self, EvaluationContextImpl context) throws PebbleException {
        List<PositionalArgumentNode> positionalArgs = new ArrayList<>();
        positionalArgs.add(new PositionalArgumentNode(getLeftExpression()));
        positionalArgs.add(new PositionalArgumentNode(getRightExpression()));

        ArgumentsNode arguments = new ArgumentsNode(positionalArgs, null, this.getLineNumber());
        FunctionOrMacroInvocationExpression function = new FunctionOrMacroInvocationExpression(
                RangeFunction.FUNCTION_NAME, arguments, this.getLineNumber());

        return function.evaluate(self, context);
    }

}