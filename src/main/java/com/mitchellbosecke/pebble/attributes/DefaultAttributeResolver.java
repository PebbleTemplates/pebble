package com.mitchellbosecke.pebble.attributes;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import com.mitchellbosecke.pebble.error.PebbleException;

public class DefaultAttributeResolver implements AttributeResolver {

    private static final DefaultAttributeResolver INSTANCE = new DefaultAttributeResolver();
    
    private final Iterable<AttributeResolver> resolvers=Collections.unmodifiableList(Arrays.asList( 
            new DynamicAttributeProviderResolver(),
            /*
             * If, and only if, no arguments were provided does it make sense to
             * check maps/arrays/lists
             */
            new MapResolver(),
            new ArrayResolver(),
            new ListResolver()
            
//            .add(new MemberResolver())
    ));
    
    private DefaultAttributeResolver() {
        
    }
    
    @Override
    public Optional<ResolvedAttribute> resolve(Object instance, Object attribute, Object[] argumentValues,
            boolean isStrictVariables, String filename, int lineNumber) throws PebbleException {
        if (instance!=null) {
            for (AttributeResolver resolver : resolvers) {
                Optional<ResolvedAttribute> resolved = resolver.resolve(instance, attribute, argumentValues, isStrictVariables, filename, lineNumber);
                if (resolved.isPresent()) {
                    return resolved;
                }
            }
        }
        return Optional.empty();
    }
    
    
    public static DefaultAttributeResolver getInstance() {
        return INSTANCE;
    }

}
