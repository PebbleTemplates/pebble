/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.utils;

/**
 * 
 * This class acts as a sort of wrapper around Java's built in operators. This
 * is necessary because Pebble treats all user provided variables as Objects
 * even if they were originally primitives.
 * <p>
 * It's important that this class mimics the natural type conversion that Java
 * will apply when performing operators. This can be found in section 5.6.2 of
 * the Java 7 spec, under Binary Numeric Promotion.
 * 
 * @author Mitchell
 * 
 */
public class OperatorUtils {

    private enum Operation {
        ADD, SUBTRACT, MULTIPLICATION, DIVISION, MODULUS, GREATER_THAN, GREATER_THAN_EQUALS, LESS_THAN, LESS_THAN_EQUALS, EQUALS
    };

    public static Object add(Object op1, Object op2) {
        if (op1 instanceof String || op2 instanceof String) {
            return concatenateStrings(String.valueOf(op1), String.valueOf(op2));
        }
        return wideningConversionBinaryOperation(op1, op2, Operation.ADD);
    }

    public static Object subtract(Object op1, Object op2) {
        return wideningConversionBinaryOperation(op1, op2, Operation.SUBTRACT);
    }

    public static Object multiply(Object op1, Object op2) {
        return wideningConversionBinaryOperation(op1, op2, Operation.MULTIPLICATION);
    }

    public static Object divide(Object op1, Object op2) {
        return wideningConversionBinaryOperation(op1, op2, Operation.DIVISION);
    }

    public static Object mod(Object op1, Object op2) {
        return wideningConversionBinaryOperation(op1, op2, Operation.MODULUS);
    }

    public static boolean equals(Object op1, Object op2) {
        if (op1 != null && op1 instanceof Number && op2 != null && op2 instanceof Number) {
            return (boolean) wideningConversionBinaryOperation(op1, op2, Operation.EQUALS);
        } else {
            return ((op1 == op2) || ((op1 != null) && op1.equals(op2)));
        }
    }

    public static boolean gt(Object op1, Object op2) {
        return (boolean) wideningConversionBinaryOperation(op1, op2, Operation.GREATER_THAN);
    }

    public static boolean gte(Object op1, Object op2) {
        return (boolean) wideningConversionBinaryOperation(op1, op2, Operation.GREATER_THAN_EQUALS);
    }

    public static boolean lt(Object op1, Object op2) {
        return (boolean) wideningConversionBinaryOperation(op1, op2, Operation.LESS_THAN);
    }

    public static boolean lte(Object op1, Object op2) {
        return (boolean) wideningConversionBinaryOperation(op1, op2, Operation.LESS_THAN_EQUALS);
    }

    public static Object unaryPlus(Object op1) {
        return multiply(1, op1);
    }

    public static Object unaryMinus(Object op1) {
        return multiply(-1, op1);
    }

    private static Object concatenateStrings(String op1, String op2) {
        return op1 + op2;
    }

    private static Object wideningConversionBinaryOperation(Object op1, Object op2, Operation operation) {

        if (!(op1 instanceof Number) || !(op2 instanceof Number)) {
            throw new RuntimeException("invalid operands for mathematical operator [+]");
        }

        if (op1 instanceof Double || op2 instanceof Double) {
            return doubleOperation(((Number) op1).doubleValue(), ((Number) op2).doubleValue(), operation);
        }

        if (op1 instanceof Float || op2 instanceof Float) {
            return floatOperation(((Number) op1).floatValue(), ((Number) op2).floatValue(), operation);
        }

        if (op1 instanceof Long || op2 instanceof Long) {
            return longOperation(((Number) op1).longValue(), ((Number) op2).longValue(), operation);
        }

        return integerOperation(((Number) op1).intValue(), ((Number) op2).intValue(), operation);
    }

    private static Object doubleOperation(Double op1, Double op2, Operation operation) {
        switch (operation) {
        case ADD:
            return op1 + op2;
        case SUBTRACT:
            return op1 - op2;
        case MULTIPLICATION:
            return op1 * op2;
        case DIVISION:
            return op1 / op2;
        case MODULUS:
            return op1 % op2;
        case GREATER_THAN:
            return op1 > op2;
        case GREATER_THAN_EQUALS:
            return op1 >= op2;
        case LESS_THAN:
            return op1 < op2;
        case LESS_THAN_EQUALS:
            return op1 <= op2;
        case EQUALS:
            return op1 == op2;
        default:
            return null;
        }
    }

    private static Object floatOperation(Float op1, Float op2, Operation operation) {
        switch (operation) {
        case ADD:
            return op1 + op2;
        case SUBTRACT:
            return op1 - op2;
        case MULTIPLICATION:
            return op1 * op2;
        case DIVISION:
            return op1 / op2;
        case MODULUS:
            return op1 % op2;
        case GREATER_THAN:
            return op1 > op2;
        case GREATER_THAN_EQUALS:
            return op1 >= op2;
        case LESS_THAN:
            return op1 < op2;
        case LESS_THAN_EQUALS:
            return op1 <= op2;
        case EQUALS:
            return op1 == op2;
        default:
            return null;
        }
    }

    private static Object longOperation(Long op1, Long op2, Operation operation) {
        switch (operation) {
        case ADD:
            return op1 + op2;
        case SUBTRACT:
            return op1 - op2;
        case MULTIPLICATION:
            return op1 * op2;
        case DIVISION:
            return op1 / op2;
        case MODULUS:
            return op1 % op2;
        case GREATER_THAN:
            return op1 > op2;
        case GREATER_THAN_EQUALS:
            return op1 >= op2;
        case LESS_THAN:
            return op1 < op2;
        case LESS_THAN_EQUALS:
            return op1 <= op2;
        case EQUALS:
            return op1 == op2;
        default:
            return null;
        }
    }

    private static Object integerOperation(Integer op1, Integer op2, Operation operation) {
        switch (operation) {
        case ADD:
            return op1 + op2;
        case SUBTRACT:
            return op1 - op2;
        case MULTIPLICATION:
            return op1 * op2;
        case DIVISION:
            return op1 / op2;
        case MODULUS:
            return op1 % op2;
        case GREATER_THAN:
            return op1 > op2;
        case GREATER_THAN_EQUALS:
            return op1 >= op2;
        case LESS_THAN:
            return op1 < op2;
        case LESS_THAN_EQUALS:
            return op1 <= op2;
        case EQUALS:
            return op1 == op2;
        default:
            return null;
        }
    }

}
