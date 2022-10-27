package io.pebbletemplates.pebble.attributes;

import io.pebbletemplates.pebble.error.AttributeNotFoundException;
import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.node.ArgumentsNode;
import io.pebbletemplates.pebble.template.EvaluationContextImpl;

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
    if (object.isEmpty() && !context.isStrictVariables()) {
      return new ResolvedAttribute(null);
    }

    Object key;
    if (attributeNameValue != null && Number.class
        .isAssignableFrom(attributeNameValue.getClass())) {
      Number keyAsNumber = (Number) attributeNameValue;

      Class<?> keyClass = object.keySet().iterator().next().getClass();
      key = this.cast(keyAsNumber, keyClass, filename, lineNumber);
    } else {
      key = attributeNameValue;
    }

    if(context.isStrictVariables() && !object.containsKey(key)) {
      throw new AttributeNotFoundException(null, String.format(
          "Attribute [%s] of [%s] does not exist or can not be accessed and strict variables is set to true.",
          attributeNameValue.toString(), object.getClass().getName()),
          attributeNameValue.toString(), lineNumber, filename);
    }

    ResolvedAttribute resolvedAttribute = new ResolvedAttribute(object.get(key));
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
