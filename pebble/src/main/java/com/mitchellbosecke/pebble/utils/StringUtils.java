package com.mitchellbosecke.pebble.utils;

import java.math.BigDecimal;

public class StringUtils {

  public static String ltrim(String input) {
    int i = 0;
    while (i < input.length() && Character.isWhitespace(input.charAt(i))) {
      i++;
    }
    return input.substring(i);
  }

  public static String rtrim(String input) {
    int i = input.length() - 1;
    while (i >= 0 && Character.isWhitespace(input.charAt(i))) {
      i--;
    }
    return input.substring(0, i + 1);
  }

  /**
   * Converts non-null objects into strings. It will use the toString() method of most objects but
   * handles some known exceptions.
   */
  public static String toString(Object var) {
    if (var == null) {
      throw new IllegalArgumentException("Var can not be null");
    }
    if (var instanceof BigDecimal) {
      return ((BigDecimal) var).toPlainString();
    } else {
      return var.toString();
    }
  }
}
