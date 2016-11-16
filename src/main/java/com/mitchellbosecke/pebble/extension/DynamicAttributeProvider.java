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
    public boolean canProvideDynamicAttribute(Object attributeName);
    
    /**
     * Returns the attribute associated to the specified key
     * or <code>null</code> if not found.
     */
    public Object getDynamicAttribute(Object attributeName, Object[] argumentValues);

}
