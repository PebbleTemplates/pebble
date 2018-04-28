package com.mitchellbosecke.pebble.attributes;

public class DefaultAttributeResolver implements AttributeResolver {
  private final AttributeResolver mapResolver = new MapResolver();
  private final AttributeResolver arrayResolver = new ArrayResolver();
  private final AttributeResolver listResolver = new ListResolver();
  private final MemberResolver memberResolver = new MemberResolver();

  @Override
  public ResolvedAttribute resolve(Object instance,
                                   Object attributeNameValue,
                                   Object[] argumentValues,
                                   boolean isStrictVariables,
                                   String filename,
                                   int lineNumber) {
    if (instance != null) {
      ResolvedAttribute resolved;
      if (this.memberResolver.getMember(instance, String.valueOf(attributeNameValue)) != null) {
        resolved = this.memberResolver.resolve(instance, attributeNameValue, argumentValues, isStrictVariables, filename, lineNumber);
        if (resolved != null) {
          return resolved;
        }
      }

      resolved = this.mapResolver.resolve(instance, attributeNameValue, argumentValues, isStrictVariables, filename, lineNumber);
      if (resolved != null) {
        return resolved;
      }

      resolved = this.arrayResolver.resolve(instance, attributeNameValue, argumentValues, isStrictVariables, filename, lineNumber);
      if (resolved != null) {
        return resolved;
      }

      resolved = this.listResolver.resolve(instance, attributeNameValue, argumentValues, isStrictVariables, filename, lineNumber);
      if (resolved != null) {
        return resolved;
      }

      resolved = this.memberResolver.resolve(instance, attributeNameValue, argumentValues, isStrictVariables, filename, lineNumber);
      if (resolved != null) {
        return resolved;
      }
    }
    return null;
  }
}
