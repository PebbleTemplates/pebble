package io.pebbletemplates.pebble.utils;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TypeUtilsTest {

  @Test
  void compatibleCastPreservesNullAndAssignableValues() {
    Object marker = new Object();
    assertSame(marker, TypeUtils.compatibleCast(marker, Object.class));
    assertNull(TypeUtils.compatibleCast((Object) null, Integer.class));
    Long value = 7L;
    assertSame(value, TypeUtils.compatibleCast((Object) value, (Class<?>) null));
    assertSame(marker, TypeUtils.compatibleCast(new Object[]{marker}, (Class<?>[]) null)[0]);
    assertNull(TypeUtils.compatibleCast((Object[]) null, new Class<?>[]{Integer.class}));
  }

  @Test
  void convertsNumbersToPrimitiveAndBigNumberTypes() {
    assertEquals(12, TypeUtils.compatibleCast(12L, int.class));
    assertEquals(12L, TypeUtils.compatibleCast(12, long.class));
    assertEquals(12.5d, TypeUtils.compatibleCast(12.5f, double.class));
    assertEquals(12.5f, TypeUtils.compatibleCast(12.5d, float.class));
    assertEquals((byte) 12, TypeUtils.compatibleCast(12L, byte.class));
    assertEquals((short) 12, TypeUtils.compatibleCast(12L, short.class));
    assertEquals(BigInteger.valueOf(12), TypeUtils.compatibleCast(12, BigInteger.class));
    assertEquals(BigDecimal.valueOf(12.5d),
        TypeUtils.compatibleCast(12.5d, BigDecimal.class));
    assertEquals(new Date(12), TypeUtils.compatibleCast(12L, Date.class));
  }

  @Test
  void convertsNumericAndStringTruthValuesToBoolean() {
    assertFalse(TypeUtils.compatibleCast(0, Boolean.class));
    assertTrue(TypeUtils.compatibleCast(-1, Boolean.class));
    assertFalse(TypeUtils.compatibleCast(Double.NaN, Boolean.class));
    assertFalse(TypeUtils.compatibleCast("", Boolean.class));
    assertTrue(TypeUtils.compatibleCast("false", Boolean.class));
  }

  @Test
  void rejectsOutOfRangeIntegralNarrowingIncludingLargeBigIntegers() {
    assertThrows(IllegalArgumentException.class,
        () -> TypeUtils.compatibleCast(Integer.MAX_VALUE + 1L, int.class));
    assertThrows(IllegalArgumentException.class,
        () -> TypeUtils.compatibleCast(128, byte.class));
    assertThrows(IllegalArgumentException.class,
        () -> TypeUtils.compatibleCast(Short.MIN_VALUE - 1, short.class));
    assertThrows(IllegalArgumentException.class,
        () -> TypeUtils.compatibleCast(BigInteger.ONE.shiftLeft(80), int.class));
  }

  @Test
  void convertsArgumentArraysAndLeavesUnsupportedValuesUnchanged() {
    Object[] converted = TypeUtils.compatibleCast(
        new Object[]{5L, null, "value"}, new Class<?>[]{int.class, String.class, Integer.class});

    assertEquals(5, converted[0]);
    assertNull(converted[1]);
    assertEquals("value", converted[2]);
    assertInstanceOf(String.class,
        TypeUtils.compatibleCast("value", Integer.class));
  }
}
