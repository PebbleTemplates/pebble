package com.mitchellbosecke.pebble.extension.writer;

import java.math.BigDecimal;

/**
 * A special type to be implemented by ${@link java.io.Writer}s so Pebble can bypass ${@link
 * Number}s String allocation and directly write primitives.
 */
public interface SpecializedWriter {

  void writeSpecialized(int i);

  void writeSpecialized(long l);

  void writeSpecialized(double d);

  void writeSpecialized(float f);

  void writeSpecialized(short s);

  void writeSpecialized(byte b);

  void writeSpecialized(char c);

  void writeSpecialized(String s);

  default void write(Object o) {
    if (o == null) {
      throw new IllegalArgumentException("Var can not be null");
    } else if (o instanceof String) {
      writeSpecialized((String) o);
    } else if (o instanceof Integer) {
      writeSpecialized(((Integer) o).intValue());
    } else if (o instanceof Long) {
      writeSpecialized(((Long) o).longValue());
    } else if (o instanceof Double) {
      writeSpecialized(((Double) o).doubleValue());
    } else if (o instanceof Float) {
      writeSpecialized(((Float) o).floatValue());
    } else if (o instanceof Short) {
      writeSpecialized(((Short) o).shortValue());
    } else if (o instanceof Byte) {
      writeSpecialized(((Byte) o).byteValue());
    } else if (o instanceof Character) {
      writeSpecialized(((Character) o).charValue());
    } else if (o instanceof BigDecimal) {
      writeSpecialized(((BigDecimal) o).toPlainString());
    } else {
      writeSpecialized(o.toString());
    }
  }
}
