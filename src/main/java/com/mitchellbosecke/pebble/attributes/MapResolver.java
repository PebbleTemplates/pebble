package com.mitchellbosecke.pebble.attributes;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.node.ArgumentsNode;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;

import java.util.Map;

public class MapResolver implements AttributeResolver {
  static final MapResolver INSTANCE = new MapResolver();

  private MapResolver() {
  }

  @Override
  public ResolvedAttribute resolve(Object instance,
                                   Object attributeNameValue,
                                   Object[] argumentValues,
                                   ArgumentsNode args,
                                   EvaluationContextImpl context,
                                   String filename,
                                   int lineNumber) {
    Map<?, ?> object = (Map<?, ?>) instance;
    if (object.isEmpty()) {
      return null;
    }
    if (attributeNameValue != null && Number.class.isAssignableFrom(attributeNameValue.getClass())) {
      Number keyAsNumber = (Number) attributeNameValue;

      Class<?> keyClass = object.keySet().iterator().next().getClass();
      Object key = this.cast(keyAsNumber, keyClass, filename, lineNumber);
      return new ResolvedAttribute(object.get(key));
    }
    return new ResolvedAttribute(object.get(attributeNameValue));
  }

  private Object cast(Number number,
                      Class<?> desiredType,
                      String filename,
                      int lineNumber) {
    if (desiredType == Long.class) {
      return number.longValue();
    } else if (desiredType == Integer.class) {
      return number.intValue();
    } else if (desiredType == Double.class) {
      return number.doubleValue();
    } else if (desiredType == Float.class) {
      return number.floatValue();
    } else if (desiredType == Short.class) {
      return number.shortValue();
    } else if (desiredType == Byte.class) {
      return number.byteValue();
    }
    throw new PebbleException(null, String.format("type %s not supported for key %s", desiredType, number), lineNumber, filename);
  }
}
