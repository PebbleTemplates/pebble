package com.mitchellbosecke.pebble.attributes;

import com.mitchellbosecke.pebble.error.AttributeNotFoundException;
import com.mitchellbosecke.pebble.error.PebbleException;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

public class DefaultAttributeResolver implements AttributeResolver {
  private final List<AttributeResolver> resolvers = unmodifiableList(asList(
      new MapResolver(),
      new ArrayResolver(),
      new ListResolver(),
      new DynamicAttributeProviderResolver(),
      new MemberResolver()));

  @Override
  public Optional<ResolvedAttribute> resolve(Object instance,
                                             Object attribute,
                                             Object[] argumentValues,
                                             boolean isStrictVariables,
                                             String filename,
                                             int lineNumber) throws PebbleException {
    if (instance != null) {
      for (AttributeResolver resolver : this.resolvers) {
        Optional<ResolvedAttribute> resolved = resolver.resolve(instance, attribute, argumentValues, isStrictVariables, filename, lineNumber);
        if (resolved.isPresent()) {
          return resolved;
        }
      }
    }

    if (isStrictVariables) {
      String attributeName = String.valueOf(attribute);
      throw new AttributeNotFoundException(null, String.format(
          "Attribute [%s] of [%s] does not exist or can not be accessed and strict variables is set to true.",
          attributeName,
          instance != null ? instance.getClass().getName(): null),
          attributeName,
          lineNumber,
          filename);
    }
    return Optional.empty();
  }
}
