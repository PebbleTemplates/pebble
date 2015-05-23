/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.node.expression;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

public class MapExpression implements Expression<Map<?, ?>> {

    // FIXME should keys be of any type?
    private final Map<Expression<?>, Expression<?>> entries;

    public MapExpression() {
        this.entries = Collections.emptyMap();
    }

    public MapExpression(Map<Expression<?>, Expression<?>> entries) {
        if (entries == null) {
            this.entries = Collections.emptyMap();
        } else {
            this.entries = entries;
        }
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Map<?, ?> evaluate(PebbleTemplateImpl self, EvaluationContext context) throws PebbleException {
        Map<Object, Object> returnEntries = new HashMap<>(Long.valueOf(Math.round(Math.ceil(entries.size() / 0.75)))
                .intValue());
        for (Entry<Expression<?>, Expression<?>> entry : entries.entrySet()) {
            Expression<?> keyExpr = entry.getKey();
            Expression<?> valueExpr = entry.getValue();
            Object key = keyExpr == null ? null : keyExpr.evaluate(self, context);
            Object value = valueExpr == null ? null : valueExpr.evaluate(self, context);
            returnEntries.put(key, value);
        }
        return returnEntries;
    }

}
