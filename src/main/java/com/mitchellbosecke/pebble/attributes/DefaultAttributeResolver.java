package com.mitchellbosecke.pebble.attributes;

import com.mitchellbosecke.pebble.error.PebbleException;

import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

public class DefaultAttributeResolver implements AttributeResolver {

    private static final DefaultAttributeResolver INSTANCE = new DefaultAttributeResolver();
    
    private final Iterable<AttributeResolver> resolvers = unmodifiableList(asList(
        new MapResolver(),
        new ArrayResolver(),
        new ListResolver(),
        new DynamicAttributeProviderResolver(),
        new MemberResolver()));

    private DefaultAttributeResolver() {
        
    }
    
    @Override
    public Optional<ResolvedAttribute> resolve(Object instance, Object attribute, Object[] argumentValues,
            boolean isStrictVariables, String filename, int lineNumber) throws PebbleException {
        return resolveWith(resolvers, instance, attribute, argumentValues, isStrictVariables, filename, lineNumber);
    }

    public static Optional<ResolvedAttribute> resolve(Iterable<? extends AttributeResolver> customResolver, Object instance, Object attribute, Object[] argumentValues,
            boolean isStrictVariables, String filename, int lineNumber) throws PebbleException {
        Optional<ResolvedAttribute> resolved = resolveWith(customResolver, instance, attribute, argumentValues, isStrictVariables, filename, lineNumber);
        if (resolved.isPresent()) {
            return resolved;
        };
        return INSTANCE.resolve(instance, attribute, argumentValues, isStrictVariables, filename, lineNumber);
    }

    private static Optional<ResolvedAttribute> resolveWith(Iterable<? extends AttributeResolver> list, Object instance,
            Object attribute, Object[] argumentValues, boolean isStrictVariables, String filename, int lineNumber)
            throws PebbleException {
        if (instance!=null) {
            for (AttributeResolver resolver : list) {
                Optional<ResolvedAttribute> resolved = resolver.resolve(instance, attribute, argumentValues, isStrictVariables, filename, lineNumber);
                if (resolved.isPresent()) {
                    return resolved;
                }
            }
        }
        return Optional.empty();
    }
}
