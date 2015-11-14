package com.mitchellbosecke.pebble.node.expression;

import java.util.ArrayList;
import java.util.List;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

/**
 * Expression which implements the range function.
 *
 * @author Eric Bussieres
 *
 */
public class RangeExpression extends BinaryExpression<Object> {

    @Override
    public Object evaluate(PebbleTemplateImpl self, EvaluationContext context) throws PebbleException {

        Object left = getLeftExpression().evaluate(self, context);
        Object right = getRightExpression().evaluate(self, context);
        
        Long leftNum = null;
        if (left instanceof Long) {
        	leftNum = (Long) left;
        }
        else {
        	throw new IllegalArgumentException("Left operand in range function must be a long " + left);
        }
        
        Long rightNum = null;
        if (right instanceof Long) {
        	rightNum = (Long) right;
        }
        else {
        	throw new IllegalArgumentException("Right operand in range function must be a long " + right);
        }
        
        List<Long> result = new ArrayList<>();
        for (Long i = leftNum; i <= rightNum; i++) {
        	result.add(i);
        }

        return result;
    }

}