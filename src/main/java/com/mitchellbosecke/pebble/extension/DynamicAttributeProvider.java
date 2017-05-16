package com.mitchellbosecke.pebble.extension;

/**
 * When an object implements this interface, it tells
 * the expression parser that it is able to provide
 * dynamic attributes.
 */
public interface DynamicAttributeProvider {
    
    /**
     * Returns <code>true</code> if the attribute can be
     * provided given the specified name.
     */
    boolean canProvideDynamicAttribute(Object attributeName);
    
    /**
     * Returns the attribute given the specified name and arguments.
     */
    Object getDynamicAttribute(Object attributeName, Object[] argumentValues);

}
