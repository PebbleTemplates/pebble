package com.mitchellbosecke.pebble.attributes;

import com.mitchellbosecke.pebble.extension.DynamicAttributeProvider;

public class DynamicAttributeProviderResolver implements AttributeResolver {

  @Override
  public ResolvedAttribute resolve(Object instance,
                                   Object attributeNameValue,
                                   Object[] argumentValues,
                                   boolean isStrictVariables,
                                   String filename,
                                   int lineNumber) {
    if (instance instanceof DynamicAttributeProvider) {
      DynamicAttributeProvider dynamicAttributeProvider = (DynamicAttributeProvider) instance;
      if (dynamicAttributeProvider.canProvideDynamicAttribute(attributeNameValue)) {
        return () -> dynamicAttributeProvider.getDynamicAttribute(attributeNameValue, argumentValues);
      }
    }

    return null;
  }

}
