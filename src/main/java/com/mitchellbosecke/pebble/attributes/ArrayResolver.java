package com.mitchellbosecke.pebble.attributes;

import java.lang.reflect.Array;

import com.google.common.base.Optional;
import com.mitchellbosecke.pebble.error.AttributeNotFoundException;
import com.mitchellbosecke.pebble.error.PebbleException;

public class ArrayResolver implements AttributeResolver {

    @Override
    public Optional<ResolvedAttribute> resolve(final Object instance, Object attribute, Object[] argumentValues,
            boolean isStrictVariables, String filename, int lineNumber) throws PebbleException {
        if (argumentValues==null) {
            if (instance.getClass().isArray()) {
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
                            return Optional.<ResolvedAttribute>of(new ResolvedAttribute() {
                                @Override
                                public Object evaluate() throws PebbleException {
                                    return null;
                                }
                            });
                        }
                    }
                    return Optional.<ResolvedAttribute>of(new ResolvedAttribute() {
                        @Override
                        public Object evaluate() throws PebbleException {
                            return Array.get(instance, index);
                        }
                    });
                }
            }
        }
        return Optional.absent();
    }

    public static Optional<Integer> asIndex(String attributeName) {
        try {
            return Optional.of(Integer.parseInt(attributeName));
        } catch (NumberFormatException nx) {
            
        }
        return Optional.absent();
    }
}
