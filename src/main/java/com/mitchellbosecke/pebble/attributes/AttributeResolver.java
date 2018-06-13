package com.mitchellbosecke.pebble.attributes;

import com.mitchellbosecke.pebble.node.ArgumentsNode;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;

public interface AttributeResolver {

  ResolvedAttribute resolve(Object instance,
      Object attributeNameValue,
      Object[] argumentValues,
      ArgumentsNode args,
      EvaluationContextImpl context,
      String filename,
      int lineNumber);
}
