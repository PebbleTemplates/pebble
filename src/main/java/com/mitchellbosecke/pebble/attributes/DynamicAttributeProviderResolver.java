package com.mitchellbosecke.pebble.attributes;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.DynamicAttributeProvider;

import java.util.Optional;

public class DynamicAttributeProviderResolver implements AttributeResolver {

  @Override
  public Optional<ResolvedAttribute> resolve(Object instance,
                                             Object attribute,
                                             Object[] argumentValues,
                                             boolean isStrictVariables,
                                             String filename,
                                             int lineNumber) throws PebbleException {
    if (instance instanceof DynamicAttributeProvider) {
      DynamicAttributeProvider dynamicAttributeProvider = (DynamicAttributeProvider) instance;
      if (dynamicAttributeProvider.canProvideDynamicAttribute(attribute)) {
        return Optional.of(() -> dynamicAttributeProvider.getDynamicAttribute(attribute, argumentValues));
      }
    }

    return Optional.empty();
  }

}
