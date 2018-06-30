/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.utils;

public class StringLengthComparator implements java.util.Comparator<String> {

  public static StringLengthComparator INSTANCE = new StringLengthComparator();

  private StringLengthComparator() {
  }

  public int compare(String s1, String s2) {
    return s2.length() - s1.length();
  }
}
