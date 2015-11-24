/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Collection;
import java.util.List;

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
        ADD, SUBTRACT, MULTIPLICATION, DIVISION, MODULUS
    }

    private enum Comparison {
        GREATER_THAN, GREATER_THAN_EQUALS, LESS_THAN, LESS_THAN_EQUALS, EQUALS
    }

    public static Object add(Object op1, Object op2) {
        if (op1 instanceof String || op2 instanceof String) {
            return concatenateStrings(String.valueOf(op1), String.valueOf(op2));
        } else if (op1 instanceof List) {
            return addToList((List<?>) op1, op2);
        }
        return wideningConversionBinaryOperation(op1, op2, Operation.ADD);
    }

    public static Object subtract(Object op1, Object op2) {
        if (op1 instanceof List) {
            return subtractFromList((List<?>) op1, op2);
        }
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
            return wideningConversionBinaryComparison(op1, op2, Comparison.EQUALS);
        } else {
            return ((op1 == op2) || ((op1 != null) && op1.equals(op2)));
        }
    }

    public static boolean gt(Object op1, Object op2) {
        return wideningConversionBinaryComparison(op1, op2, Comparison.GREATER_THAN);
    }

    public static boolean gte(Object op1, Object op2) {
        return wideningConversionBinaryComparison(op1, op2, Comparison.GREATER_THAN_EQUALS);
    }

    public static boolean lt(Object op1, Object op2) {
        return wideningConversionBinaryComparison(op1, op2, Comparison.LESS_THAN);
    }

    public static boolean lte(Object op1, Object op2) {
        return wideningConversionBinaryComparison(op1, op2, Comparison.LESS_THAN_EQUALS);
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

    /**
     * This is not a documented feature but we are leaving this in for now. I'm
     * unsure if there is demand for this feature.
     * 
     * @param op1
     * @param op2
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static Object addToList(List<?> op1, Object op2) {
        if (op2 instanceof Collection) {
            op1.addAll((Collection) op2);
        } else {
            ((List<Object>) op1).add(op2);
        }
        return op1;
    }

    /**
     * This is not a documented feature but we are leaving this in for now. I'm
     * unsure if there is demand for this feature.
     * 
     * @param op1
     * @param op2
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static Object subtractFromList(List<?> op1, Object op2) {
        if (op2 instanceof Collection) {
            op1.removeAll((Collection) op2);
        } else {
            ((List<Object>) op1).remove(op2);
        }
        return op1;
    }

    private static Object wideningConversionBinaryOperation(Object op1, Object op2, Operation operation) {
        // If operand are String, we try to parse it as a number
        op1 = evaluateAsNumber(op1);
        op2 = evaluateAsNumber(op2);
        
        if (!(op1 instanceof Number) || !(op2 instanceof Number)) {
            throw new RuntimeException(String.format("invalid operands for mathematical operation [%s]",
                    operation.toString()));
        }

        Number num1 = (Number) op1;
        Number num2 = (Number) op2;

        if (num1 instanceof BigDecimal || num2 instanceof BigDecimal) {
            return bigDecimalOperation(BigDecimal.valueOf(num1.doubleValue()), BigDecimal.valueOf(num2.doubleValue()), 
                    operation);
        }
        
        if (num1 instanceof Double || num2 instanceof Double) {
            return doubleOperation(num1.doubleValue(), num2.doubleValue(), operation);
        }

        if (num1 instanceof Float || num2 instanceof Float) {
            return floatOperation(num1.floatValue(), num2.floatValue(), operation);
        }
        
        return longOperation(num1.longValue(), num2.longValue(), operation);
    }

    private static boolean wideningConversionBinaryComparison(Object op1, Object op2, Comparison comparison) {
        if (op1 == null || op2 == null) {
            return false;
        }
        
        // If operand are String, we try to parse it as a number
        op1 = evaluateAsNumber(op1);
        op2 = evaluateAsNumber(op2);
 
        if (!(op1 instanceof Number) || !(op2 instanceof Number)) {
            throw new RuntimeException(String.format("invalid operands for mathematical comparison [%s]",
                    comparison.toString()));
        }

        Number num1 = (Number) op1;
        Number num2 = (Number) op2;

        return doubleComparison(num1.doubleValue(), num2.doubleValue(), comparison);
    }

    private static double doubleOperation(double op1, double op2, Operation operation) {
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
        default:
            throw new RuntimeException("Bug in OperatorUtils in pebble library");
        }
    }

    private static boolean doubleComparison(double op1, double op2, Comparison comparison) {
        switch (comparison) {
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
            throw new RuntimeException("Bug in OperatorUtils in pebble library");
        }
    }
    
    private static BigDecimal bigDecimalOperation(BigDecimal op1, BigDecimal op2, Operation operation) {
        switch (operation) {
        case ADD:
            return op1.add(op2);
        case SUBTRACT:
            return op1.subtract(op2);
        case MULTIPLICATION:
            return op1.multiply(op2, MathContext.DECIMAL128);
        case DIVISION:
            return op1.divide(op2, MathContext.DECIMAL128);
        case MODULUS:
            return op1.remainder(op2, MathContext.DECIMAL128);
        default:
            throw new RuntimeException("Bug in OperatorUtils in pebble library");
        }
    }
    
    private static Float floatOperation(Float op1, Float op2, Operation operation) {
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
        default:
            throw new RuntimeException("Bug in OperatorUtils in pebble library");
        }
    }

    private static long longOperation(long op1, long op2, Operation operation) {
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
        default:
            throw new RuntimeException("Bug in OperatorUtils in pebble library");
        }
    }
    
    private static Object evaluateAsNumber(Object op) {
        Object result = op;
        if (op != null && op instanceof String) {
            try {
                result = Double.parseDouble((String) op);
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException(String.format("invalid operands for mathematical comparison [%s]",
                        op));
            }
        }
        
        return result;
    }
} 
