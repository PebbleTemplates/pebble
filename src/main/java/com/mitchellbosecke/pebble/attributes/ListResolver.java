package com.mitchellbosecke.pebble.attributes;

import com.mitchellbosecke.pebble.error.AttributeNotFoundException;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;

public class ListResolver implements AttributeResolver {

  @Override
  public Optional<ResolvedAttribute> resolve(Object instance,
                                             Object attribute,
                                             Object[] argumentValues,
                                             boolean isStrictVariables,
                                             String filename,
                                             int lineNumber) {
    if (argumentValues == null && instance instanceof List) {
      String attributeName = String.valueOf(attribute);

      @SuppressWarnings("unchecked") List<Object> list = (List<Object>) instance;

      Optional<Integer> optIndex = ArrayResolver.asIndex(attributeName);
      if (optIndex.isPresent()) {
        int index = optIndex.get();
        int length = list.size();

        if (index < 0 || index >= length) {
          if (isStrictVariables) {
            throw new AttributeNotFoundException(null,
                "Index out of bounds while accessing array with strict variables on.",
                attributeName, lineNumber, filename);
          } else {
            return Optional.of(() -> null);
          }
        }

        return Optional.of(() -> list.get(index));
      }
    }
    return empty();
  }

}
