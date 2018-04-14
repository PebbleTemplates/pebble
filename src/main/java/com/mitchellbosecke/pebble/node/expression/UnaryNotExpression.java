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

public class UnaryNotExpression extends UnaryExpression {

    @Override
    public Object evaluate(PebbleTemplateImpl self, EvaluationContextImpl context) throws PebbleException {
        Boolean result = (Boolean) getChildExpression().evaluate(self, context);
        if (context.isStrictVariables()){
            if(result == null)
                throw new PebbleException(null, "null value given to not() and strict variables is set to true", getLineNumber(), self.getName());
            return !result;
        } else {
            return result == null || !result;
        }
    }

}
