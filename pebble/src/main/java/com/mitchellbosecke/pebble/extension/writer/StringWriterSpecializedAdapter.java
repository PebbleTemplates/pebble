package com.mitchellbosecke.pebble.extension.writer;

import java.io.StringWriter;

/**
 * A ${@link SpecializedWriter} that wraps a ${@link StringWriter}. Directly write numbers into the
 * underlying ${@link StringBuffer} and save String allocations (compared to ${@link
 * java.io.Writer}).
 */
public class StringWriterSpecializedAdapter implements SpecializedWriter {

  private final StringBuffer buff;

  public StringWriterSpecializedAdapter(StringWriter sw) {
    this.buff = sw.getBuffer();
  }

  @Override
  public void writeSpecialized(int i) {
    buff.append(i);
  }

  @Override
  public void writeSpecialized(long l) {
    buff.append(l);
  }

  @Override
  public void writeSpecialized(double d) {
    buff.append(d);
  }

  @Override
  public void writeSpecialized(float f) {
    buff.append(f);
  }

  @Override
  public void writeSpecialized(short s) {
    buff.append(s);
  }

  @Override
  public void writeSpecialized(byte b) {
    buff.append(b);
  }

  @Override
  public void writeSpecialized(char i) {
    buff.append(i);
  }

  @Override
  public void writeSpecialized(String s) {
    buff.append(s);
  }
}
