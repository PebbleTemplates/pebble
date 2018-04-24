package com.mitchellbosecke.pebble.attributes;

import com.mitchellbosecke.pebble.error.AttributeNotFoundException;
import com.mitchellbosecke.pebble.error.PebbleException;

import java.lang.reflect.Array;
import java.util.Optional;

import static java.util.Optional.empty;

public class ArrayResolver implements AttributeResolver {

  @Override
  public Optional<ResolvedAttribute> resolve(Object instance,
                                             Object attribute,
                                             Object[] argumentValues,
                                             boolean isStrictVariables,
                                             String filename,
                                             int lineNumber) throws PebbleException {
    if (argumentValues == null && instance.getClass().isArray()) {
      String attributeName = String.valueOf(attribute);
      Optional<Integer> optIndex = asIndex(attributeName);
      if (optIndex.isPresent()) {
        final int index = optIndex.get();
        int length = Array.getLength(instance);
        if (index < 0 || index >= length) {
          if (isStrictVariables) {
            throw new AttributeNotFoundException(null,
                "Index out of bounds while accessing array with strict variables on.",
                attributeName, lineNumber, filename);
          } else {
            return Optional.of(() -> null);
          }
        }
        return Optional.of(() -> Array.get(instance, index));
      }
    }
    return empty();
  }

  public static Optional<Integer> asIndex(String attributeName) {
    try {
      return Optional.of(Integer.parseInt(attributeName));
    } catch (NumberFormatException nx) {

    }
    return empty();
  }
}
