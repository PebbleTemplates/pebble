package com.mitchellbosecke.pebble.node.fornode;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

public class LazyLength extends Number {

  private final Object iterableEvaluation;
  private int value = -1;

  public LazyLength(Object iterableEvaluation) {
    this.iterableEvaluation = iterableEvaluation;
  }

  @Override
  public int intValue() {
    return this.getValue();
  }

  @Override
  public long longValue() {
    return (long) this.getValue();
  }

  @Override
  public float floatValue() {
    return (float) this.getValue();
  }

  @Override
  public double doubleValue() {
    return (double) this.getValue();
  }

  @Override
  public String toString() {
    return String.valueOf(this.getValue());
  }

  private int getValue() {
    if (this.value == -1) {
      this.value = this.getIteratorSize(this.iterableEvaluation);
    }
    return this.value;
  }

  private int getIteratorSize(Object iterable) {
    if (iterable == null) {
      return 0;
    }
    if (iterable instanceof Collection) {
      return ((Collection<?>) iterable).size();
    } else if (iterable instanceof Map) {
      return ((Map<?, ?>) iterable).size();
    } else if (iterable.getClass().isArray()) {
      return Array.getLength(iterable);
    } else if (iterable instanceof Enumeration) {
      Enumeration<?> enumeration = (Enumeration<?>) iterable;
      int size = 0;
      while (enumeration.hasMoreElements()) {
        size++;
        enumeration.nextElement();
      }
      return size;
    }

    // assumed to be of type Iterator
    Iterator<?> it = ((Iterable<?>) iterable).iterator();
    int size = 0;
    while (it.hasNext()) {
      size++;
      it.next();
    }
    return size;
  }
}
