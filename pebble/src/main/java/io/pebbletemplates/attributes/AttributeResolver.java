package io.pebbletemplates.attributes;

import io.pebbletemplates.node.ArgumentsNode;
import io.pebbletemplates.template.EvaluationContextImpl;

public interface AttributeResolver {

  ResolvedAttribute resolve(Object instance,
      Object attributeNameValue,
      Object[] argumentValues,
      ArgumentsNode args,
      EvaluationContextImpl context,
      String filename,
      int lineNumber);
}
