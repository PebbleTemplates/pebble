/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.node.expression;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

public class AndExpression extends BinaryExpression<Boolean> {

    @SuppressWarnings("unchecked")
    @Override
    public Boolean evaluate(PebbleTemplateImpl self, EvaluationContextImpl context) throws PebbleException {
        Boolean left = ((Expression<Boolean>) getLeftExpression()).evaluate(self, context);
        Boolean right = ((Expression<Boolean>) getRightExpression()).evaluate(self, context);
        if(context.isStrictVariables()){
            if (left == null || right == null)
                throw new PebbleException(null, "null value used in and operator and strict variables is set to true", getLineNumber(), self.getName());
        } else {
            if (left == null) {
                left = false;
            }
            if (right == null) {
                right = false;
            }
        }
        return left && right;
    }
}
