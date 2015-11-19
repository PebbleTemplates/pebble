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
        BigDecimal num1 = evaluateAsNumber(op1);
        BigDecimal num2 = evaluateAsNumber(op2);
        
        if (num1 == null || num2 == null) {
            throw new RuntimeException(String.format("invalid operands for mathematical operation [%s][%s][%s]",
                    operation.toString(), op1, op2));
        }

        BigDecimal result = null;
        switch (operation) {
        case ADD:
            result =  num1.add(num2);
            break;
        case SUBTRACT:
            result =  num1.subtract(num2);
            break;
        case MULTIPLICATION:
            result =  num1.multiply(num2, MathContext.DECIMAL128);
            break;
        case DIVISION:
            result =  num1.divide(num2, MathContext.DECIMAL128);
            break;
        case MODULUS:
            result =  num1.remainder(num2, MathContext.DECIMAL128);
            break;
        default:
            throw new RuntimeException("Bug in OperatorUtils in pebble library");
        }
        
        result = result.stripTrailingZeros();
        
        //For compatibility issue, we cast as before to the type specified
        if (op1 instanceof BigDecimal || op2 instanceof BigDecimal) {
            return result;
        }            
        if (op1 instanceof Double || op2 instanceof Double) {
            return result.doubleValue();
        }
        if (op1 instanceof Float || op2 instanceof Float) {
            return result.floatValue();
        }
        if (op1 instanceof BigInteger || op2 instanceof BigInteger) {
            return result.toBigInteger();
        }
        if (op1 instanceof Long || op2 instanceof Long) {
            return result.longValue();
        }
        if (op1 instanceof Integer || op2 instanceof Integer) {
            return result.intValue();
        }
        if (op1 instanceof Short || op2 instanceof Short) {
            return result.shortValue();
        }
        if (op1 instanceof Byte || op2 instanceof Byte) {
            return result.byteValue();
        }
        throw new IllegalArgumentException("in OperatorUtils in pebble library");
    }
    
    private static boolean wideningConversionBinaryComparison(Object op1, Object op2, Comparison comparison) {
        BigDecimal num1 = evaluateAsNumber(op1);
        BigDecimal num2 = evaluateAsNumber(op2);
        
        if (num1 == null || num2 == null) {
            return false;
        }

        switch (comparison) {
        case GREATER_THAN:
            return num1.compareTo(num2) > 0;
        case GREATER_THAN_EQUALS:
            return num1.compareTo(num2) >= 0;
        case LESS_THAN:
            return num1.compareTo(num2) < 0;
        case LESS_THAN_EQUALS:
            return num1.compareTo(num2) <= 0;
        case EQUALS:
            return num1.compareTo(num2) == 0;
        default:
            throw new RuntimeException("Bug in OperatorUtils in pebble library");
        }
    }
    
    private static BigDecimal evaluateAsNumber(Object object) {
        if (object == null) {
            return null;
        }

        if (object instanceof Number) {
            if (object instanceof BigDecimal) {
                return (BigDecimal) object;
            }
            else {
                return BigDecimal.valueOf(((Number) object).doubleValue());
            }
        }
        else if (object instanceof String) {
            try {
                return new BigDecimal(((String) object).trim());
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException(String.format("invalid operands for mathematical comparison [%s]",
                        object));
            }
        }

        return null;
    }
}
