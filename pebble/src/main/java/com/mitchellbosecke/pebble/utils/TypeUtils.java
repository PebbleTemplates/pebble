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

  @SuppressWarnings("unchecked")
  public static <T> T compatibleCast(Object value, Class<T> type) {
    if (value == null || type == null || type.isAssignableFrom(value.getClass())) {
      return (T) value;
    }
    if (value instanceof Number) {
      Number number = (Number) value;
      if (type == int.class || type == Integer.class) {
        return (T) (Integer) number.intValue();
      }
      if (type == long.class || type == Long.class) {
        return (T) (Long) number.longValue();
      }
      if (type == double.class || type == Double.class) {
        return (T) (Double) number.doubleValue();
      }
      if (type == float.class || type == Float.class) {
        return (T) (Float) number.floatValue();
      }
      if (type == byte.class || type == Byte.class) {
        return (T) (Byte) number.byteValue();
      }
      if (type == short.class || type == Short.class) {
        return (T) (Short) number.shortValue();
      }
      if (type == BigInteger.class) {
        return (T) BigInteger.valueOf(number.longValue());
      }
      if (type == BigDecimal.class) {
        return (T) BigDecimal.valueOf(number.doubleValue());
      }
      if (type == Date.class) {
        return (T) new Date(number.longValue());
      }
      if (type == Boolean.class) {
        return (T) (Boolean) (number.doubleValue() != 0.0);
      }
    }
    if (value instanceof String) {
      String str = (String) value;
      if (type == Boolean.class) {
        return (T) (Boolean) !str.isEmpty();
      }
    }
    return (T) value;
  }
}
