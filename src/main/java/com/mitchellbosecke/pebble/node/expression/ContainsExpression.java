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

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Boolean evaluate(PebbleTemplateImpl self, EvaluationContext context) throws PebbleException {
        Object leftValue = getLeftExpression().evaluate(self, context);

        if (leftValue == null) {
            return false;
        }
        Object rightValue = getRightExpression().evaluate(self, context);

        if (leftValue instanceof Collection) {
            if (rightValue instanceof Collection) {
                return Boolean.valueOf(((Collection) leftValue).containsAll((Collection) rightValue));
            } else {
                return Boolean.valueOf(((Collection) leftValue).contains(rightValue));
            }
        } else if (leftValue instanceof Map) {
            return Boolean.valueOf(((Map) leftValue).containsKey(rightValue));
        } else if (leftValue.getClass().isArray()) {
            return arrayContains(leftValue, rightValue);
        } else if (leftValue instanceof String) {
            return String.valueOf(leftValue).contains(String.valueOf(rightValue));
        } else {
            throw new PebbleException(null,
                    "Contains operator can only be used on Collections, Maps and arrays. Actual type was: "
                            + leftValue.getClass().getName(), this.getLineNumber(), self.getName());
        }
    }

    // FIXME is this right? does it make sense to support?
    private static boolean arrayContains(Object input, Object value) {
        if (input instanceof Object[]) {
            return containsObject((Object[]) input, value);
        } else if (input instanceof boolean[]) {
            return containsBoolean((boolean[]) input, value);
        } else if (input instanceof byte[]) {
            return containsByte((byte[]) input, value);
        } else if (input instanceof char[]) {
            return containsChar((char[]) input, value);
        } else if (input instanceof double[]) {
            return containsDouble((double[]) input, value);
        } else if (input instanceof float[]) {
            return containsFloat((float[]) input, value);
        } else if (input instanceof int[]) {
            return containsInt((int[]) input, value);
        } else if (input instanceof long[]) {
            return containsLong((long[]) input, value);
        } else {
            return containsShort((short[]) input, value);
        }
    }

    private static boolean containsObject(Object[] array, Object value) {
        for (Object o : array) {
            if (value == o || (value != null && value.equals(o)))
                return true;
        }
        return false;
    }

    private static boolean containsBoolean(boolean[] array, Object value) {
        if (!(value instanceof Boolean))
            return false;
        for (boolean b : array) {
            if (b == ((Boolean) value).booleanValue())
                return true;
        }
        return false;
    }

    private static boolean containsByte(byte[] array, Object value) {
        if (!(value instanceof Byte))
            return false;
        for (byte b : array) {
            if (b == ((Byte) value).byteValue())
                return true;
        }
        return false;
    }

    private static boolean containsChar(char[] array, Object value) {
        if (!(value instanceof Character))
            return false;
        for (char c : array) {
            if (c == ((Character) value).charValue())
                return true;
        }
        return false;
    }

    private static boolean containsDouble(double[] array, Object value) {
        if (!(value instanceof Double))
            return false;
        for (double d : array) {
            if (d == ((Double) value).doubleValue())
                return true;
        }
        return false;
    }

    private static boolean containsFloat(float[] array, Object value) {
        if (!(value instanceof Float))
            return false;
        for (float f : array) {
            if (f == ((Float) value).floatValue())
                return true;
        }
        return false;
    }

    private static boolean containsInt(int[] array, Object value) {
        if (!(value instanceof Integer))
            return false;
        for (int i : array) {
            if (i == ((Integer) value).intValue())
                return true;
        }
        return false;
    }

    private static boolean containsLong(long[] array, Object value) {
        if (!(value instanceof Long))
            return false;
        for (long l : array) {
            if (l == ((Long) value).longValue())
                return true;
        }
        return false;
    }

    private static boolean containsShort(short[] array, Object value) {
        if (!(value instanceof Short))
            return false;
        for (short s : array) {
            if (s == ((Short) value).shortValue())
                return true;
        }
        return false;
    }

}
