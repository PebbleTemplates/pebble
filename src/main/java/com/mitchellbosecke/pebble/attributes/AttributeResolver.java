package com.mitchellbosecke.pebble.attributes;

import java.util.Optional;

public interface AttributeResolver {
  Optional<ResolvedAttribute> resolve(Object instance,
                                      Object attribute,
                                      Object[] argumentValues,
                                      boolean isStrictVariables,
                                      String filename,
                                      int lineNumber);
}
