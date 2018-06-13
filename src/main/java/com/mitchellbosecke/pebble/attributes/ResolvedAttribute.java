package com.mitchellbosecke.pebble.attributes;

public final class ResolvedAttribute {

  public final Object evaluatedValue;

  public ResolvedAttribute(Object evaluatedValue) {
    this.evaluatedValue = evaluatedValue;
  }
}
