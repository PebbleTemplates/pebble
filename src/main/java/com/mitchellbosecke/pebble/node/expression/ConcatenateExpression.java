package com.mitchellbosecke.pebble.node.expression;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

/**
 * Expression which implements the string concatenation.
 *
 * @author Thomas Hunziker
 *
 */
public class ConcatenateExpression extends BinaryExpression<Object> {

    @Override
    public String evaluate(PebbleTemplateImpl self, EvaluationContext context) throws PebbleException {

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

}
