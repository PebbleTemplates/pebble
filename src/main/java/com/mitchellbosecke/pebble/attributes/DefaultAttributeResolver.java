package com.mitchellbosecke.pebble.attributes;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.Optional.empty;

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
                                             int lineNumber) {
    if (instance != null) {
      return this.resolvers.stream()
              .map(resolver -> resolver.resolve(instance, attribute, argumentValues, isStrictVariables, filename, lineNumber))
              .filter(Optional::isPresent)
              .findFirst()
              .orElse(empty());
    }
    return empty();
  }
}
