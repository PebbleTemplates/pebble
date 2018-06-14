package com.mitchellbosecke.pebble.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * A small utility class to handle type operation.
 *
 * @author yanxiyue
 */
public class TypeUtils {

  public static Object[] compatibleCast(Object[] argumentValues, Class<?>[] parameterTypes) {
    if (argumentValues == null || parameterTypes == null) {
      return argumentValues;
    }

    Object[] result = new Object[argumentValues.length];
    for (int i = 0; i < result.length; i++) {
      result[i] = compatibleCast(argumentValues[i], parameterTypes[i]);
    }

    return result;
  }

  public static Object compatibleCast(Object value, Class<?> type) {
    if (value == null || type == null || type.isAssignableFrom(value.getClass())) {
      return value;
    }
    if (value instanceof Number) {
      Number number = (Number) value;
      if (type == byte.class || type == Byte.class) {
        return number.byteValue();
      }
      if (type == short.class || type == Short.class) {
        return number.shortValue();
      }
      if (type == int.class || type == Integer.class) {
        return number.intValue();
      }
      if (type == long.class || type == Long.class) {
        return number.longValue();
      }
      if (type == float.class || type == Float.class) {
        return number.floatValue();
      }
      if (type == double.class || type == Double.class) {
        return number.doubleValue();
      }
      if (type == BigInteger.class) {
        return BigInteger.valueOf(number.longValue());
      }
      if (type == BigDecimal.class) {
        return BigDecimal.valueOf(number.doubleValue());
      }
      if (type == Date.class) {
        return new Date(number.longValue());
      }
    }
    return value;
  }
}
