package io.pebbletemplates.pebble.node.fornode;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LazyLengthTest {

  @Test
  void calculatesLengthForSupportedContainerTypes() {
    assertEquals(0, new LazyLength(null).intValue());
    assertEquals(3, new LazyLength(Arrays.asList("a", "b", "c")).intValue());

    Map<String, Integer> map = new HashMap<>();
    map.put("a", 1);
    map.put("b", 2);
    assertEquals(2, new LazyLength(map).intValue());
    assertEquals(4, new LazyLength(new int[]{1, 2, 3, 4}).intValue());
  }

  @Test
  void calculatesLengthForEnumerationAndIterableAndCachesIt() {
    Vector<String> values = new Vector<>(Arrays.asList("a", "b", "c"));
    Enumeration<String> enumeration = values.elements();
    LazyLength enumerationLength = new LazyLength(enumeration);
    assertEquals(3, enumerationLength.longValue());
    assertEquals(3.0f, enumerationLength.floatValue());
    assertEquals(3.0d, enumerationLength.doubleValue());
    assertEquals("3", enumerationLength.toString());
    assertEquals(3, enumerationLength.intValue());
    assertEquals(false, enumeration.hasMoreElements());

    List<String> iterableValues = Arrays.asList("a", "b");
    Iterable<String> iterable = () -> iterableValues.iterator();
    assertEquals(2, new LazyLength(iterable).intValue());
    assertEquals(0, new LazyLength(Collections.emptyList()).intValue());
  }
}
