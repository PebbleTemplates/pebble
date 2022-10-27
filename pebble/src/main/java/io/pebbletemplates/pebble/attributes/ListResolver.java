package io.pebbletemplates.pebble.attributes;

import io.pebbletemplates.pebble.error.AttributeNotFoundException;
import io.pebbletemplates.pebble.node.ArgumentsNode;
import io.pebbletemplates.pebble.template.EvaluationContextImpl;

import java.util.List;

class ListResolver implements AttributeResolver {

  static final ListResolver INSTANCE = new ListResolver();

  private ListResolver() {
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

    @SuppressWarnings("unchecked") List<Object> list = (List<Object>) instance;

    int index;
    try {
      index = Integer.parseInt(attributeName);
    } catch (NumberFormatException e) {
      return null;
    }
    int length = list.size();

    if (index < 0 || index >= length) {
      if (context.isStrictVariables()) {
        throw new AttributeNotFoundException(null,
            "Index out of bounds while accessing array with strict variables on.",
            attributeName, lineNumber, filename);
      } else {
        return new ResolvedAttribute(null);
      }
    }

    return new ResolvedAttribute(list.get(index));
  }
}
