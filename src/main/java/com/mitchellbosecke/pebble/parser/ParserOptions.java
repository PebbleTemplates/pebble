package com.mitchellbosecke.pebble.parser;

/**
 * Parser options.
 *
 * @author yanxiyue
 */
public class ParserOptions {

  private boolean literalDecimalTreatedAsInteger;

  public boolean isLiteralDecimalTreatedAsInteger() {
    return literalDecimalTreatedAsInteger;
  }

  public ParserOptions setLiteralDecimalTreatedAsInteger(boolean literalDecimalTreatedAsInteger) {
    this.literalDecimalTreatedAsInteger = literalDecimalTreatedAsInteger;
    return this;
  }
}
