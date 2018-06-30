package com.mitchellbosecke.pebble.attributes;

import com.mitchellbosecke.pebble.error.AttributeNotFoundException;
import com.mitchellbosecke.pebble.node.ArgumentsNode;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;
import java.lang.reflect.Array;

class ArrayResolver implements AttributeResolver {

  static final ArrayResolver INSTANCE = new ArrayResolver();

  private ArrayResolver() {
  }

  @Override
  public ResolvedAttribute resolve(Object instance,
      Object attributeNameValue,
      Object[] argumentValues,
      ArgumentsNode args,
      EvaluationContextImpl context,
      String filename,
      int lineNumber) {
    String attributeName = String.valueOf(attributeNameValue);
    int index = this.getIndex(attributeName);
    int length = Array.getLength(instance);
    if (index < 0 || index >= length) {
      if (context.isStrictVariables()) {
        throw new AttributeNotFoundException(null,
            "Index out of bounds while accessing array with strict variables on.",
            attributeName, lineNumber, filename);
      } else {
        return new ResolvedAttribute(null);
      }
    }
    return new ResolvedAttribute(Array.get(instance, index));
  }

  private int getIndex(String attributeName) {
    try {
      return Integer.parseInt(attributeName);
    } catch (NumberFormatException e) {
      return -1;
    }
  }
}
