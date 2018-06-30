package com.mitchellbosecke.pebble.node.fornode;

public class LazyRevIndex extends Number {
  private final int value;
  private final LazyLength lazyLength;

  public LazyRevIndex(int value, LazyLength lazyLength) {
    this.value = value;
    this.lazyLength = lazyLength;
  }

  @Override
  public int intValue() {
    return getValue();
  }

  @Override
  public long longValue() {
    return (long) getValue();
  }

  @Override
  public float floatValue() {
    return (float) getValue();
  }

  @Override
  public double doubleValue() {
    return (double) getValue();
  }

  @Override
  public String toString() {
    return String.valueOf(getValue());
  }

  private int getValue() {
    return lazyLength.intValue() - this.value - 1;
  }
}
