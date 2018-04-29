package com.mitchellbosecke.pebble.attributes;

import com.mitchellbosecke.pebble.template.EvaluationContextImpl;

public interface AttributeResolver {
  ResolvedAttribute resolve(Object instance,
                            Object attributeNameValue,
                            Object[] argumentValues,
                            EvaluationContextImpl context,
                            String filename,
                            int lineNumber);
}
