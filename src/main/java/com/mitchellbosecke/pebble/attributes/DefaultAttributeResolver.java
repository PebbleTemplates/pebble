package com.mitchellbosecke.pebble.attributes;

import com.mitchellbosecke.pebble.error.PebbleException;

import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

public class DefaultAttributeResolver implements AttributeResolver {
  private final Iterable<AttributeResolver> resolvers = unmodifiableList(asList(
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
    return Optional.empty();
  }
}
