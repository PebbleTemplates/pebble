package com.mitchellbosecke.pebble.attributes;

import java.util.Map;

import com.google.common.base.Optional;
import com.mitchellbosecke.pebble.error.PebbleException;

public class MapResolver implements AttributeResolver {

    @Override
    public Optional<ResolvedAttribute> resolve(final Object instance, final Object attribute, Object[] argumentValues,
            boolean isStrictVariables, final String filename, final int lineNumber) throws PebbleException {
        if (argumentValues==null) {
            if (instance instanceof Map) {
                return Optional.<ResolvedAttribute>of(new ResolvedAttribute() {
                    
                    @Override
                    public Object evaluate() throws PebbleException {
                        return getObjectFromMap((Map<?, ?>) instance, attribute, filename, lineNumber);
                    }
                });
            }
        }
        return Optional.absent();
    }

    private static Object getObjectFromMap(Map<?, ?> object, Object attributeNameValue, String filename, int lineNumber) throws PebbleException {
        if (object.isEmpty()) {
            return null;
        }
        if (Number.class.isAssignableFrom(attributeNameValue.getClass())) {
            Number keyAsNumber = (Number) attributeNameValue;
    
            Class<?> keyClass = object.keySet().iterator().next().getClass();
            Object key = cast(keyAsNumber, keyClass, filename, lineNumber);
            return object.get(key);
        }
        return object.get(attributeNameValue);
    }
    
    private static Object cast(Number number, Class<?> desiredType, String filename, int lineNumber) throws PebbleException {
        if (desiredType == Long.class) {
            return number.longValue();
        } else if (desiredType == Integer.class) {
            return number.intValue();
        } else if (desiredType == Double.class) {
            return number.doubleValue();
        } else if (desiredType == Float.class) {
            return number.floatValue();
        } else if (desiredType == Short.class) {
            return number.shortValue();
        } else if (desiredType == Byte.class) {
            return number.byteValue();
        }
        throw new PebbleException(null, String.format("type %s not supported for key %s", desiredType, number), lineNumber, filename);
    }
}
