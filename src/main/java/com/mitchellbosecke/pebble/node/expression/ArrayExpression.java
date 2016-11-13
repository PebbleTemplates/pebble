/*******************************************************************************
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.node.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

public class ArrayExpression implements Expression<List<?>> {

    private final List<Expression<?>> values;
    private final int lineNumber;

    public ArrayExpression(int lineNumber) {
        this.values = Collections.emptyList();
        this.lineNumber = lineNumber;
    }

    public ArrayExpression(List<Expression<?>> values, int lineNumber) {
        if (values == null) {
            this.values = Collections.emptyList();
        } else {
            this.values = values;
        }
        this.lineNumber = lineNumber;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public List<?> evaluate(PebbleTemplateImpl self, EvaluationContextImpl context) throws PebbleException {
        List<Object> returnValues = new ArrayList<>(values.size());
        for (int i = 0; i < values.size(); i++) {
            Expression<?> expr = values.get(i);
            Object value = expr == null ? null : expr.evaluate(self, context);
            returnValues.add(value);
        }
        return returnValues;
    }

    @Override
    public int getLineNumber() {
        return this.lineNumber;
    }

}
