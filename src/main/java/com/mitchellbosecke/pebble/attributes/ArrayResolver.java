package com.mitchellbosecke.pebble.attributes;

import com.mitchellbosecke.pebble.error.AttributeNotFoundException;

import java.lang.reflect.Array;

public class ArrayResolver implements AttributeResolver {
  static final ArrayResolver INSTANCE = new ArrayResolver();

  private ArrayResolver() {
  }

  @Override
  public ResolvedAttribute resolve(Object instance,
                                   Object attributeNameValue,
                                   Object[] argumentValues,
                                   boolean isStrictVariables,
                                   String filename,
                                   int lineNumber) {
    String attributeName = String.valueOf(attributeNameValue);
    int index = this.getIndex(attributeName);
    int length = Array.getLength(instance);
    if (index < 0 || index >= length) {
      if (isStrictVariables) {
        throw new AttributeNotFoundException(null,
                "Index out of bounds while accessing array with strict variables on.",
                attributeName, lineNumber, filename);
      } else {
        return () -> null;
      }
    }
    return () -> Array.get(instance, index);
  }

  private int getIndex(String attributeName) {
    try {
      return Integer.parseInt(attributeName);
    } catch (NumberFormatException e) {
      return -1;
    }
  }
}
