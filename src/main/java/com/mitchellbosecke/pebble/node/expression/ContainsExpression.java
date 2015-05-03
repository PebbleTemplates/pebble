/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.node.expression;

import java.util.Collection;
import java.util.Map;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

public class ContainsExpression extends BinaryExpression<Boolean> {

    @SuppressWarnings({"rawtypes","unchecked"})
	@Override
    public Boolean evaluate(PebbleTemplateImpl self, EvaluationContext context) throws PebbleException {
    	Object leftValue = getLeftExpression().evaluate(self, context);
    	if (leftValue == null) return Boolean.FALSE;
    	Object rightValue = getRightExpression().evaluate(self, context);

    	if (leftValue instanceof Collection) {
    		if (rightValue instanceof Collection) {
    			return Boolean.valueOf(((Collection)leftValue).containsAll((Collection)rightValue));
    		} else {
    			return Boolean.valueOf(((Collection)leftValue).contains(rightValue));
    		}
    	} else if (leftValue instanceof Map) {
    		return Boolean.valueOf(((Map)leftValue).containsKey(rightValue));
    	} else if (leftValue.getClass().isArray()) {
    		//TODO support for arrays
    		//Arrays.
    		throw new UnsupportedOperationException("Contains operator does not yet implement arrays");
    	} else {
    		throw new IllegalArgumentException("Contains operator can only be used on Collections, Maps and arrays. Actual type was: " + leftValue.getClass().getName());
    	}
    }

}
