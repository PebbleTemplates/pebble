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
    return this.lazyLength.intValue() - this.value - 1;
  }
}
