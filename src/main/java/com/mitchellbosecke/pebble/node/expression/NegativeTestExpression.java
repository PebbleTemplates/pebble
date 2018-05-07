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

public class NegativeTestExpression extends PositiveTestExpression {

    @Override
    public Object evaluate(PebbleTemplateImpl self, EvaluationContext context) {
        return !((Boolean) super.evaluate(self, context));
    }
}
