package com.mitchellbosecke.pebble.attributes;

public interface AttributeResolver {
  ResolvedAttribute resolve(Object instance,
                            Object attributeNameValue,
                            Object[] argumentValues,
                            boolean isStrictVariables,
                            String filename,
                            int lineNumber);
}
