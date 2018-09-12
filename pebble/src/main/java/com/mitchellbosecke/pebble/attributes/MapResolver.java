package com.mitchellbosecke.pebble.attributes;

import com.mitchellbosecke.pebble.error.AttributeNotFoundException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.node.ArgumentsNode;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;
import java.util.Map;

class MapResolver implements AttributeResolver {

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
      return new ResolvedAttribute(null);
    }

    ResolvedAttribute resolvedAttribute;
    if (attributeNameValue != null && Number.class
        .isAssignableFrom(attributeNameValue.getClass())) {
      Number keyAsNumber = (Number) attributeNameValue;

      Class<?> keyClass = object.keySet().iterator().next().getClass();
      Object key = this.cast(keyAsNumber, keyClass, filename, lineNumber);
      resolvedAttribute = new ResolvedAttribute(object.get(key));
    } else {
      resolvedAttribute = new ResolvedAttribute(object.get(attributeNameValue));
    }

    if (context.isStrictVariables() && resolvedAttribute.evaluatedValue == null) {
      throw new AttributeNotFoundException(null, String.format(
          "Attribute [%s] of [%s] does not exist or can not be accessed and strict variables is set to true.",
          attributeNameValue.toString(), object.getClass().getName()),
          attributeNameValue.toString(), lineNumber, filename);
    }

    return resolvedAttribute;
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
    throw new PebbleException(null,
        String.format("type %s not supported for key %s", desiredType, number), lineNumber,
        filename);
  }
}
