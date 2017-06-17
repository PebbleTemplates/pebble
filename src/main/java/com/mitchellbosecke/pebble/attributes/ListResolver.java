package com.mitchellbosecke.pebble.attributes;

import java.util.List;

import com.google.common.base.Optional;
import com.mitchellbosecke.pebble.error.AttributeNotFoundException;
import com.mitchellbosecke.pebble.error.PebbleException;

public class ListResolver implements AttributeResolver {

    @Override
    public Optional<ResolvedAttribute> resolve(Object instance, Object attribute, Object[] argumentValues,
            boolean isStrictVariables, String filename, int lineNumber) throws PebbleException {
        if (argumentValues==null) {
            if (instance instanceof List) {
                String attributeName = String.valueOf(attribute);
    
                @SuppressWarnings("unchecked")
                final List<Object> list = (List<Object>) instance;
    
                Optional<Integer> optIndex =ArrayResolver.asIndex(attributeName);
                if (optIndex.isPresent()) {
                    final int index = optIndex.get();
                    int length = list.size();
        
                    if (index < 0 || index >= length) {
                        if (isStrictVariables) {
                            throw new AttributeNotFoundException(null,
                                    "Index out of bounds while accessing array with strict variables on.",
                                    attributeName, lineNumber, filename);
                        } else {
                            return Optional.<ResolvedAttribute>of(new ResolvedAttribute() {
                                @Override
                                public Object get() throws PebbleException {
                                    return null;
                                }
                            });
                        }
                    }
        
                    return Optional.<ResolvedAttribute>of(new ResolvedAttribute() {
                        @Override
                        public Object get() throws PebbleException {
                            return list.get(index);
                        }
                    });
                }
            }
        }
        return Optional.absent();
    }

}
