package io.pebbletemplates.pebble.parser;

/**
 * Parser options.
 *
 * @author yanxiyue
 */
public class ParserOptions {

  private boolean literalDecimalTreatedAsInteger;

  private boolean literalNumbersAsBigDecimals;

  public boolean isLiteralDecimalTreatedAsInteger() {
    return literalDecimalTreatedAsInteger;
  }

  public ParserOptions setLiteralDecimalTreatedAsInteger(boolean literalDecimalTreatedAsInteger) {
    this.literalDecimalTreatedAsInteger = literalDecimalTreatedAsInteger;
    return this;
  }

  public boolean isLiteralNumbersAsBigDecimals() {
    return literalNumbersAsBigDecimals;
  }

  public ParserOptions setLiteralNumbersAsBigDecimals(boolean literalNumbersAsBigDecimals) {
    this.literalNumbersAsBigDecimals = literalNumbersAsBigDecimals;
    return this;
  }


}
