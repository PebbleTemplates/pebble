package com.mitchellbosecke.pebble.attributes;

import com.google.common.base.Optional;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.DynamicAttributeProvider;

// check if the object is able to provide the attribute dynamically
public class DynamicAttributeProviderResolver implements AttributeResolver {

    @Override
    public Optional<ResolvedAttribute> resolve(Object instance, final Object attribute, final Object[] argumentValues,
            boolean isStrictVariables, String filename, int lineNumber) throws PebbleException {
        if(instance instanceof DynamicAttributeProvider) {
            final DynamicAttributeProvider dynamicAttributeProvider = (DynamicAttributeProvider) instance;
            if(dynamicAttributeProvider.canProvideDynamicAttribute(attribute)) {
                return Optional.<ResolvedAttribute>of(new ResolvedAttribute() {
                    
                    @Override
                    public Object get() throws PebbleException {
                        return dynamicAttributeProvider.getDynamicAttribute(attribute, argumentValues);
                    }
                });
            }
        }
        
        return Optional.absent();
    }

}
