/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.node.expression;

import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

public class OrExpression extends BinaryExpression<Boolean> {

    @SuppressWarnings("unchecked")
    @Override
    public Boolean evaluate(PebbleTemplateImpl self, EvaluationContext context) {
        Expression<Boolean> left = (Expression<Boolean>) getLeftExpression();
        Expression<Boolean> right = (Expression<Boolean>) getRightExpression();
        return left.evaluate(self, context) || right.evaluate(self, context);
    }
}
