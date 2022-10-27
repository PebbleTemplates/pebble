package io.pebbletemplates.pebble.attributes;

import io.pebbletemplates.pebble.node.ArgumentsNode;
import io.pebbletemplates.pebble.template.EvaluationContextImpl;

public interface AttributeResolver {

  ResolvedAttribute resolve(Object instance,
      Object attributeNameValue,
      Object[] argumentValues,
      ArgumentsNode args,
      EvaluationContextImpl context,
      String filename,
      int lineNumber);
}
